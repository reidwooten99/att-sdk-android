package com.att.testaab;


import android.os.Bundle;
import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.view.Menu;
import android.widget.TabHost;

public class ContactsTabView extends TabActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contacts_tab_view);
		
		final TabHost tabHost = getTabHost();
		
		tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("MyInfo")
				.setContent(new Intent(this, ContactsListTabView.class)));
		
		tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("Groups")
				.setContent(new Intent(this, ContactsListTabView.class)));

		tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("New")
				.setContent(new Intent(this, ContactsListTabView.class)));

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.contacts_tab_view, menu);
		return true;
	}

}
