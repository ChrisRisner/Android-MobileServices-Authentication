/*
 Copyright 2013 Microsoft Corp
 
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.apache.org/licenses/LICENSE-2.0
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

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

public class RegisterAccountActivity extends BaseActivity {
	
	private final String TAG = "RegisterAccountActivity";
	private Button btnRegister;
	private EditText mTxtUsername;
	private EditText mTxtPassword;
	private EditText mTxtConfirm;
	private EditText mTxtEmail;
	private Activity mActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_account);
		
		mActivity = this;
		
		//Get UI elements
		btnRegister = (Button) findViewById(R.id.btnRegister);
		mTxtUsername = (EditText) findViewById(R.id.txtRegisterUsername);
		mTxtPassword = (EditText) findViewById(R.id.txtRegisterPassword);
		mTxtConfirm = (EditText) findViewById(R.id.txtRegisterConfirm);
		mTxtEmail = (EditText) findViewById(R.id.txtRegisterEmail);
		
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
			//We're just logging the validation errors, we should be showing something to the user
			if (mTxtUsername.getText().toString().equals("") ||
					mTxtPassword.getText().toString().equals("") ||
					mTxtConfirm.getText().toString().equals("") ||
					mTxtEmail.getText().toString().equals("")) {
				Log.w(TAG, "You must enter all fields to register");
				return;
			} else if (!mTxtPassword.getText().toString().equals(mTxtConfirm.getText().toString())) {
				Log.w(TAG, "The passwords you've entered don't match");
				return;
			} else {
				mAuthService.registerUser(mTxtUsername.getText().toString(),
										 mTxtPassword.getText().toString(),
										 mTxtConfirm.getText().toString(),
										 mTxtEmail.getText().toString(),
										 new TableJsonOperationCallback() {											
						@Override
						public void onCompleted(JsonObject jsonObject, Exception exception,
								ServiceFilterResponse response) {
							if (exception == null) {
								//If that was successful, set and save the user data
								mAuthService.setUserAndSaveData(jsonObject);
								//Finish this activity and run the logged in activity	
								mActivity.finish();
								Intent loggedInIntent = new Intent(getApplicationContext(), LoggedInActivity.class);
								startActivity(loggedInIntent);
							} else {
								Log.e(TAG, "There was an error registering the user: " + exception.getMessage());
							}
						}
					});
			}
		}
	};

}
