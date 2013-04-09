package com.msdpe.authenticationdemo;

import com.google.gson.JsonObject;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableJsonOperationCallback;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class CustomLoginActivity extends Activity {
	
	private final String TAG = "CustomLoginActivity";
	private Button mBtnCancel;
	private Button mBtnLogin;
	private Button mBtnRegisterForAccount;
	private EditText mTxtUsername;
	private EditText mTxtPassword;
	private Activity mActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_custom_login);
		
		mActivity = this;
		
		//Get UI objects
		mBtnCancel = (Button) findViewById(R.id.btnCancel);
		mBtnLogin = (Button) findViewById(R.id.btnLogin);
		mBtnRegisterForAccount = (Button) findViewById(R.id.btnRegisterForAccount);
		mTxtUsername = (EditText) findViewById(R.id.txtUsername);
		mTxtPassword = (EditText) findViewById(R.id.txtPassword);
		
		//Add on click listeners
		mBtnCancel.setOnClickListener(cancelClickListener);
		mBtnLogin.setOnClickListener(loginClickListener);
		mBtnRegisterForAccount.setOnClickListener(registerClickListener);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.custom_login, menu);
		return true;
	}
	
	View.OnClickListener cancelClickListener = new OnClickListener() {		
		@Override
		public void onClick(View v) {
			mActivity.finish();
		}
	};
	
	View.OnClickListener loginClickListener = new OnClickListener() {		
		@Override
		public void onClick(View v) {	
			AuthenticationApplication myApp = (AuthenticationApplication) getApplication();
			final AuthService authService = myApp.getAuthService();
			authService.login(mTxtUsername.getText().toString(), mTxtPassword.getText().toString(), new TableJsonOperationCallback() {				
				@Override
				public void onCompleted(JsonObject jsonObject, Exception exception,
						ServiceFilterResponse response) {
					if (exception == null) {
						authService.setUser(jsonObject);
						Intent loggedInIntent = new Intent(getApplicationContext(), LoggedInActivity.class);
						startActivity(loggedInIntent);
					} else {
						Log.e(TAG, "Error loggin in: " + exception.getMessage());
					}
				}
			});
		}
	};

	View.OnClickListener registerClickListener = new OnClickListener() {		
		@Override
		public void onClick(View v) {	
			Intent registerIntent = new Intent(getApplicationContext(), RegisterAccountActivity.class);
			startActivity(registerIntent);
		}
	};
}
