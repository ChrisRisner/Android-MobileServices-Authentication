package com.msdpe.authenticationdemo;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.util.Pair;

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
			JsonObject newUser = new JsonObject();
			newUser.addProperty("username", username);
			newUser.addProperty("password", password);
			
			List<Pair<String,String>> parameters = new ArrayList<Pair<String, String>>();
			parameters.add(new Pair<String, String>("login", "true"));
			
			mTableAccounts.insert(newUser, parameters, callback);			
		}
		
		public void getAuthData(TableJsonQueryCallback callback) {
			mTableAuthData.where().execute(callback);
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
		}
		

}
