package com.att.aabsampleapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.att.api.aab.manager.AabManager;
import com.att.api.aab.service.Contact;
import com.att.api.aab.service.ContactWrapper;
import com.att.api.error.AttSdkError;
import com.att.sdk.listener.AttSdkListener;

public class ContactDetails extends Activity {

	private AabManager aabManager;
	private String contactId;
	private EditText editFirstName;
	private EditText editLastName;
	private EditText editOrganization;
	private EditText editPhone1;
	private EditText editPhone2;
	private EditText editEmailAddress;
	private EditText editAddress;
	private EditText editAddress2;
	private EditText editCity;
	private EditText editState;
	private EditText editZipCode;
	public static Contact currentContact; // Contact object used to display and update contact.
	public static Contact newContact; //Contact object used to create new contact.
	private ContactWrapper contactWrapper;	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact_details);
		
		editFirstName = (EditText) findViewById(R.id.editfirstName);
		editLastName = (EditText) findViewById(R.id.editlastName);
		editOrganization = (EditText) findViewById(R.id.editorgName);
		editPhone1 = (EditText) findViewById(R.id.editPhoneType1);
		editPhone2 = (EditText) findViewById(R.id.editPhoneType2);
		editEmailAddress = (EditText) findViewById(R.id.editEmailAdddress);
		editAddress = (EditText) findViewById(R.id.editAddress);
		editAddress2 = (EditText) findViewById(R.id.editAddress2);
		editCity = (EditText) findViewById(R.id.editCity);
		editState =(EditText) findViewById(R.id.editState);
		editZipCode =(EditText) findViewById(R.id.editzipCode);
		
		Intent intent = getIntent();
		contactId = intent.getStringExtra("contactId");	
		
		if(contactId == "NEW_CONTACT" ) {		 //To be implemented
			
			ContactDetails.newContact = createContactFromContactDetails();
			
		}
		
		else  {
				if (contactId == "MY_INFO") {		
					aabManager = new AabManager(Config.fqdn, Config.authToken, new getMyInfoListener());
					aabManager.GetMyInfo();
			
				} else {
					aabManager  = new AabManager(Config.fqdn, Config.authToken, new getContactListener());
					aabManager.GetContact(contactId, " ");	
				}
			}
		
		aabManager  = new AabManager(Config.fqdn, Config.authToken, new getContactListener());
		aabManager.GetContact(contactId, contactId);	
	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.contact_details, menu);
		return true;
	}
	
	private class getMyInfoListener implements AttSdkListener {
		
		private String strText;
		
		@Override
		public void onSuccess(Object response) {
			ContactDetails.currentContact = (Contact) response;
			Contact c = ContactDetails.currentContact;
			if (null != c) {
				strText = "\n" + c.getContactId() + ", " + 
							c.getFormattedName();
				Log.i("getMyInfoAPI","OnSuccess : ContactID :  " + strText);
				
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
	
	private class getContactListener implements AttSdkListener {

		private String strText;
		@Override
		public void onSuccess(Object response) {			
			contactWrapper = (ContactWrapper) response;
			if (null != contactWrapper) { 
				ContactDetails.currentContact = contactWrapper.getContact();
				Contact c = ContactDetails.currentContact;
				if (null != c) {
					strText = "\n" + c.getContactId() + ", " + 
								c.getFormattedName();
					Log.i("getContactAPI","OnSuccess : ContactID :  " + strText);
					
					createContactDetailsFromContact(c);
				}
			}
		}

		@Override
		public void onError(AttSdkError error) {
			Log.i("getContactAPI on error", "onError");

		}
	}
	
	public void createContactDetailsFromContact( Contact contact) {
		editFirstName.setText(contact.getFirstName());
		editLastName.setText(contact.getLastName());
		editOrganization.setText(contact.getOrganization());
		/*editPhone1.setText(contact.getPhones()[0].getNumber());
		editPhone2.setText(contact.getPhones()[0].getNumber());
		editEmailAddress.setText(contact.getEmails()[0].getEmailAddress());
		editAddress.setText(contact.getAddresses()[0].getAddrLineOne());
		editAddress2.setText(contact.getAddresses()[0].getAddrLineOne());;
		editCity.setText(contact.getAddresses()[0].getCity());
		editState.setText(contact.getAddresses()[0].getState());
		editZipCode.setText(contact.getAddresses()[0].getZipcode());*/
	}
	
	public Contact createContactFromContactDetails() {
		
		Contact.Builder builder = new Contact.Builder(); 
		builder.setFirstName(editFirstName.getText().toString());
		builder.setLastName(editLastName.getText().toString());
		
		/*long time= System.currentTimeMillis();
		builder.setContactId(String.valueOf(time));
		Phone [] phones = new Phone[1];
		phones[0] = new Phone("WORK,CELL", "42567689700", true);
		builder.setPhones(phones);*/
		ContactDetails.newContact = builder.build();
		
		return ContactDetails.newContact;
	}
	
	
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		//Contact c = getContactFromFields();
		switch(item.getItemId()) {
			case R.id.action_save :
			case R.id.action_update :

				//UpdateMyInfo or UpdateContact API
				//Toast.makeText(getApplicationContext(), "List Save clicked", Toast.LENGTH_LONG).show();
				//if(contactId == "NEW_CONTACT")
				ContactDetails.newContact = createContactFromContactDetails();
				aabManager = new AabManager(Config.fqdn, Config.authToken, new createContactListener());
				aabManager.CreateContact(ContactDetails.newContact);
				break;
			
				
		}
		return super.onMenuItemSelected(featureId, item);
	}
	
	private class createContactListener implements AttSdkListener {

		;
		@Override
		public void onSuccess(Object response) {		
			String strText;
			String newContactId;
			String result = (String) response;
			if (null != result) {
				strText = "\n" + result;				
				String[] locationUrl = result.split("contacts/");
				newContactId = locationUrl[1];
				Log.i("createContactAPI","OnSuccess : ContactID :  " + result);
				finish();
				/*Intent intent = new Intent(ContactDetails.this, ContactList.class);
				intent.putExtra("contactId", newContactId);
				startActivity(intent);*/
			}
			else {
					strText = "Unknown: " + "test.\nNo data returned.";				
			}		
			
		}

		@Override
		public void onError(AttSdkError error) {
			Log.i("createContactAPI on error", "onError");

		}
	}

}
