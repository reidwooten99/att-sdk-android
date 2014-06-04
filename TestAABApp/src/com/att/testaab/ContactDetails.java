package com.att.testaab;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.att.api.aab.manager.AabManager;
import com.att.api.aab.service.Contact;
import com.att.api.aab.service.ContactWrapper;
import com.att.api.error.AttSdkError;
import com.att.api.oauth.OAuthToken;
import com.att.sdk.listener.AttSdkListener;


public class ContactDetails extends Activity implements OnClickListener {
	
	private String contactId;
	//private AabManager AabManager;
	public static Contact contact; // Contact object used to display and update contact.
	private ContactWrapper contactWrapper;	
	private EditText editFirstName;
	private EditText editLastName;
	private EditText editOrganization;
	private EditText editPhone1;
	private EditText editEmailAddress;
	private EditText editAddress;
	private EditText editAddress2;
	private EditText editCity;
	private EditText editState;
	private EditText editZipCode;
	private String strText;
	private Button btnGroups;
	private Button btnUpdateContact;
	private Button btnDeleteContact;
	private Button btnSettings;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        OAuthToken authToken = new OAuthToken(Config.token, Config.accessTokenExpiry, Config.refreshToken);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact_details); 
		
		editFirstName = (EditText) findViewById(R.id.editfirstName);
		editLastName = (EditText) findViewById(R.id.editlastName);
		editOrganization = (EditText) findViewById(R.id.editorgName);
		editPhone1 = (EditText) findViewById(R.id.editPhoneType1);
		editEmailAddress = (EditText) findViewById(R.id.editEmailAdddress);
		editAddress = (EditText) findViewById(R.id.editAddress);
		editAddress2 = (EditText) findViewById(R.id.editAddress2);
		editCity = (EditText) findViewById(R.id.editCity);
		editState =(EditText) findViewById(R.id.editState);
		editZipCode =(EditText) findViewById(R.id.editzipCode);
		
		btnUpdateContact = (Button) findViewById(R.id.Update);
		//btnUpdateContact.setOnClickListener(this);
		
		btnDeleteContact =(Button) findViewById(R.id.Delete);
		//btnDeleteContact.setOnClickListener(this);
		
		btnGroups = (Button) findViewById(R.id.Groups);
		//btnGroups.setOnClickListener(this);
		
		btnSettings = (Button) findViewById(R.id.settings);
		//btnSettings.setOnClickListener(this);
			
		Intent intent = getIntent();
		contactId = intent.getStringExtra("contactId");	
		
		if (contactId == "MY_INFO") {
			AabManager AabManager = new AabManager(Config.fqdn, authToken, new getMyInfoListener());
			AabManager.GetMyInfo();
			
		} else {
			AabManager aabManager = new AabManager(Config.fqdn, authToken, new getContactListener());
			aabManager.GetContact(contactId, " ");	
		}
		//int id = Integer.valueOf(contactId);
		
		
		/*switch(id) {
			case -1:
				AabManager AabManager = new AabManager(Config.fqdn, Config.authToken, new getMyInfoListener());
				AabManager.GetMyInfo();
				break;
			case -2:
				break;
			default :
				break;
				
		}*/
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.contact_details, menu);
		return true;
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// TODO Auto-generated method stub
		//Contact c = getContactFromFields();
		switch(item.getItemId()) {
			case R.id.action_save :
				//UpdateContact API
				break;
				
			case R.id.action_new :
				Intent i = new Intent(this, ContactDetails.class);
				 i.putExtra("contactId", "-2"); // not reqd. create API // create contact from fields aand populate fileds from contact
				 startActivity(i);
				 break;
			
		}
		return super.onMenuItemSelected(featureId, item);
	}

	private class getContactListener implements AttSdkListener {

		@Override
		public void onSuccess(Object response) {			
			contactWrapper = (ContactWrapper) response;
			if (null != contactWrapper) { 
				ContactDetails.contact = contactWrapper.getContact();
				Contact c = ContactDetails.contact;
				if (null != c) {
					strText = "\n" + c.getContactId() + ", " + 
								c.getFormattedName();
					Log.i("getContactsAPI","OnSuccess : ContactID :  " + strText);
					
					editFirstName.setText(c.getFirstName());
					editLastName.setText(c.getLastName());
					editOrganization.setText("ATT");
				}
			}
		}

		@Override
		public void onError(AttSdkError error) {
			Log.i("getContactAPI on error", "onError");

		}
	}
	
	private class getMyInfoListener implements AttSdkListener {
		@Override
		public void onSuccess(Object response) {
			ContactDetails.contact = (Contact) response;
			Contact c = ContactDetails.contact;
			if (null != c) {
				strText = "\n" + c.getContactId() + ", " + 
							c.getFormattedName();
				Log.i("getContactsAPI","OnSuccess : ContactID :  " + strText);
				
				editFirstName.setText(c.getFirstName());
				editLastName.setText(c.getLastName());
				editOrganization.setText("ATT");
			}
			return;
		}

		@Override
		public void onError(AttSdkError error) {
			Log.i("getMyInfoAPI on error", "onError");
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent;
		switch(v.getId()) {	
			case R.id.Groups :
				intent = new Intent(ContactDetails.this, GroupList.class);
				intent.putExtra("contactId", contactId);
				startActivity(intent);	
				break;	
				
		    //Adding the handlers but NOT TESTED.  			
			case R.id.Update :
				/*Contact contactToUpdate; 
				contactToUpdate = contactWrapper.getContact();
				AabManager = new AabManager(Config.fqdn, authToken,new UpdateContactListener());
				AabManager.UpdateContact(contactToUpdate);	*/	
				Toast.makeText(getApplicationContext(), "TO BE IMPLEMENTED", Toast.LENGTH_LONG).show();
				break;
			
			case R.id.Delete : 
				DeleteContact();
				//Toast.makeText(getApplicationContext(), "TO BE IMPLEMENTED", Toast.LENGTH_LONG).show();
				break;
				
			case R.id.settings :
				Toast.makeText(getApplicationContext(), "TO BE IMPLEMENTED", Toast.LENGTH_LONG).show();
				break;
			
				
		}
	}
	
	private void DeleteContact() {
		new AlertDialog.Builder(this)
	    .setTitle("Delete Contact")
	    .setMessage("Are you sure you want to delete this contact?")
	    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	            OAuthToken authToken = new OAuthToken(Config.token, Config.accessTokenExpiry, Config.refreshToken);
	        	AabManager AabManager = new AabManager(Config.fqdn, authToken, new DeleteContactListener());
				AabManager.DeleteContact(contactId);
	        }
	     })
	    .setNegativeButton("No", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	            // do nothing
	        }
	     })
	    .setIcon(android.R.drawable.ic_delete)
	    .show();	
	}

	private class DeleteContactListener implements AttSdkListener {
	
		@Override
		public void onSuccess(Object response) {
			Log.i("contact deleted successfully", "Success");
			Toast.makeText(getApplicationContext(), "Contact deleted.", Toast.LENGTH_LONG).show();
			Intent i = new Intent(ContactDetails.this, ContactsList.class);
			startActivity(i);				
		}
	
		@Override
		public void onError(AttSdkError error) {
			Log.i("deleteContact on error", "onError");	
		}
	}

}
