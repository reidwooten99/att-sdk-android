package com.att.aabsampleapp;
import java.util.Date;

import com.att.api.aab.manager.AabManager;
import com.att.api.oauth.OAuthToken;
import com.att.api.util.Preferences;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class DebugSettingsPage extends Activity {

	private Button m_forceOffNetButton = null;
	private Button m_suppressButton = null;
	private Button m_clearCookiesButton = null;
	private Button m_applyButton = null;
	private Button m_forceCheckBox = null;
	private EditText m_curAC = null;
	private EditText m_refreshToken = null;
	private TextView m_curACTime = null;
	private boolean OFF_NET = false;
	private boolean SUPPRESS = false;
	private boolean CLEAR_COOKIES = false;
	
	protected void InitializeStateFromPreferences() {

		m_curAC = (EditText)findViewById(R.id.curAC);
		m_refreshToken = (EditText)findViewById(R.id.refreshToken);
		m_curACTime = (TextView)findViewById(R.id.curACTime);
		m_forceOffNetButton = (Button) findViewById(R.id.forceOffNetCheckBox);
		m_suppressButton = (Button) findViewById(R.id.forceSuppressCheckBox);		

		Preferences prefs = new Preferences(getApplicationContext());		
		if (prefs != null) {
			m_curAC.setText(prefs.getString(Config.accessTokenSettingName, ""));
			m_refreshToken.setText(prefs.getString(Config.refreshTokenSettingName, ""));
			m_curACTime.setText(String.valueOf(prefs.getLong(Config.tokenExpirySettingName, 0) - 
					AabManager.GetReduceTokenExpiryInSeconds_Debug()));

			String savedCustomParam = prefs.getString(Config.customParamSettingName, "");
			if (savedCustomParam.contains("bypass_onnetwork_auth")) {
				m_forceOffNetButton.setBackgroundResource(R.drawable.check_mark);
				OFF_NET = true;
			}

			if (savedCustomParam.contains("suppress_landing_page")) {
				m_suppressButton.setBackgroundResource(R.drawable.check_mark);
				SUPPRESS = true;
			}
		}		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.debug_settings_page);
		
		InitializeStateFromPreferences();

		m_forceOffNetButton.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {    	 		 
				if (!OFF_NET){
					OFF_NET = true;
					m_forceOffNetButton.setBackgroundResource(R.drawable.check_mark);
				}
				else{
					OFF_NET = false;
					m_forceOffNetButton.setBackgroundColor(Color.LTGRAY);
				}
			}
		});

		m_suppressButton.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {   	 
				if (!SUPPRESS){
					m_suppressButton.setBackgroundResource(R.drawable.check_mark);
					SUPPRESS = true;
				}
				else {
					m_suppressButton.setBackgroundColor(Color.LTGRAY);
					SUPPRESS = false;
				}
			}
		});

		m_clearCookiesButton = (Button) findViewById(R.id.clearCookiesCheckBox);
		m_clearCookiesButton.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {
				if (!CLEAR_COOKIES){
					m_clearCookiesButton.setBackgroundResource(R.drawable.check_mark);  
					CLEAR_COOKIES = true;
				}
				else {
					m_clearCookiesButton.setBackgroundColor(Color.LTGRAY);
					CLEAR_COOKIES = false;
				}
			}	 
		});

		m_forceCheckBox = (Button) findViewById(R.id.forceCheckBox);
		m_forceCheckBox.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {
				m_forceCheckBox.setBackgroundResource(R.drawable.check_mark);
				m_curACTime.setText("0");
			}
		});

		m_applyButton = (Button) findViewById(R.id.applyButton);
		m_applyButton.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {
				String customParamValue = "";
				OAuthToken token = null;

				Preferences prefs = new Preferences(getApplicationContext());		
				if (CLEAR_COOKIES){
					prefs.setString(Config.accessTokenSettingName, "");
					prefs.setString(Config.refreshTokenSettingName, "");
					prefs.setLong(Config.tokenExpirySettingName, 0);
					prefs.setString(Config.customParamSettingName, "");
					CookieSyncManager.createInstance(getApplicationContext());
					CookieManager cookieManager = CookieManager.getInstance();
					cookieManager.removeAllCookie();
					cookieManager.removeSessionCookie(); 
				}
				else {
					if (OFF_NET) {
						customParamValue = "bypass_onnetwork_auth";
					} 
					if (SUPPRESS) {
						customParamValue = customParamValue.isEmpty() ? "suppress_landing_page" : customParamValue + "," + "suppress_landing_page";
					}
					prefs.setString(Config.customParamSettingName, customParamValue);

					token = new OAuthToken(m_curAC.getText().toString().trim(), 
							Long.parseLong(m_curACTime.getText().toString().trim()), 
							m_refreshToken.getText().toString().trim(), 0);
					if (token != null) {
						prefs.setString(Config.accessTokenSettingName, token.getAccessToken());
						prefs.setString(Config.refreshTokenSettingName, token.getRefreshToken());
						prefs.setLong(Config.tokenExpirySettingName, token.getAccessTokenExpiry());

						Log.i("updateSavedToken", "Saved Token: " + AddressBookLaunch.tokenDisplayString(token.getAccessToken()));
						Log.i("ActualTokenExpiry", new Date(token.getAccessTokenExpiry()*1000).toString());
						Log.i("AdjustedTokenExpiry", new Date((token.getAccessTokenExpiry() - Config.reduceTokenExpiryInSeconds_Debug)*1000).toString());
						
						AabManager.SetCurrentToken(token);
					}		
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
