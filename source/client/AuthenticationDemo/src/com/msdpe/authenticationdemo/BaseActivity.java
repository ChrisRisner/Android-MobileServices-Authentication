package com.msdpe.authenticationdemo;

import android.app.Activity;
import android.os.Bundle;

public class BaseActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		AuthenticationApplication myApp = (AuthenticationApplication) getApplication();
		myApp.setCurrentActivity(this);
	}
}
