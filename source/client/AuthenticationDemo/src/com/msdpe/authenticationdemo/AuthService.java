package com.msdpe.authenticationdemo;

import java.net.MalformedURLException;

import android.content.Context;
import android.util.Log;

import com.microsoft.windowsazure.mobileservices.MobileServiceAuthenticationProvider;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceJsonTable;
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

}
