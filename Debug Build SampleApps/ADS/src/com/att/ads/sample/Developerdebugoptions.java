package com.att.ads.sample;

import com.att.ads.util.EncryptDecrypt;
import com.att.ads.util.Preferences;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class Developerdebugoptions extends Activity {

	String accessToken;
	String refreshToken;
	Preferences pref;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_developerdebugoptions);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		pref = new Preferences(getApplicationContext());
		
		String accessTokenEnc = pref.getString("access_token", null);
		String refreshTokenEnc = pref.getString("refresh_token", null);
		try {
			accessToken = EncryptDecrypt.getDecryptedValue(accessTokenEnc,
					EncryptDecrypt.getSecretKeySpec("access_token"));
			refreshToken = EncryptDecrypt.getDecryptedValue(
					refreshTokenEnc,
					EncryptDecrypt.getSecretKeySpec("refresh_token"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		long expiresInd = Long.valueOf(pref.getString("expires_in", "-1"));
		
		EditText txtAccessToken = (EditText)findViewById(R.id.txt_AccessToken);
		txtAccessToken.setText(accessToken);
		EditText txtRefreshToken = (EditText)findViewById(R.id.txt_RefreshToken);
		txtRefreshToken.setText(refreshToken);
		EditText txtExpirationTime = (EditText)findViewById(R.id.txt_ExpirationTime);
		txtExpirationTime.setText(String.valueOf(expiresInd));
	}
	
	public void updateValues(View view) {
		
	EditText txtAT = (EditText)findViewById(R.id.txt_AccessToken);
	EditText txtRT = (EditText)findViewById(R.id.txt_RefreshToken);
	EditText txtET = (EditText)findViewById(R.id.txt_ExpirationTime);
		try {
			pref.setString("access_token", EncryptDecrypt
					.getEncryptedValue(txtAT.getText().toString(), EncryptDecrypt
							.getSecretKeySpec("access_token")));
			
			pref.setString("refresh_token", EncryptDecrypt
					.getEncryptedValue(txtRT.getText().toString(), EncryptDecrypt
							.getSecretKeySpec("refresh_token")));
			
			pref.setString("expires_in", txtET.getText().toString());
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		startActivity(new Intent(this, SplashActivity.class));
	}
}
