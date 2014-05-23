package com.att.testaab;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.att.api.aab.manager.AabManager;
import com.att.api.aab.service.ContactResultSet;
import com.att.api.aab.service.PageParams;
import com.att.api.aab.service.QuickContact;
import com.att.api.aab.service.SearchParams;
import com.att.api.error.AttSdkError;
import com.att.api.oauth.OAuthToken;
import com.att.sdk.listener.AttSdkListener;

public class ContactsList extends Activity implements OnClickListener {

	private AabManager aabManager;
	private PageParams pageParams;
	private SearchParams searchParams;
	private ContactResultSet contactResultSet;
	private QuickContact[] contactsList;
	private String contactId;
	private Button myInfo;
	private Button myGrps;
	private Button allContacts;
	private Button mySettings;
	
	private ListView ContactsListView;
	private ContactsAdapter adapter;
	
	public final String MY_INFO = "-1";
	public final String NEW_CONTACT = "-2";

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        OAuthToken authToken = new OAuthToken(Config.token, Config.accessTokenExpiry, Config.refreshToken);

        super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contacts_list);

		myInfo = (Button) findViewById(R.id.MyInfo);
		myInfo.setOnClickListener(this);
		
		myGrps = (Button) findViewById(R.id.Groups);
		myGrps.setOnClickListener(this);

		allContacts = (Button) findViewById(R.id.newContact);
		allContacts.setOnClickListener(this);
		
		mySettings = (Button) findViewById(R.id.searchContactList);
		mySettings.setOnClickListener(this);
		
		ContactsListView = (ListView) findViewById(R.id.contactsListViewItem);
		

		aabManager = new AabManager(Config.fqdn,
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

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				contactId = ((QuickContact)ContactsListView.getItemAtPosition(position)).getContactId().toString();
				
				Intent intent = new Intent(ContactsList.this, ContactDetails.class);
				intent.putExtra("contactId", contactId);
				startActivity(intent);				
			}
		});
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch(v.getId()) {	
			case R.id.MyInfo :
				 intent = new Intent(ContactsList.this, ContactDetails.class);
				intent.putExtra("contactId", "-1");
				startActivity(intent);	
				break;
				
			case R.id.Groups :
				 intent = new Intent(ContactsList.this, GroupList.class);
				intent.putExtra("contactId", MY_INFO);
				startActivity(intent);
				break;
				
			case R.id.newContact :
				intent = new Intent(ContactsList.this, ContactDetails.class);
				intent.putExtra("contactId", NEW_CONTACT);
				startActivity(intent);	
				//Toast.makeText(getApplicationContext(), "TO BE IMPLEMENTED", Toast.LENGTH_LONG).show();
				break;
				
			case R.id.settings :
				Toast.makeText(getApplicationContext(), "TO BE IMPLEMENTED", Toast.LENGTH_LONG).show();
				break;
				
		}					
	}
}


