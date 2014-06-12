package com.att.aabsampleapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.att.api.aab.service.Contact;
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
		
		ContactsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				//Contact ctcResult = (Contact) ContactsListView.getItemAtPosition(position);
				QuickContact ctcResult = ((QuickContact)ContactsListView.getItemAtPosition(position));
				
				CharSequence popUpList[] = new CharSequence[] {"Delete Contact", "Update Contact"};
				popUpActionList(popUpList, ctcResult, position);
				return true;
			}
		});
	}
	
	public void popUpActionList(final CharSequence popUpList[],
			final QuickContact contact, int position) {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Contact Options");
		builder.setItems(popUpList, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int options) {
				switch (options) {
				case 0: //Delete Contact
						deleteContact(contact);
						break;
				case 1: //Update Contact
						updateContact(contact);
						break;
				
			default:
					break;
				}
			}
		});
		builder.show();
	}
	
	/*@Override
	public void onResume() {
		//refreshContactList(ContactsListView);
		setupContactListListener();
	}*/
	
	public void updateContact(QuickContact contact) {
		String contactId ;
		contactId = contact.getContactId();
		Intent i = new Intent(AllContacts.this, ContactDetails.class);
		i.putExtra("contactId", contactId);
		startActivity(i);
	}
	
	public void deleteContact(final QuickContact delcontact) {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Delete Contact");
		builder.setMessage("Do you want to delete the Contact :  " + delcontact.getFirstName() + "?" );
		
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				String deleteContactID;
				deleteContactID = delcontact.getContactId();
				aabManager = new AabManager(Config.fqdn, Config.authToken, new deleteContactListener());
				aabManager.DeleteContact(deleteContactID);			
			}
			
		});
		
		builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
      	  public void onClick(DialogInterface dialog, int whichButton) {
      		  dialog.cancel();
      	  }
      }); 
		builder.create();	
		builder.show();
		
	}

	private class deleteContactListener implements AttSdkListener {

		@Override
		public void onSuccess(Object response) {
			String result = (String) response;
		
			aabManager = new AabManager(Config.fqdn, Config.authToken,new getContactsListener());
			pageParams = new PageParams("ASC", "firstName", "10", "0");
			aabManager.GetContacts("shallow", pageParams, searchParams);
		
			Log.i("deleteContactAPI onSuccess", result);
		}

		@Override
		public void onError(AttSdkError error) {
			Log.i("deleteContactAPI on error", "onError");
		}
	
	}

	
	public void refreshContactList(ListView ContactsListView) {

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

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		aabManager = new AabManager(Config.fqdn, Config.authToken,new getContactsListener());
		pageParams = new PageParams("ASC", "firstName", "25", "0");
		aabManager.GetContacts("shallow", pageParams, searchParams);
	
	}
	
	
	
	
	

}
