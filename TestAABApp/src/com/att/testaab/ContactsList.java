package com.att.testaab;

import java.util.ArrayList;
import java.util.Arrays;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.att.api.aab.service.AABManager;
import com.att.api.aab.service.ContactResultSet;
import com.att.api.aab.service.ContactWrapper;
import com.att.api.aab.service.PageParams;
import com.att.api.aab.service.QuickContact;
import com.att.api.aab.service.SearchParams;
import com.att.api.error.InAppMessagingError;
import com.att.api.aab.listener.ATTIAMListener;
import com.att.api.oauth.OAuthToken;

public class ContactsList extends Activity {

	private AABManager aabManager;
	private PageParams pageParams;
	private SearchParams searchParams;
	private ContactResultSet contactResultSet;
	private OAuthToken authToken;
	private QuickContact[] contactsList;
	private ContactWrapper contactWrapper;	
	private String contactId;
	private String IdForContactDetails;


	private ListView ContactsListView;
	private ContactsAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contacts_list);

		ContactsListView = (ListView) findViewById(R.id.contactsListViewItem);

		aabManager = new AABManager(Config.fqdn,
				authToken, new getContactsListener());
		aabManager.GetContacts("shallow", pageParams, searchParams);
		
		setupContactListListener();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.contacts_list, menu);
		return true;
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
	
	public void setupContactListListener() {
		ContactsListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				
				contactId = ((QuickContact)ContactsListView.getItemAtPosition(position)).getContactId().toString();
				
				Intent intent = new Intent(ContactsList.this, ContactDetails.class);
				intent.putExtra("contactId", contactId);
				startActivity(intent);				
			}
		});
	}
	
	
	
	

}
