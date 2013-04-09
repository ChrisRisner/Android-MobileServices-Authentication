package com.msdpe.authenticationdemo;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class CustomLoginActivity extends Activity {
	
	private Button btnCancel;
	private Button btnLogin;
	private Button btnRegisterForAccount;
	private Activity mActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_custom_login);
		
		mActivity = this;
		
		//Get UI objects
		btnCancel = (Button) findViewById(R.id.btnCancel);
		btnLogin = (Button) findViewById(R.id.btnLogin);
		btnRegisterForAccount = (Button) findViewById(R.id.btnRegisterForAccount);
		
		//Add on click listeners
		btnCancel.setOnClickListener(cancelClickListener);
		btnLogin.setOnClickListener(loginClickListener);
		btnRegisterForAccount.setOnClickListener(registerClickListener);
		
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
		}
	};

	View.OnClickListener registerClickListener = new OnClickListener() {		
		@Override
		public void onClick(View v) {			
		}
	};
}
