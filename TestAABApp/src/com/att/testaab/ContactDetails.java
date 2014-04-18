package com.att.testaab;

import java.util.ArrayList;
import java.util.Arrays;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.EditText;
import android.widget.TextView;

import com.att.api.aab.service.AABManager;
import com.att.api.aab.service.ContactWrapper;
import com.att.api.aab.service.QuickContact;
import com.att.api.error.InAppMessagingError;
import com.att.api.immn.listener.ATTIAMListener;
import com.att.api.oauth.OAuthToken;


public class ContactDetails extends Activity {
	
	private String firstName;
	private String contactId;
	private AABManager aabManager;
	private OAuthToken authToken;
	private ContactWrapper contactWrapper;	
	private TextView fnText;
	private EditText editFirstName;
	private String strText;




	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact_details); 
		fnText = (TextView) findViewById(R.id.fnText);
		editFirstName = (EditText) findViewById(R.id.firstName);
		
		
		Intent intent = getIntent();
		contactId = intent.getStringExtra("contactId");
		
		aabManager = new AABManager(Config.fqdn, authToken,new getContactListener());
		aabManager.GetContact(contactId, "shallow");	
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.contact_details, menu);
		return true;
	}
	
	private class getContactListener implements ATTIAMListener {

		@Override
		public void onSuccess(Object response) {
			
			contactWrapper = (ContactWrapper) response;
			if (null != contactWrapper) {
				strText = null;
				QuickContact qc = contactWrapper.getQuickContact();
				if (null != qc) {
					strText = "\n" + qc.getContactId() + ", " + 
								qc.getFormattedName() + ", " + qc.getPhone().getNumber();
					Log.i("getContactsAPI","OnSuccess : ContactID :  " + strText);
					
					firstName = qc.getFormattedName();
					//Log.i("getContactsAPI", "OnSuccess : ContactID :  " +contactResultSet.getQuickContacts()[1].getContactId().toString());
					editFirstName.setText(firstName);
				}
				return;
			}
		}

		@Override
		public void onError(InAppMessagingError error) {
			Log.i("getContactAPI on error", "onError");

		}
	}

}
