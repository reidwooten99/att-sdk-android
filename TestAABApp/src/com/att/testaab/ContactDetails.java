package com.att.testaab;

import com.att.api.aab.listener.ATTIAMListener;
import com.att.api.aab.manager.AABManager;
import com.att.api.aab.service.ContactWrapper;
import com.att.api.error.InAppMessagingError;
import com.att.api.oauth.OAuthToken;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract.QuickContact;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class ContactDetails extends Activity implements OnClickListener {
	
	private String contactId;
	//private AABManager aabManager;
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
	private Button btnUpdateContact;
	private Button btnDeleteContact;
	private Button btnSettings;
	

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
		
		btnUpdateContact = (Button) findViewById(R.id.Update);
		btnUpdateContact.setOnClickListener(this);
		
		btnDeleteContact =(Button) findViewById(R.id.Delete);
		btnDeleteContact.setOnClickListener(this);
		
		btnGroups = (Button) findViewById(R.id.Groups);
		btnGroups.setOnClickListener(this);
		
		btnSettings = (Button) findViewById(R.id.settings);
		btnSettings.setOnClickListener(this);
			
		Intent intent = getIntent();
		contactId = intent.getStringExtra("contactId");	
		AABManager aabManager = new AABManager(Config.fqdn, authToken, new getContactListener());
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
				com.att.api.aab.service.QuickContact qc = contactWrapper.getQuickContact();
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
				
		    //Adding the handlers but NOT TESTED.  			
			case R.id.Update :
				/*Contact contactToUpdate; 
				contactToUpdate = contactWrapper.getContact();
				aabManager = new AABManager(Config.fqdn, authToken,new UpdateContactListener());
				aabManager.UpdateContact(contactToUpdate);	*/	
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
	        	AABManager aabManager = new AABManager(Config.fqdn, authToken, new DeleteContactListener());
				aabManager.DeleteContact(contactId);
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

	private class DeleteContactListener implements ATTIAMListener {
	
		@Override
		public void onSuccess(Object response) {
			Log.i("contact deleted successfully", "Success");
			Toast.makeText(getApplicationContext(), "Contact deleted.", Toast.LENGTH_LONG).show();
			Intent i = new Intent(ContactDetails.this, ContactsList.class);
			startActivity(i);				
		}
	
		@Override
		public void onError(InAppMessagingError error) {
			Log.i("deleteContact on error", "onError");	
		}
	}

}
