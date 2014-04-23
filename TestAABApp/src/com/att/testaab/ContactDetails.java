package com.att.testaab;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.att.api.aab.service.AABManager;
import com.att.api.aab.service.ContactWrapper;
import com.att.api.aab.service.QuickContact;
import com.att.api.error.InAppMessagingError;
import com.att.api.aab.listener.ATTIAMListener;
import com.att.api.oauth.OAuthToken;


public class ContactDetails extends Activity implements OnClickListener {
	
	private String contactId;
	private AABManager aabManager;
	private OAuthToken authToken;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
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
		
		btnGroups = (Button) findViewById(R.id.Groups);
		btnGroups.setOnClickListener(this);
			
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
					editOrganization.setText(/*qc.getOrganization()*/"ATT");
					editPhone1.setText(qc.getPhone().getNumber());
					editEmailAddress.setText(qc.getEmail().getEmailAddress());
					editAddress.setText(qc.getAddress().getAddrLineOne());
					editAddress2.setText(qc.getAddress().getAddrLineTwo());
					editCity.setText(qc.getAddress().getCity());
					editState.setText(qc.getAddress().getState());
					editZipCode.setText(qc.getAddress().getZipcode());
				}
				return;
			}
		}

		@Override
		public void onError(InAppMessagingError error) {
			Log.i("getContactAPI on error", "onError");

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
		}
	}

}
