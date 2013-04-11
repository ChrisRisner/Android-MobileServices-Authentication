package com.msdpe.authenticationdemo;

import com.microsoft.windowsazure.mobileservices.MobileServiceAuthenticationProvider;
import com.microsoft.windowsazure.mobileservices.MobileServiceUser;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.UserAuthenticationCallback;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;

public class AuthenticationActivity extends BaseActivity {
	
	private final String TAG = "AuthenticationActivity";
	private ImageButton btnLoginWithFacebook;
	private ImageButton btnLoginWithGoogle;
	private ImageButton btnLoginWithMicrosoft;
	private ImageButton btnLoginWithTwitter;
	private Button btnLoginWithEmail;
	private Activity mActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_authentication);
		
		mActivity = this;

		//Get UI Properties
		btnLoginWithFacebook = (ImageButton) findViewById(R.id.btnLoginWithFacebook);
		btnLoginWithGoogle = (ImageButton) findViewById(R.id.btnLoginWithGoogle);
		btnLoginWithMicrosoft = (ImageButton) findViewById(R.id.btnLoginWithMicrosoft);
		btnLoginWithTwitter = (ImageButton) findViewById(R.id.btnLoginWithTwitter);
		btnLoginWithEmail = (Button) findViewById(R.id.btnLoginWithEmail);
		
		//If user is already authenticated, bypass logging in
		if (mAuthService.isUserAuthenticated()) {
			Intent loggedInIntent = new Intent(getApplicationContext(), LoggedInActivity.class);
			startActivity(loggedInIntent);
		}
		
		//Set onclick listeners
		btnLoginWithFacebook.setOnClickListener(loginWithProviderClickListener);
		btnLoginWithGoogle.setOnClickListener(loginWithProviderClickListener);
		btnLoginWithMicrosoft.setOnClickListener(loginWithProviderClickListener);
		btnLoginWithTwitter.setOnClickListener(loginWithProviderClickListener);
		btnLoginWithEmail.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				Intent customLoginIntent = new Intent(getApplicationContext(), CustomLoginActivity.class);
				startActivity(customLoginIntent);				
			}
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.authentication, menu);
		return true;
	}
	
	View.OnClickListener loginWithProviderClickListener = new OnClickListener() {		
		@Override
		public void onClick(View v) {
			MobileServiceAuthenticationProvider provider = null;
			if (v == btnLoginWithFacebook)
				provider = MobileServiceAuthenticationProvider.Facebook;
			else if (v == btnLoginWithGoogle)
				provider = MobileServiceAuthenticationProvider.Google;
			else if (v == btnLoginWithMicrosoft)
				provider = MobileServiceAuthenticationProvider.MicrosoftAccount;
			else if (v == btnLoginWithTwitter)
				provider = MobileServiceAuthenticationProvider.Twitter;
			mAuthService.login(mActivity, provider, new UserAuthenticationCallback() {				
				@Override
				public void onCompleted(MobileServiceUser user, Exception exception,
						ServiceFilterResponse response) {
					mAuthService.setContext(getApplicationContext());
					if (exception == null) {
						//Take user to the logged in view
						mAuthService.saveUserData();
						Intent loggedInIntent = new Intent(getApplicationContext(), LoggedInActivity.class);
						startActivity(loggedInIntent);
					} else {
						Log.e(TAG, "User did not login successfully");
					}					
				}
			});
		}
	};

}
