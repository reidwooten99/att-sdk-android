package com.att.testaab;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.EditText;

import com.att.api.aab.service.AABManager;
import com.att.api.aab.service.ContactWrapper;
import com.att.api.aab.service.QuickContact;
import com.att.api.error.InAppMessagingError;
import com.att.api.aab.listener.ATTIAMListener;
import com.att.api.oauth.OAuthToken;


public class ContactDetails extends Activity {
	
	private String contactId;
	private AABManager aabManager;
	private OAuthToken authToken;
	private ContactWrapper contactWrapper;	
	private EditText editFirstName;
	private EditText editLastName;
	private String strText;




	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact_details); 
		editFirstName = (EditText) findViewById(R.id.editfirstName);
		editLastName = (EditText) findViewById(R.id.editlastName);
		
		
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
					
					editFirstName.setText(qc.getFirstName());
					editLastName.setText(qc.getLastName());
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
