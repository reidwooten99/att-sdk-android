package com.att.aabsampleapp;

import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.att.api.aab.manager.AabManager;
import com.att.api.error.AttSdkError;
import com.att.api.oauth.OAuthToken;
import com.att.api.util.Preferences;
import com.att.sdk.listener.AttSdkListener;
import com.att.sdk.listener.AttSdkTokenUpdater;

public class AddressBookLaunch extends Activity {

	private final int OAUTH_CODE = 1;
	private AabManager aabManager = null;
	private ProgressDialog pDialog;
	
	public static String tokenDisplayString (final String tokenString) {
		int len = 0;
		String tokenDisplay = "";
		len = tokenString.length();
		if (len > 10) {
			tokenDisplay = tokenString.substring(0,10) + "********";
		}
		if (len > tokenDisplay.length()) {
			tokenDisplay += tokenString.substring(tokenDisplay.length());
		}
		
		return tokenDisplay;		
	}
	
	public void UpdateSavedToken(OAuthToken token) {
		
		Preferences prefs = new Preferences(getApplicationContext());
		if (prefs != null && token != null) {
			prefs.setString(Config.accessTokenSettingName, token.getAccessToken());
			prefs.setString(Config.refreshTokenSettingName, token.getRefreshToken());
			prefs.setLong(Config.tokenExpirySettingName, token.getAccessTokenExpiry());

			Log.i("updateSavedToken", "Saved Token: " + tokenDisplayString(token.getAccessToken()));
			Log.i("ActualTokenExpiry", new Date(token.getAccessTokenExpiry()*1000).toString());
			Log.i("AdjustedTokenExpiry", new Date((token.getAccessTokenExpiry() - AabManager.GetReduceTokenExpiryInSeconds_Debug())*1000).toString());
		}		
	}
	
	public void DeleteSavedToken() {		
		Preferences prefs = new Preferences(getApplicationContext());
		if (prefs != null) {
			prefs.setString(Config.accessTokenSettingName, "");
			prefs.setString(Config.refreshTokenSettingName, "");
			prefs.setLong(Config.tokenExpirySettingName, 0);
			Log.i("deleteSavedToken", "Deleted Saved Token.");
		}	
		// Logout from the application and restart.
		Log.e("Invalid Token", "Logout and Restart the application");
		// GetUserConsentAuthCode();
	}
	
	private void GetUserConsentAuthCode() {
		// Read custom_param from Preferences
		String strStoredCustomParam = Config.customParam;
		Preferences prefs = new Preferences(getApplicationContext());
		if (prefs != null) {
			strStoredCustomParam = prefs.getString(Config.customParamSettingName, "");
			if (strStoredCustomParam.length() <= 0) {
				strStoredCustomParam = Config.customParam;
				prefs.setString(Config.customParamSettingName, strStoredCustomParam);
			}
		}
		Intent i = new Intent(this,
				com.att.api.consentactivity.UserConsentActivity.class);
		i.putExtra("fqdn", Config.fqdn);
		i.putExtra("clientId", Config.clientID);
		i.putExtra("clientSecret", Config.secretKey);
		i.putExtra("redirectUri", Config.redirectUri);
		i.putExtra("appScope", Config.appScope);
		i.putExtra("customParam", strStoredCustomParam);

		startActivityForResult(i, OAUTH_CODE);		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		OAuthToken savedToken = null;
		String strStoredToken = null;

		super.onCreate(savedInstanceState);
				
		showProgressDialog("Opening  AddressBook .. ");
		setContentView(R.layout.activity_address_book_launch);
		
		Preferences prefs = new Preferences(getApplicationContext());
		if (prefs != null) {
			strStoredToken = prefs.getString(Config.accessTokenSettingName, "");
			if (strStoredToken.length() > 0) {
				savedToken = new OAuthToken(strStoredToken, 
						prefs.getLong(Config.tokenExpirySettingName, 0), 
						prefs.getString(Config.refreshTokenSettingName, ""), 0);
			}
		}
		
		// Initialize the AabManager also:
		AabManager.SetApiFqdn(Config.fqdn);
		AabManager.SetTokenUpdatedListener(new tokenUpdatedListener());
		AabManager.SetReduceTokenExpiryInSeconds_Debug(Config.reduceTokenExpiryInSeconds_Debug);
		AabManager.SetAppendToRefreshToken_Debug(Config.appendToRefreshToken_Debug);
		aabManager = new AabManager(Config.fqdn, Config.clientID, Config.secretKey, new getTokenListener());
		
		//savedToken = null; // Set it to null due to some UI issue.
		
		if (savedToken == null) {	
			GetUserConsentAuthCode();
		} else {
			AabManager.SetCurrentToken(savedToken);	
			Log.i("gotSavedToken", "Saved Token: " + tokenDisplayString(savedToken.getAccessToken()));
			getAddressBookContacts();			
		}
	}

	@SuppressWarnings("unused")
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == OAUTH_CODE) {
			String oAuthCode = null;
			if (resultCode == RESULT_OK) {
				oAuthCode = data.getStringExtra("oAuthCode");
				Log.i("ContactList", "oAuthCode:" + oAuthCode);
				if (null != oAuthCode && null != aabManager) {
					aabManager.getOAuthToken(oAuthCode);
				} else if (resultCode == RESULT_CANCELED) {
					String errorMessage = null;
					if (null != data) {
						errorMessage = data.getStringExtra("ErrorMessage");
					} else {
						errorMessage = getResources().getString(
								R.string.title_close_application);
					}
					new AlertDialog.Builder(AddressBookLaunch.this)
							.setTitle("Error")
							.setMessage(errorMessage)
							.setPositiveButton("OK",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
											dialog.cancel();
											finish();
										}
									}).show();
				}
			}
		}
	}

	public class getTokenListener implements AttSdkListener {

		@Override
		public void onSuccess(Object response) {
			OAuthToken authToken = (OAuthToken) response;
			if (null != authToken) {
				Config.authToken = null; // authToken;
				//Config.token = authToken.getAccessToken();
				//Config.refreshToken = authToken.getRefreshToken();
				//Config.accessTokenExpiry = authToken.getAccessTokenExpiry();
				AabManager.SetCurrentToken(authToken);
				UpdateSavedToken(authToken); // Store the token in preferences
				Log.i("getTokenListener", "onSuccess Message : " + tokenDisplayString(authToken.getAccessToken()));
				getAddressBookContacts();
			}
		}

		@Override
		public void onError(AttSdkError error) {
			Log.i("getTokenListener", "Error:" + error.getHttpResponse());
			if (error.getHttpResponse().contains("invalid_grant")) {
				DeleteSavedToken();
			}
		}
	}

	public void getAddressBookContacts() {
		Intent i = new Intent(AddressBookLaunch.this, SampleAppLauncher.class);
		startActivity(i);
		dismissProgressDialog();
		finish();

	}

	// Progress Dialog
	public void showProgressDialog(String dialogMessage) {

		if (null == pDialog)
			pDialog = new ProgressDialog(this);
		pDialog.setCancelable(false);
		pDialog.setMessage(dialogMessage);
		pDialog.show();
	}

	public void dismissProgressDialog() {
		if (null != pDialog) {
			pDialog.dismiss();
		}
	}

	public class tokenUpdatedListener implements AttSdkTokenUpdater {
		@Override
		public void onTokenUpdate(OAuthToken newToken) {
			UpdateSavedToken(newToken);
		}
		
		@Override
		public void onTokenDelete() {
			DeleteSavedToken();
		}
	}

}
