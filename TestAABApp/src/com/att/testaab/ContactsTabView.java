package com.att.testaab;


import com.att.api.aab.listener.ATTIAMListener;
import com.att.api.aab.manager.AABManager;
import com.att.api.aab.service.ContactResultSet;
import com.att.api.aab.service.PageParams;
import com.att.api.aab.service.QuickContact;
import com.att.api.aab.service.SearchParams;
import com.att.api.error.InAppMessagingError;
import com.att.api.oauth.OAuthToken;

import android.os.Bundle;
import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.widget.ListView;
import android.widget.TabHost;

public class ContactsTabView extends TabActivity {
	
	private AABManager aabManager;
	private PageParams pageParams;
	private SearchParams searchParams;
	private ContactResultSet contactResultSet;
	private ContactsAdapter adapter;
	private QuickContact[] contactsList;
	private ListView ContactsListView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		OAuthToken authToken = new OAuthToken(Config.token, Config.accessTokenExpiry, Config.refreshToken);	
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contacts_tab_view);
		
		final TabHost tabHost = getTabHost();
		
		tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("MyInfo")
				.setContent(new Intent(this, ContactsListTabView.class)));
		
		tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("Groups")
				.setContent(new Intent(this, ContactsListTabView.class)));

		tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("New")
				.setContent(new Intent(this, ContactsListTabView.class)));
		
		ContactsListView = (ListView) findViewById(R.id.contactsListViewItem);		
		aabManager = new AABManager(Config.ldevFqdn,
				authToken, new getContactsListener());
		aabManager.GetContacts("shallow", pageParams, searchParams);
	}
	
	private class getContactsListener implements ATTIAMListener {

		@Override
		public void onSuccess(Object response) {
			// TODO Auto-generated method stub

			contactResultSet = (ContactResultSet) response;
			if (null != contactResultSet && null != contactResultSet.getQuickContacts()
				&& contactResultSet.getQuickContacts().length > 0) {
				
				contactsList = contactResultSet.getQuickContacts();
				adapter = new ContactsAdapter(getApplicationContext(),contactsList);
				ContactsListView.setAdapter(adapter);
				adapter.notifyDataSetChanged();

			}

		}

		@Override
		public void onError(InAppMessagingError error) {
			// TODO Auto-generated method stub
			Log.i("getContactsAPI on error", "onError");

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.contacts_tab_view, menu);
		return true;
	}

}
