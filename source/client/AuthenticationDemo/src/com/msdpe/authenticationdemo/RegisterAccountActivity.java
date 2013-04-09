package com.msdpe.authenticationdemo;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class RegisterAccountActivity extends Activity {
	
	private Button btnRegister;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_account);
		
		//Get UI elements
		btnRegister = (Button) findViewById(R.id.btnRegister);
		
		//Set click listeners
		btnRegister.setOnClickListener(registerClickListener);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.register_account, menu);
		return true;
	}
	
	View.OnClickListener registerClickListener = new OnClickListener() {		
		@Override
		public void onClick(View v) {			
		}
	};

}
