package com.att.aabsampleapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.att.api.aab.listener.ATTIAMListener;
import com.att.api.aab.manager.AABManager;
import com.att.api.error.InAppMessagingError;
import com.att.api.oauth.OAuthToken;

public class AddressBookLaunch extends Activity {
	
	private final int OAUTH_CODE = 1;
	private AABManager aabManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact_list);

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
					aabManager = new AABManager(Config.fqdn, Config.clientID,Config.secretKey,new getTokenListener());
					aabManager.getOAuthToken(oAuthCode);
				} else {
					Log.i("ContactList", "oAuthCode: is null");
				}
			} 					
		} 
	}
	
	public class getTokenListener implements ATTIAMListener {

		@Override
		public void onSuccess(Object response) {
			OAuthToken authToken = (OAuthToken) response;
			if (null != authToken) {
				Config.token = authToken.getAccessToken();
				Config.refreshToken = authToken.getRefreshToken();
				Log.i("getTokenListener","onSuccess Message : " + authToken.getAccessToken());
			}
		}

		@Override
		public void onError(InAppMessagingError error) {
			Log.i("getTokenListener","onError Message : " );
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.contact_list, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
			
			case R.id.action_logout :
				CookieSyncManager.createInstance(this);
				CookieManager cookieManager = CookieManager.getInstance();
				cookieManager.removeAllCookie();
				cookieManager.removeExpiredCookie();
				cookieManager.removeSessionCookie();
				finish();
				break;
			
			default :
				return super.onOptionsItemSelected(item);
			}		
			return true;
	}		
}
