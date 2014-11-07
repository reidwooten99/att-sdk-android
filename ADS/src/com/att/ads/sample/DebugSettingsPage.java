package com.att.ads.sample;

import com.att.ads.AuthService;
import com.att.ads.util.EncryptDecrypt;
import com.att.ads.util.Preferences;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class DebugSettingsPage extends Activity {

	private CheckBox m_clearCookiesCheckBox = null;
	private CheckBox m_clearPreferencesCheckBox = null;
	private CheckBox m_revokeRefreshTokenCheckBox = null;
	private EditText m_accessToken = null;
	private EditText m_refreshToken = null;
	private EditText m_tokenExpiresIn = null;
	private String accessToken = "";
	private String refreshToken = "";
	
	protected void InitializeStateFromPreferences() {

		m_accessToken = (EditText)findViewById(R.id.curAC);
		m_accessToken.setEnabled(false);
		m_refreshToken = (EditText)findViewById(R.id.refreshToken);
		m_tokenExpiresIn = (EditText)findViewById(R.id.curACTime);
		m_clearCookiesCheckBox = (CheckBox) findViewById(R.id.clearCookiesCheckBox);		
		m_clearPreferencesCheckBox = (CheckBox) findViewById(R.id.clearPreferencesCheckBox);
		m_revokeRefreshTokenCheckBox = (CheckBox) findViewById(R.id.revokeRefreshTokenCheckBox);

		Preferences prefs = new Preferences(getApplicationContext());		
		if (prefs != null) {
			String accessTokenEnc = prefs.getString("access_token", null);
			String refreshTokenEnc = prefs.getString("refresh_token", null);
			try {
				accessToken = EncryptDecrypt.getDecryptedValue(accessTokenEnc,
						EncryptDecrypt.getSecretKeySpec("access_token"));
				refreshToken = EncryptDecrypt.getDecryptedValue(
						refreshTokenEnc,
						EncryptDecrypt.getSecretKeySpec("refresh_token"));
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			long expiresInd = Long.valueOf(prefs.getString("expires_in", "-1"));

			m_accessToken.setText(accessToken);
			m_refreshToken.setText(refreshToken);
			m_tokenExpiresIn.setText(String.valueOf(expiresInd));
		}		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.debug_settings_page);
		
		InitializeStateFromPreferences();

		Button applyButton = (Button) findViewById(R.id.applyButton);
		applyButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Preferences prefs = new Preferences(getApplicationContext());		
				try {
					prefs.setString("access_token", EncryptDecrypt
							.getEncryptedValue(m_accessToken.getText().toString().trim(), EncryptDecrypt
									.getSecretKeySpec("access_token")));
					
					prefs.setString("refresh_token", EncryptDecrypt
							.getEncryptedValue(m_refreshToken.getText().toString().trim(), EncryptDecrypt
									.getSecretKeySpec("refresh_token")));
					
					prefs.setString("expires_in", m_tokenExpiresIn.getText().toString().trim());
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (m_clearPreferencesCheckBox.isChecked()){
					prefs.setString("access_token", "");
					prefs.setString("refresh_token", "");
					prefs.setString("expires_in", "-1");
				}
				if (m_clearCookiesCheckBox.isChecked()){
					CookieSyncManager.createInstance(getApplicationContext());
					CookieManager cookieManager = CookieManager.getInstance();
					cookieManager.removeAllCookie();
					cookieManager.removeSessionCookie(); 
				}

				if (m_revokeRefreshTokenCheckBox.isChecked()) {
					// Invokes revokeToken network operation a separate thread
					AuthService as = new AuthService(getApplicationContext());
					as.revokeToken();
				}

				finish();	 
			}
		});
		
		Button cancelButton = (Button) findViewById(R.id.cancelButton);
		cancelButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
	}	

	@Override
	public void onResume() {
		super.onResume();
		InitializeStateFromPreferences();
	}
}
