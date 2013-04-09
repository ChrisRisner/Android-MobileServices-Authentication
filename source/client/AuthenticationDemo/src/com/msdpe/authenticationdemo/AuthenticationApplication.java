package com.msdpe.authenticationdemo;

import com.google.gson.JsonObject;
import com.microsoft.windowsazure.mobileservices.TableJsonOperationCallback;

import android.app.Application;

public class AuthenticationApplication extends Application {
	private AuthService mAuthService;
	
	public AuthenticationApplication() {}
	
	public AuthService getAuthService() {
		if (mAuthService == null) {
			mAuthService = new AuthService(this);
		}
		return mAuthService;
	}	
}
