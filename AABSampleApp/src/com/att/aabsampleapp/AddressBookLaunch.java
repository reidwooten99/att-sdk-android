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
import com.att.sdk.listener.AttSdkListener;

public class AddressBookLaunch extends Activity {

	private final int OAUTH_CODE = 1;
	private AabManager aabManager;
	private ProgressDialog pDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		showProgressDialog("Opening  AddressBook .. ");
		setContentView(R.layout.activity_address_book_launch);

		Intent i = new Intent(this,
				com.att.api.consentactivity.UserConsentActivity.class);
		i.putExtra("fqdn", Config.fqdn);
		i.putExtra("clientId", Config.clientID);
		i.putExtra("clientSecret", Config.secretKey);
		i.putExtra("redirectUri", Config.redirectUri);
		i.putExtra("appScope", Config.appScope);

		startActivityForResult(i, OAUTH_CODE);
	}

	@SuppressWarnings("unused")
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == OAUTH_CODE) {
			String oAuthCode = null;
			if (resultCode == RESULT_OK) {
				oAuthCode = data.getStringExtra("oAuthCode");
				Log.i("ContactList", "oAuthCode:" + oAuthCode);
				if (null != oAuthCode) {
					aabManager = new AabManager(Config.fqdn, Config.clientID,
							Config.secretKey, new getTokenListener());
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
				Config.authToken = authToken;
				Config.token = authToken.getAccessToken();
				Config.refreshToken = authToken.getRefreshToken();
				Log.i("getTokenListener",
						"onSuccess Message : " + authToken.getAccessToken());
				getAddressBookContacts();
			}
		}

		@Override
		public void onError(AttSdkError error) {
			Log.i("getTokenListener", "onError Message : ");
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

}
