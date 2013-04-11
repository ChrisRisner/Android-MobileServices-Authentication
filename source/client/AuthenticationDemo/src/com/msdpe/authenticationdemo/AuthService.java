package com.msdpe.authenticationdemo;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.StatusLine;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.ProgressBar;

import com.google.gson.JsonObject;
import com.microsoft.windowsazure.mobileservices.MobileServiceAuthenticationProvider;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceJsonTable;
import com.microsoft.windowsazure.mobileservices.MobileServiceRequestType;
import com.microsoft.windowsazure.mobileservices.MobileServiceTableBase;
import com.microsoft.windowsazure.mobileservices.MobileServiceUser;
import com.microsoft.windowsazure.mobileservices.NextServiceFilterCallback;
import com.microsoft.windowsazure.mobileservices.ServiceFilter;
import com.microsoft.windowsazure.mobileservices.ServiceFilterRequest;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponseCallback;
import com.microsoft.windowsazure.mobileservices.TableJsonOperationCallback;
import com.microsoft.windowsazure.mobileservices.TableJsonQueryCallback;
import com.microsoft.windowsazure.mobileservices.UserAuthenticationCallback;

public class AuthService {
		private MobileServiceClient mClient;
		private MobileServiceJsonTable mTableAccounts;
		private MobileServiceJsonTable mTableAuthData;
		private MobileServiceJsonTable mTableBadAuth;
		private Context mContext;
		private Context mAppContext;
		private final String TAG = "AuthService";
		private boolean mShouldRetryAuth;
		private boolean mIsCustomAuthProvider = false;
		private MobileServiceAuthenticationProvider mProvider;
	
		public AuthService(Context context) {
			mContext = context;
			try {
				mClient = new MobileServiceClient("https://myauthdemo.azure-mobile.net/", 
						"HZatbbcDTUXflXkUFIlkcqeFxPMppl54", mContext)
						.withFilter(new MyServiceFilter());
				//TODO: this should be changed becuase we don't KNOW that mContext is the app context
				mAppContext = mContext;
				mTableAccounts = mClient.getTable("Accounts");
				mTableAuthData = mClient.getTable("AuthData");
				mTableBadAuth = mClient.getTable("BadAuth");
			} catch (MalformedURLException e) {
				Log.e(TAG, "There was an error creating the Mobile Service.  Verify the URL");
			}
		}
		
		public void login(Context activityContext, MobileServiceAuthenticationProvider provider, UserAuthenticationCallback callback) {
			mProvider = provider;
			mClient.setContext(activityContext);
			mClient.login(provider, callback);
		}
		
		public void setContext(Context context) {
			mClient.setContext(context);
		}
		
		public String getUserId() {
			return mClient.getCurrentUser().getUserId();
		}

		public void login(String username, String password, TableJsonOperationCallback callback) {
			JsonObject customUser = new JsonObject();
			customUser.addProperty("username", username);
			customUser.addProperty("password", password);
			
			List<Pair<String,String>> parameters = new ArrayList<Pair<String, String>>();
			parameters.add(new Pair<String, String>("login", "true"));
			
			mTableAccounts.insert(customUser, parameters, callback);			
		}
		
		public void getAuthData(TableJsonQueryCallback callback) {
			mTableAuthData.where().execute(callback);
		}
		
		public boolean isUserAuthenticated() {			
			SharedPreferences settings = mContext.getSharedPreferences("UserData", 0);
			if (settings != null) {
				String userId = settings.getString("userid", null);
				String token = settings.getString("token", null);
				if (userId != null && !userId.equals("")) {
					setUserData(userId, token);
					return true;
				}
			}
			return false;
		}
		
		public void setUserData(String userId, String token) {
			MobileServiceUser user = new MobileServiceUser(userId);
			user.setAuthenticationToken(token);
			mClient.setCurrentUser(user);	
			
			//Check for custom provider
			String provider = userId.substring(0, userId.indexOf(":"));
			
			if (provider.equals("Custom")) {
				mProvider = null;
				mIsCustomAuthProvider = true;
			} else if (provider.equals("Facebook"))
				mProvider = MobileServiceAuthenticationProvider.Facebook;	
			else if (provider.equals("Twitter"))
				mProvider = MobileServiceAuthenticationProvider.Twitter;
			else if (provider.equals("MicrosoftAccount"))
				mProvider = MobileServiceAuthenticationProvider.MicrosoftAccount;
			else if (provider.equals("Google"))
				mProvider = MobileServiceAuthenticationProvider.Google;
				
		}

		/***
		 * Pulls the user ID and token out of a json object from the server
		 * @param jsonObject
		 */
		public void setUserAndSaveData(JsonObject jsonObject) {			
			String userId = jsonObject.getAsJsonPrimitive("userId").getAsString();
			String token = jsonObject.getAsJsonPrimitive("token").getAsString();			
			setUserData(userId, token);	
			saveUserData();
		}
		
		public void saveUserData() {
			SharedPreferences settings = mContext.getSharedPreferences("UserData", 0);
	        SharedPreferences.Editor preferencesEditor = settings.edit();
	        preferencesEditor.putString("userid", mClient.getCurrentUser().getUserId());
	        preferencesEditor.putString("token", mClient.getCurrentUser().getAuthenticationToken());
	        preferencesEditor.commit();
		}

		public void registerUser(String username, String password, String confirm,
				String email,
				TableJsonOperationCallback callback) {
			JsonObject newUser = new JsonObject();
			newUser.addProperty("username", username);
			newUser.addProperty("password", password);
			newUser.addProperty("email", email);
			
			mTableAccounts.insert(newUser, callback);			
		}

		public void logout(boolean shouldRedirectToLogin) {
			//Clear the cookies so they won't auto login to a provider again
			CookieSyncManager.createInstance(mContext);
			CookieManager cookieManager = CookieManager.getInstance();
			cookieManager.removeAllCookie();
			
			//Clear the user id and token from the shared preferences
			SharedPreferences settings = mContext.getSharedPreferences("UserData", 0);
	        SharedPreferences.Editor preferencesEditor = settings.edit();
	        preferencesEditor.clear();
	        preferencesEditor.commit();
						
	        //Clear the user and return to the auth activity
			mClient.logout();
			
			if (shouldRedirectToLogin) {
				Intent logoutIntent = new Intent(mContext, AuthenticationActivity.class);
				logoutIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
				mContext.startActivity(logoutIntent);		
			}
		}
		
		public void testForced401(boolean shouldRetry,
				TableJsonOperationCallback callback) {
			
			JsonObject data = new JsonObject();
			data.addProperty("data", "data");
			mShouldRetryAuth = shouldRetry;
//			List<Pair<String,String>> parameters = new ArrayList<Pair<String, String>>();
//			parameters.add(new Pair<String, String>("login", "true"));
			
			mTableBadAuth.insert(data, callback);
			
		}
		
		private class MyServiceFilter implements ServiceFilter {
			
			@Override
			public void handleRequest(final ServiceFilterRequest request, final NextServiceFilterCallback nextServiceFilterCallback,
					final ServiceFilterResponseCallback responseCallback) {
				
				nextServiceFilterCallback.onNext(request, new ServiceFilterResponseCallback() {				
					@Override
					public void onResponse(ServiceFilterResponse response, Exception exception) {
						if (exception != null) {
							Log.e(TAG, "MyServiceFilter onResponse Exception: " + exception.getMessage());
						}
						StatusLine status = response.getStatus();
						int statusCode = status.getStatusCode();						
						if (statusCode == 401) {
							//Log the user out but don't send them to the login page
							logout(false);
							//If we shouldn't retry (or they've used custom auth), 
							//we're going to kick them out for now
							//If you're doing custom auth, you'd need to show your own
							//custom auth popup to login with
							if (mShouldRetryAuth && !mIsCustomAuthProvider) {
								//Get the current activity for the context so we can show the login dialog
								AuthenticationApplication myApp = (AuthenticationApplication) mContext;
								Activity currentActivity = myApp.getCurrentActivity();
								mClient.setContext(currentActivity);
								//Return a response to the caller (otherwise returning from this method to 
								//RequestAsyncTask will cause a crash).
								responseCallback.onResponse(response, exception);
								//Show the login dialog on the UI thread
								currentActivity.runOnUiThread(new Runnable() {
									@Override
									public void run() {
										mClient.login(mProvider, new UserAuthenticationCallback() {				
											@Override
											public void onCompleted(MobileServiceUser user, Exception exception,
													ServiceFilterResponse response) {
												if (exception == null) {
													//Save their updated user data locally
													saveUserData();
													//Pull out the previous request so we can retry it
													ServiceFilterRequest previousRequest = request.getPreviousRequest();
													//Update the requests X-ZUMO-AUTH header
													previousRequest.removeHeader("X-ZUMO-AUTH");
													previousRequest.addHeader("X-ZUMO-AUTH", mClient.getCurrentUser().getAuthenticationToken());

													//Add our BYPASS querystring parameter to the URL
													Uri.Builder uriBuilder = Uri.parse(previousRequest.getUrl()).buildUpon();
													uriBuilder.appendQueryParameter("bypass", "true");
													try {
														previousRequest.setUrl(uriBuilder.build().toString());
													} catch (URISyntaxException e) {
														Log.e(TAG, "Couldn't set request's new url: " + e.getMessage());
														e.printStackTrace();
													}
													
													//Call the appropriate method for the previous request type
													//This is important because they have different callback 
													//handlers (except insert/update)
													//String previousCalltype = request.getPreviousCalltype();
													MobileServiceTableBase previousTable = request.getPreviousRequestTable();
													switch (request.getPreviousCalltype()) {
													case INSERT:
														previousTable.executeInsertUpdateRequest(previousRequest, request.getPreviousCallback());
														break;
													case UPDATE:
														previousTable.executeInsertUpdateRequest(previousRequest, request.getPreviousCallback());
														break;
													case DELETE:
														previousTable.executeDeleteRequest(request.getPreviousDeleteCallback(), previousRequest);
														break;
													case GET: 
														previousTable.executeGetRequest(request.getPreviousQueryCallback(), previousRequest);
														break;
													}
//													if (previousCalltype.equals("INSERT")) {
//														previousTable.executeInsertUpdateRequest(previousRequest, request.getPreviousCallback());
//													} else if (previousCalltype.equals("UPDATE")) {
//														previousTable.executeInsertUpdateRequest(previousRequest, request.getPreviousCallback());
//													} else if (previousCalltype.equals("DELETE")) {
//														previousTable.executeDeleteRequest(request.getPreviousDeleteCallback(), previousRequest);
//													} else if (previousCalltype.equals("GET")) {
//														previousTable.executeGetRequest(request.getPreviousQueryCallback(), previousRequest);
//													}													
												} else {
													Log.e(TAG, "User did not login successfully after 401");
													//Kick user back to login screen
													logout(true);
												}
												
											}
										});									
									}										
								});									
							} else {
								//Log them out and proceed with the response
								logout(true);
								responseCallback.onResponse(response, exception);
							}							
						} else {//
							responseCallback.onResponse(response, exception);
						}
					}
				});
			}
		}
}
