package com.att.aabsampleapp;

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
import com.att.api.util.TokenUpdatedListener;

public class AddressBookLaunch extends Activity {

	private final int OAUTH_CODE = 1;
	private AabManager aabManager = null;
	private ProgressDialog pDialog;

	private void GetUserConsentAuthCode() {
		// Read custom_param from Preferences
		String strStoredCustomParam = Config.customParam;
		Preferences prefs = new Preferences(getApplicationContext());
		if (prefs != null) {
			strStoredCustomParam = prefs.getString(TokenUpdatedListener.customParamSettingName, "");
			if (strStoredCustomParam.length() <= 0) {
				strStoredCustomParam = Config.customParam;
				prefs.setString(TokenUpdatedListener.customParamSettingName, strStoredCustomParam);
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
			strStoredToken = prefs.getString(TokenUpdatedListener.accessTokenSettingName, "");
			if (strStoredToken.length() > 0) {
				savedToken = new OAuthToken(strStoredToken, 
						prefs.getLong(TokenUpdatedListener.tokenExpirySettingName, 0), 
						prefs.getString(TokenUpdatedListener.refreshTokenSettingName, ""), 0);
			}
		}
		
		// Initialize the AabManager also:
		AabManager.SetApiFqdn(Config.fqdn);
		AabManager.SetTokenUpdatedListener(new TokenUpdatedListener(getApplicationContext()));
		AabManager.SetLowerTokenExpiryTimeTo(Config.lowerTokenExpiryTimeTo); // This step is optional.
		aabManager = new AabManager(Config.fqdn, Config.clientID, Config.secretKey, new getTokenListener());
		
		if (savedToken == null) {	
			GetUserConsentAuthCode();
		} else {
			AabManager.SetCurrentToken(savedToken);	
			Log.i("gotSavedToken", "Saved Token: " + TokenUpdatedListener.tokenDisplayString(savedToken.getAccessToken()));
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

	public class getTokenListener extends AttSdkSampleListener {
		
		public getTokenListener() {
			super("getTokenListener");
		}

		@Override
		public void onSuccess(Object response) {
			OAuthToken authToken = (OAuthToken) response;
			OAuthToken adjustedAuthToken = null;
			if (null != authToken) {
				if (AabManager.GetLowerTokenExpiryTimeTo() >= 0) {
					adjustedAuthToken = new OAuthToken(authToken.getAccessToken(),
							AabManager.GetLowerTokenExpiryTimeTo(),
							authToken.getRefreshToken(), (System.currentTimeMillis() / 1000));
				} else {
					adjustedAuthToken = authToken;
				}
				AabManager.SetCurrentToken(adjustedAuthToken);
				TokenUpdatedListener.UpdateSavedToken(adjustedAuthToken); // Store the token in preferences
				Log.i("getTokenListener", "onSuccess Message : " + TokenUpdatedListener.tokenDisplayString(adjustedAuthToken.getAccessToken()));
				getAddressBookContacts();
			}
		}

		@Override
		public void onError(AttSdkError error) {
			super.onError(error);
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
	
	public static void RevokeToken(final String hint) {
		class revokeTokenListener extends AttSdkSampleListener {		
			public revokeTokenListener() {
				super("revokeToken");
			}
			@Override
			public void onSuccess(Object response) {
				Log.i("revokeTokenListener", hint + " was successfully revoked.");
			}

			@Override
			public void onError(AttSdkError error) {
				super.onError(error);
				Log.i("revokeTokenListener", "Error:"+ hint + " revocation failed. " + error.getHttpResponse());
			}
		}
		
		AabManager aabManager = new AabManager(new revokeTokenListener());
		if (hint.equalsIgnoreCase("access_token")) {
			aabManager.RevokeAccessToken();
		} else {
			aabManager.RevokeToken(hint);		
		}
	}
}
