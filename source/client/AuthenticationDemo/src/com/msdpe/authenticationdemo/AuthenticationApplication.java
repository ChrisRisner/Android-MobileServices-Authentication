package com.msdpe.authenticationdemo;

import android.app.Activity;
import android.app.Application;

public class AuthenticationApplication extends Application {
	private AuthService mAuthService;
	private Activity mCurrentActivity;
	
	public AuthenticationApplication() {}
	
	public AuthService getAuthService() {
		if (mAuthService == null) {
			mAuthService = new AuthService(this);
		}
		return mAuthService;
	}	
	
	public void setCurrentActivity(Activity activity) {
		mCurrentActivity = activity;
	}
	
	public Activity getCurrentActivity() {
		return mCurrentActivity;
	}
}
