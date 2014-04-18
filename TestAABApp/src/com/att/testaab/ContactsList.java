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
import com.att.api.aab.service.Contact;
import com.att.api.aab.service.ContactResultSet;
import com.att.api.aab.service.ContactWrapper;
import com.att.api.aab.service.PageParams;
import com.att.api.aab.service.QuickContact;
import com.att.api.aab.service.SearchParams;
import com.att.api.error.InAppMessagingError;
import com.att.api.immn.listener.ATTIAMListener;
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
				
				aabManager = new AABManager(Config.fqdn, authToken,
											new getContactListener());
				aabManager.GetContact(contactId, "shallow");
				
			}
		});
	}
	
	private class getContactListener implements ATTIAMListener {

		@Override
		public void onSuccess(Object response) {
			
			contactWrapper = (ContactWrapper) response;
			if (null != contactWrapper) {

				//Contact contactDetails = contactWrapper.getContact();
				QuickContact contactDetails = contactWrapper.getQuickContact();
				ArrayList<QuickContact> contacts = new ArrayList<QuickContact>(Arrays.asList(contactDetails));
				
				Intent intent = new Intent(getApplicationContext(), ContactDetails.class);
				
				Bundle bundleObject = new Bundle();
				bundleObject.putSerializable("key", contacts);
				
				for (int i = 0; i < contacts.size(); i++) {
					 IdForContactDetails = contacts.get(i).getContactId();
					Log.i("ContactsList","OnSuccess : ContactID :  " + contacts.get(i).getContactId());
				}
							
				intent.putExtras(bundleObject);
				//intent.putExtra("contactId", IdForContactDetails);
				//startActivity(intent);
			}
		}

		@Override
		public void onError(InAppMessagingError error) {
			Log.i("getContactAPI on error", "onError");

		}
	}

}
