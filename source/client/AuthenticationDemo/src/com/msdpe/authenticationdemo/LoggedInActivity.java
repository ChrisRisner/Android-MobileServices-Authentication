package com.msdpe.authenticationdemo;

import java.util.ArrayList;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableJsonOperationCallback;
import com.microsoft.windowsazure.mobileservices.TableJsonQueryCallback;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;

public class LoggedInActivity extends BaseActivity {
	
	private final String TAG = "LoggedInActivity";
	private TextView mLblUserIdValue;
	private TextView mLblUsernameValue;
	private Button mBtnLogout;
	private Button mBtnTestNoRetry;
	private Button mBtnTestRetry;
	private TextView mLblInfo;
	private Activity mActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_logged_in);
		
		mActivity = this;

		//get UI elements
		mLblUserIdValue = (TextView) findViewById(R.id.lblUserIdValue);
		mLblUsernameValue = (TextView) findViewById(R.id.lblUsernameValue);
		mBtnLogout = (Button) findViewById(R.id.btnLogout);
		mBtnTestNoRetry = (Button) findViewById(R.id.btnTestNoRetry);
		mBtnTestRetry = (Button) findViewById(R.id.btnTestRetry);
		mLblInfo = (TextView) findViewById(R.id.lblInfo);
		
		//Set click listeners
		mBtnLogout.setOnClickListener(logoutClickListener);
		mBtnTestNoRetry.setOnClickListener(testNoRetryClickListener);
		mBtnTestRetry.setOnClickListener(testRetryClickListener);
		
		AuthenticationApplication myApp = (AuthenticationApplication) getApplication();
		AuthService authService = myApp.getAuthService();
		
		mLblUserIdValue.setText(authService.getUserId());
		
		authService.getAuthData(new TableJsonQueryCallback() {			
			@Override
			public void onCompleted(JsonElement result, int count, Exception exception,
					ServiceFilterResponse response) {
				if (exception == null) {
					JsonArray results = result.getAsJsonArray();
					JsonElement item = results.get(0);
					mLblUsernameValue.setText(item.getAsJsonObject().getAsJsonPrimitive("UserName").getAsString());
				} else {
					Log.e(TAG, "There was an exception getting auth data: " + exception.getMessage());
				}
			}
		});
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.logged_in, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		/*case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;*/
		}
		return super.onOptionsItemSelected(item);
	}
	
	View.OnClickListener logoutClickListener = new OnClickListener() {		
		@Override
		public void onClick(View v) {	
			AuthenticationApplication myApp = (AuthenticationApplication) getApplication();
			AuthService authService = myApp.getAuthService();
			authService.logout(true);
		}
	};
	
	View.OnClickListener testRetryClickListener = new OnClickListener() {		
		@Override
		public void onClick(View v) {	
			AuthenticationApplication myApp = (AuthenticationApplication) getApplication();
			AuthService authService = myApp.getAuthService();
			authService.testForced401(true, new TableJsonOperationCallback() {				
				@Override
				public void onCompleted(JsonObject jsonObject, Exception exception,
						ServiceFilterResponse response) {
					
					if (exception == null) {
						mLblInfo.setText("Success testing 401");
					} else {
						Log.e(TAG, "Exception testing 401: " + exception.getMessage());
					}
				}
			});
		}
	};
	
	View.OnClickListener testNoRetryClickListener = new OnClickListener() {		
		@Override
		public void onClick(View v) {	
			AuthenticationApplication myApp = (AuthenticationApplication) getApplication();
			AuthService authService = myApp.getAuthService();			
			
			authService.testForced401(false, new TableJsonOperationCallback() {				
				@Override
				public void onCompleted(JsonObject jsonObject, Exception exception,
						ServiceFilterResponse response) {	
					
					if (exception == null) {
						mLblInfo.setText("Success testing 401");
					} else {
						Log.e(TAG, "Exception testing 401: " + exception.getMessage());
					}
				}
			});
		}
	};
}
