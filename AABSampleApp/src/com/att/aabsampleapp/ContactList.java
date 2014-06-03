package com.att.aabsampleapp;


import com.att.api.aab.service.ContactResultSet;
import com.att.api.error.AttSdkError;
import com.att.sdk.listener.AttSdkListener;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.TabHost;

@SuppressWarnings("deprecation")
public class ContactList extends TabActivity {
	
	Intent i;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact_list);
		
		final TabHost tabHost = getTabHost();
		
		i = new Intent(this, AllContacts.class);
		i.putExtra("groupId", "-1");
		tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("CONTCTS")
				.setContent(i));
		
		tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("MYINFO")
				.setContent(i));
		
		tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("GROUPS")
				.setContent(i));
		
		tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("NEW")
				.setContent(i));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.contact_list, menu);
		return true;
	}
	
	

	
	
}
