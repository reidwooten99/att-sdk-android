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
import android.widget.TextView;

import com.att.api.aab.service.AABManager;
import com.att.api.aab.service.Contact;
import com.att.api.aab.service.ContactResultSet;
import com.att.api.aab.service.ContactWrapper;
import com.att.api.aab.service.Group;
import com.att.api.aab.service.GroupResultSet;
import com.att.api.aab.service.PageParams;
import com.att.api.aab.service.Phone;
import com.att.api.aab.service.QuickContact;
import com.att.api.aab.service.SearchParams;
import com.att.api.error.InAppMessagingError;
import com.att.api.immn.listener.ATTIAMListener;
import com.att.api.oauth.OAuthToken;

public class TestAAB extends Activity {

	private AABManager aabManager;
	private PageParams pageParams;
	private SearchParams searchParams;
	private ContactResultSet contactResultSet;
	private OAuthToken authToken;
	private Button getContacts;
	private TextView displayContacts;
	private ContactWrapper contactWrapper;	
	private Button BtnContactsList;
	private EditText testApi;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_aab);
		getContacts = (Button) findViewById(R.id.getContactsBtn);
		displayContacts = (TextView)findViewById(R.id.displayContacts1);
		BtnContactsList = (Button)findViewById(R.id.contactsListView);
		testApi = (EditText)findViewById(R.id.editText1);
		
		pageParams = new PageParams("ASC", "firstName", "2", "0");
		SearchParams.Builder builder = new SearchParams.Builder();
		searchParams = new SearchParams(builder.setZipcode("94086"));
		
		BtnContactsList.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(TestAAB.this, ContactsList.class);
				startActivity(i);				
			}
		});
	
		
		getContacts.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			    String apiNumber = testApi.getText().toString();
                int iOperation = Integer.valueOf(apiNumber);
				switch (iOperation) {
					case 1:
						aabManager = new AABManager("http://ldev.code-api-att.com:8888", 
													authToken,
													new getContactsListener());
						aabManager.GetContacts("shallow", pageParams, searchParams );
						break;
					case 2:
						aabManager = new AABManager("http://ldev.code-api-att.com:8888", 
													authToken,
													new getContactListener());
						//aabManager.GetContact("09876544321", "shallow");
						aabManager.GetContact("0987654432123", "shallow");	
						break;
					case 3:
						aabManager = new AABManager("http://ldev.code-api-att.com:8888", 
													authToken,
													new getContactGroupsListener());
						//aabManager.GetContact("09876544321", "shallow");
						pageParams = new PageParams("ASC", "firstName", "2", "0");
						aabManager.GetContactGroups("0987654432123", pageParams);	
						break;
					case 4:
						aabManager = new AABManager("http://ldev.code-api-att.com:8888", 
								authToken,
								new createContactListener());
						Contact.Builder builder = new Contact.Builder(); 
						builder.setFirstName("First");
						builder.setLastName("Last");
						builder.setFormattedName("First Last");
						long time= System.currentTimeMillis();
						builder.setContactId(String.valueOf(time));
						Phone [] phones = new Phone[1];
						phones[0] = new Phone("Work", "1234567890", true);
						builder.setPhones(phones);
						Contact contact = builder.build();
						aabManager.CreateContact(contact);
						break;
				}
			}
		});
		
	}
	
	private class getContactsListener implements ATTIAMListener {

		@Override
		public void onSuccess(Object response) {
			contactResultSet = (ContactResultSet) response;
			if (null != contactResultSet) {
				String strText = new String("");
				QuickContact[] quickContacts_arr = contactResultSet.getQuickContacts();
				for (int i=0; i < quickContacts_arr.length; i++) {
					QuickContact qc = quickContacts_arr[i];
					strText += "\n" + qc.getContactId() + ", " + 
								qc.getFormattedName() + ", " + qc.getPhone().getNumber();
				}
				Log.i("getContactsAPI","OnSuccess : ContactID :  " + strText);
				//Log.i("getContactsAPI", "OnSuccess : ContactID :  " +contactResultSet.getQuickContacts()[1].getContactId().toString());
				displayContacts.setText(strText);
				return;
			}
		}

		@Override
		public void onError(InAppMessagingError error) {
			Log.i("getContactsAPI on error", "onError");

		}
	}

	private class getContactListener implements ATTIAMListener {

		@Override
		public void onSuccess(Object response) {
			contactWrapper = (ContactWrapper) response;
			if (null != contactWrapper) {
				String strText = new String("");
				QuickContact qc = contactWrapper.getQuickContact();
				if (null != qc) {
					strText += "\n" + qc.getContactId() + ", " + 
								qc.getFormattedName() + ", " + qc.getPhone().getNumber();
					Log.i("getContactsAPI","OnSuccess : ContactID :  " + strText);
					//Log.i("getContactsAPI", "OnSuccess : ContactID :  " +contactResultSet.getQuickContacts()[1].getContactId().toString());
					displayContacts.setText(strText);
				}
				return;
			}
		}

		@Override
		public void onError(InAppMessagingError error) {
			Log.i("getContactAPI on error", "onError");

		}
	}

	private class getContactGroupsListener implements ATTIAMListener {
		public GroupResultSet groupResultSet;

		@Override
		public void onSuccess(Object response) {
			groupResultSet = (GroupResultSet) response;
			if (null != groupResultSet) {
				String strText = new String("");
				//QuickContact qc = contactWrapper.getQuickContact();
				Group[] groups_arr = groupResultSet.getGroups();
				for (int i=0; i < groups_arr.length; i++) {
					Group grp = groups_arr[i];
					strText += "\n" + grp.getGroupId() + ", " + grp.getGroupName() + ", "  + grp.getGroupType();
					Log.i("getContactGroupsAPI","OnSuccess : ContactID :  " + strText);
					//Log.i("getContactsAPI", "OnSuccess : ContactID :  " +contactResultSet.getQuickContacts()[1].getContactId().toString());
					displayContacts.setText(strText);
				}
				return;
			}
		}

		@Override
		public void onError(InAppMessagingError error) {
			Log.i("getContactGroups on error", "onError");

		}
	}

	private class createContactListener implements ATTIAMListener {
		@Override
		public void onSuccess(Object response) {
			String location = (String) response;
			if (null != location) {
				Log.i("createContactAPI","OnSuccess : Location :  " + location);
				displayContacts.setText(location);
				return;
			}
		}

		@Override
		public void onError(InAppMessagingError error) {
			Log.i("createContactAPI on error", "onError");

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.test_aab, menu);
		return true;
	}

}
