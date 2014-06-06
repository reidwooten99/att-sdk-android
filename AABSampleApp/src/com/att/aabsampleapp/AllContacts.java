package com.att.aabsampleapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.att.api.aab.manager.AabManager;
import com.att.api.aab.service.ContactResultSet;
import com.att.api.aab.service.PageParams;
import com.att.api.aab.service.QuickContact;
import com.att.api.aab.service.SearchParams;
import com.att.api.error.AttSdkError;
import com.att.sdk.listener.AttSdkListener;



public class AllContacts extends Activity implements OnClickListener{
	
	private AabManager aabManager;
	private PageParams pageParams;
	private SearchParams searchParams;
	private ContactResultSet contactResultSet;
	private ContactsAdapter adapter;
	private ListView ContactsListView;
	private QuickContact[] contactsList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_all_contacts);
				
		ContactsListView = (ListView) findViewById(R.id.contactsListViewItem);
		aabManager = new AabManager(Config.fqdn, Config.authToken, new getContactsListener());
		
		aabManager.GetContacts("shallow", pageParams, searchParams);
		
		setupContactListListener();
	}
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.all_contacts, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		
	}
	
	private class getContactsListener implements AttSdkListener {

		@Override
		public void onSuccess(Object response) {
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
		public void onError(AttSdkError error) {
			Log.i("getContactsAPI on error", "onError");

		}
	}
	
	public void setupContactListListener() {
		ContactsListView.setOnItemClickListener(new OnItemClickListener() {
			private String contactId;
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				contactId = ((QuickContact)ContactsListView.getItemAtPosition(position)).getContactId().toString();
				
				Intent intent = new Intent(AllContacts.this, ContactDetails.class);
				intent.putExtra("contactId", contactId);
				startActivity(intent);				
			}
		});
	}

}
