package com.att.testaab;

import com.att.api.aab.manager.AabManager;
import com.att.api.aab.service.ContactResultSet;
import com.att.api.aab.service.PageParams;
import com.att.api.aab.service.QuickContact;
import com.att.api.aab.service.SearchParams;
import com.att.api.oauth.OAuthToken;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class ContactsListTabView extends Activity {
	
	private AabManager AabManager;
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
		setContentView(R.layout.activity_contacts_list_tab_view);
		ContactsListView = (ListView) findViewById(R.id.contactsListViewItem);

		setupContactListListener();

		
		
	}
	
	public void setupContactListListener() {
		ContactsListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				
				contactId = ((QuickContact)ContactsListView.getItemAtPosition(position)).getContactId().toString();
				
				Intent intent = new Intent(ContactsListTabView.this, ContactDetails.class);
				intent.putExtra("contactId", contactId);
				startActivity(intent);				
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.contacts_list_tab_view, menu);
		return true;
	}

}
