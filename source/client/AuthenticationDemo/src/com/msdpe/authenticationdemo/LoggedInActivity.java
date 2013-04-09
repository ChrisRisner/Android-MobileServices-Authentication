package com.msdpe.authenticationdemo;

import java.util.ArrayList;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableJsonQueryCallback;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.os.Build;

public class LoggedInActivity extends Activity {
	
	private final String TAG = "LoggedInActivity";
	private TextView lblUserIdValue;
	private TextView lblUsernameValue;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_logged_in);

		//get UI elements
		lblUserIdValue = (TextView) findViewById(R.id.lblUserIdValue);
		lblUsernameValue = (TextView) findViewById(R.id.lblUsernameValue);
		
		AuthenticationApplication myApp = (AuthenticationApplication) getApplication();
		AuthService authService = myApp.getAuthService();
		
		lblUserIdValue.setText(authService.getUserId());
		
		authService.getAuthData(new TableJsonQueryCallback() {			
			@Override
			public void onCompleted(JsonElement result, int count, Exception exception,
					ServiceFilterResponse response) {
				if (exception == null) {
					JsonArray results = result.getAsJsonArray();
					JsonElement item = results.get(0);
					lblUsernameValue.setText(item.getAsJsonObject().getAsJsonPrimitive("UserName").getAsString());
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

}
