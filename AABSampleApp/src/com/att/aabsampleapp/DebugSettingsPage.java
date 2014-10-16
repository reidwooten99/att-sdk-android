package com.att.aabsampleapp;
import java.util.Date;

import com.att.api.aab.manager.AabManager;
import com.att.api.oauth.OAuthToken;
import com.att.api.util.Preferences;
import com.att.api.util.TokenUpdatedListener;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class DebugSettingsPage extends Activity {

	private CheckBox m_forceOffNetCheckBox = null;
	private CheckBox m_suppressCheckBox = null;
	private CheckBox m_clearCookiesCheckBox = null;
	private CheckBox m_forceACExpiresCheckBox = null;
	private EditText m_curAC = null;
	private EditText m_refreshToken = null;
	private TextView m_curACTime = null;
	// private static Preferences prefs = null;
	
	protected void InitializeStateFromPreferences() {

		m_curAC = (EditText)findViewById(R.id.curAC);
		m_curAC.setEnabled(false);
		m_refreshToken = (EditText)findViewById(R.id.refreshToken);
		m_curACTime = (TextView)findViewById(R.id.curACTime);
		m_forceOffNetCheckBox = (CheckBox) findViewById(R.id.forceOffNetCheckBox);
		m_suppressCheckBox = (CheckBox) findViewById(R.id.forceSuppressCheckBox);		
		m_clearCookiesCheckBox = (CheckBox) findViewById(R.id.clearCookiesCheckBox);		
		m_forceACExpiresCheckBox = (CheckBox) findViewById(R.id.forceACExpiresCheckBox);

		Preferences prefs = new Preferences(getApplicationContext());		
		if (prefs != null) {
			m_curAC.setText(prefs.getString(TokenUpdatedListener.accessTokenSettingName, ""));
			m_refreshToken.setText(prefs.getString(TokenUpdatedListener.refreshTokenSettingName, ""));
			m_curACTime.setText(String.valueOf(prefs.getLong(TokenUpdatedListener.tokenExpirySettingName, 0) - 
					AabManager.GetReduceTokenExpiryInSeconds_Debug()));

			String savedCustomParam = prefs.getString(TokenUpdatedListener.customParamSettingName, "");
			m_forceOffNetCheckBox.setChecked(savedCustomParam.contains("bypass_onnetwork_auth"));
			m_suppressCheckBox.setChecked(savedCustomParam.contains("suppress_landing_page"));
		}		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.debug_settings_page);
		
		InitializeStateFromPreferences();

		m_forceACExpiresCheckBox.setOnClickListener(new CheckBox.OnClickListener(){
			public void onClick(View v) {
				// set the expiry time to current Unix Epoch time
				m_curACTime.setText(String.format("%d", (new Date().getTime())/1000));
				m_forceACExpiresCheckBox.setChecked(true); // once true, always true
			}
		});
		
		Button applyButton = (Button) findViewById(R.id.applyButton);
		applyButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				String customParamValue = "";
				OAuthToken token = null;

				Preferences prefs = new Preferences(getApplicationContext());		
				if (m_forceOffNetCheckBox.isChecked()) {
					customParamValue = "bypass_onnetwork_auth";
				} 
				if (m_suppressCheckBox.isChecked()) {
					customParamValue = customParamValue.length() > 0 ? customParamValue + "," + "suppress_landing_page" : "suppress_landing_page";
				}
				prefs.setString(TokenUpdatedListener.customParamSettingName, customParamValue);

				token = new OAuthToken(m_curAC.getText().toString().trim(), 
						(Long.parseLong(m_curACTime.getText().toString().trim()) +  
							AabManager.GetReduceTokenExpiryInSeconds_Debug()), 
						m_refreshToken.getText().toString().trim(), 0);
				if (token != null) {
					prefs.setString(TokenUpdatedListener.accessTokenSettingName, token.getAccessToken());
					prefs.setString(TokenUpdatedListener.refreshTokenSettingName, token.getRefreshToken());
					prefs.setLong(TokenUpdatedListener.tokenExpirySettingName, token.getAccessTokenExpiry());

					Log.i("updateSavedToken", "Saved Token: " + TokenUpdatedListener.tokenDisplayString(token.getAccessToken()));
					Log.i("ActualTokenExpiry", new Date(token.getAccessTokenExpiry()*1000).toString());
					Log.i("AdjustedTokenExpiry", new Date((token.getAccessTokenExpiry() - Config.reduceTokenExpiryInSeconds_Debug)*1000).toString());
					
					AabManager.SetCurrentToken(token);
				}		
				if (m_clearCookiesCheckBox.isChecked()){
					CookieSyncManager.createInstance(getApplicationContext());
					CookieManager cookieManager = CookieManager.getInstance();
					cookieManager.removeAllCookie();
					cookieManager.removeSessionCookie(); 
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
