package com.msdpe.authenticationdemo;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.google.gson.JsonObject;
import com.microsoft.windowsazure.mobileservices.MobileServiceAuthenticationProvider;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceJsonTable;
import com.microsoft.windowsazure.mobileservices.MobileServiceUser;
import com.microsoft.windowsazure.mobileservices.TableJsonOperationCallback;
import com.microsoft.windowsazure.mobileservices.TableJsonQueryCallback;
import com.microsoft.windowsazure.mobileservices.UserAuthenticationCallback;

public class AuthService {
		private MobileServiceClient mClient;
		private MobileServiceJsonTable mTableAccounts;
		private MobileServiceJsonTable mTableAuthData;
		private Context mContext;
		private final String TAG = "AuthService";
	
		public AuthService(Context context) {
			mContext = context;
			try {
				mClient = new MobileServiceClient("https://myauthdemo.azure-mobile.net/", "HZatbbcDTUXflXkUFIlkcqeFxPMppl54", mContext);
				mTableAccounts = mClient.getTable("Accounts");
				mTableAuthData = mClient.getTable("AuthData");
			} catch (MalformedURLException e) {
				Log.e(TAG, "There was an error creating the Mobile Service.  Verify the URL");
			}
		}
		
		public void login(Context activityContext, MobileServiceAuthenticationProvider provider, UserAuthenticationCallback callback) {
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
					MobileServiceUser user = new MobileServiceUser(userId);
					user.setAuthenticationToken(token);
					mClient.setCurrentUser(user);	
					return true;
				}
			}
			return false;
		}

		/***
		 * Pulls the user ID and token out of a json object from the server
		 * @param jsonObject
		 */
		public void setUser(JsonObject jsonObject) {			
			String userId = jsonObject.getAsJsonPrimitive("userId").getAsString();
			String token = jsonObject.getAsJsonPrimitive("token").getAsString();			
			MobileServiceUser user = new MobileServiceUser(userId);
			user.setAuthenticationToken(token);
			mClient.setCurrentUser(user);		
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

		public void logout() {
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
			Intent logoutIntent = new Intent(mContext, AuthenticationActivity.class);
			logoutIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
			mContext.startActivity(logoutIntent);			
		}
		

}
