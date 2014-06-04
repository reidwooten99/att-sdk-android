package com.att.aabsampleapp;

import com.att.api.aab.manager.AabManager;
import com.att.api.aab.service.Contact;
import com.att.api.aab.service.ContactWrapper;
import com.att.api.error.AttSdkError;
import com.att.sdk.listener.AttSdkListener;


import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.widget.EditText;

public class ContactDetails extends Activity {

	private AabManager aabManager;
	private String contactId;
	private EditText editFirstName;
	private EditText editLastName;
	private EditText editOrganization;
	public static Contact contact; // Contact object used to display and update contact.
	private ContactWrapper contactWrapper;	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact_details);
		
		editFirstName = (EditText) findViewById(R.id.editfirstName);
		editLastName = (EditText) findViewById(R.id.editlastName);
		editOrganization = (EditText) findViewById(R.id.editorgName);
		
		Intent intent = getIntent();
		contactId = intent.getStringExtra("contactId");	
		
		if (contactId == "MY_INFO") {
			aabManager = new AabManager(Config.fqdn, Config.authToken, new getMyInfoListener());
			aabManager.GetMyInfo();
			
		} else {
			aabManager  = new AabManager(Config.fqdn, Config.authToken, new getContactListener());
			aabManager.GetContact(contactId, " ");	
		}
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
			ContactDetails.contact = (Contact) response;
			Contact c = ContactDetails.contact;
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
				ContactDetails.contact = contactWrapper.getContact();
				Contact c = ContactDetails.contact;
				if (null != c) {
					strText = "\n" + c.getContactId() + ", " + 
								c.getFormattedName();
					Log.i("getContactAPI","OnSuccess : ContactID :  " + strText);
					
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

}
