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
import android.view.MenuItem;
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
	Intent i;
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contacts_tab_view);
		
		final TabHost tabHost = getTabHost();
		
		i = new Intent(this, ContactsList.class);
		i.putExtra("groupId", "-1");
		tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("List")
				.setContent(i));

		i = new Intent(this, ContactDetails.class);
		i.putExtra("contactId", "-1");
		tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("MyInfo")
				.setContent(i));
		
		i = new Intent(this, GroupList.class);
		i.putExtra("groupId", "-1");
		tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("Groups")
				.setContent(i));

		i = new Intent(this, ContactDetails.class);
		i.putExtra("contactId", "-2");
		tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("New")
				.setContent(i));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.contacts_tab_view, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		
		switch(item.getItemId()) {
		 case R.id.action_new :
			 i = new Intent(this, ContactDetails.class);
			 i.putExtra("contactId", "-2");
			 startActivity(i);
			 break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	

}
