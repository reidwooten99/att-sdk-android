package com.att.aabsampleapp;

import com.att.api.util.Preferences;
import com.att.api.util.TokenUpdatedListener;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.TabHost;

@SuppressWarnings("deprecation")
public class SampleAppLauncher extends TabActivity {

	Intent i;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact_list);

		final TabHost tabHost = getTabHost();

		i = new Intent(this, ContactList.class);
		i.putExtra("groupId", "-1");
		tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("CONTACTS")
				.setContent(i));

		i = new Intent(this, ContactDetails.class);
		i.putExtra("contactId", "MY_INFO");
		tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("MYINFO")
				.setContent(i));

		i = new Intent(this, GroupList.class);
		i.putExtra("groupId", "-1");
		tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("GROUPS")
				.setContent(i));

		/*
		 * i = new Intent(this, ContactDetails.class);
		 * tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("NEW")
		 * .setContent(i));
		 * 
		 * i = new Intent(this, ContactDetails.class); i.putExtra("contactId",
		 * "NEW_CONTACT");
		 * tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("CONTACT")
		 * .setContent(i));
		 */
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.contact_list, menu);
		return true;
	}
	
	private void ProcessMenuCommand(int menuItemId) {
		Intent intent;
		switch (menuItemId) {
		case R.id.action_update:
			// UpdateMyInfo or UpdateContact API
			// Toast.makeText(getApplicationContext(), "List Save clicked",
			// Toast.LENGTH_LONG).show();
			intent = new Intent(SampleAppLauncher.this, ContactDetails.class);
			intent.putExtra("contactId", "MY_INFO");
			intent.putExtra("isUpdateMyInfo", true);
			startActivity(intent);
			break;

		case R.id.action_new:
			// Create new contact based on the values in the fields
			// Toast.makeText(getApplicationContext(), "List New clicked",
			// Toast.LENGTH_LONG).show();
			intent = new Intent(SampleAppLauncher.this, ContactDetails.class);
			intent.putExtra("contactId", "NEW_CONTACT");
			startActivity(intent);
			break;

		case R.id.action_logout:
			Preferences prefs = new Preferences(getApplicationContext());		
			prefs.setString(TokenUpdatedListener.accessTokenSettingName,"");  
//			CookieSyncManager.createInstance(this);
//			CookieManager cookieManager = CookieManager.getInstance();
//			cookieManager.removeAllCookie();
//			cookieManager.removeExpiredCookie();
//			cookieManager.removeSessionCookie();
			finish();
			break;

		case R.id.action_debug_settings:
			intent = new Intent(SampleAppLauncher.this, DebugSettingsPage.class);
	   	 	startActivity(intent);
			break;

		}		
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		ProcessMenuCommand(item.getItemId());
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		ProcessMenuCommand(item.getItemId());
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}

}
