package com.att.aabsampleapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import com.att.api.oauth.OAuthService;
import com.att.api.oauth.OAuthToken;
import com.att.api.rest.RESTException;


public class ContactList extends Activity {
	
	private OAuthService osrvc;
	private final int OAUTH_CODE = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact_list);
		
		//OAuthToken authToken = new OAuthToken(Config.token, Config.accessTokenExpiry, Config.refreshToken);
		osrvc = new OAuthService(Config.fqdn, Config.clientID, Config.secretKey);
		Intent i = new Intent(this,
				com.att.api.consentactivity.UserConsentActivity.class);
		i.putExtra("fqdn", Config.fqdn);
		i.putExtra("clientId", Config.clientID);
		i.putExtra("clientSecret", Config.secretKey);
		i.putExtra("redirectUri", Config.redirectUri);
		i.putExtra("appScope", Config.appScope);
		
		startActivityForResult(i, OAUTH_CODE);				
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == OAUTH_CODE) {
			String oAuthCode = null;
			if (resultCode == RESULT_OK) {
				oAuthCode = data.getStringExtra("oAuthCode");
				Log.i("ContactList", "oAuthCode:" + oAuthCode);
				if (null != oAuthCode) {
					
					try {
						OAuthToken authToken = osrvc.getTokenUsingCode(oAuthCode);
						Log.i("ContactList", "authToken:" + authToken);
					} catch (RESTException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					Log.i("ContactList", "oAuthCode: is null");
				}
			} 					
		} 
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.contact_list, menu);
		return true;
	}

}
