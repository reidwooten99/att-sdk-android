package com.att.aabsampleapp;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

import com.att.api.aab.manager.AabManager;
import com.att.api.aab.service.Contact;
import com.att.api.aab.service.ContactWrapper;
import com.att.api.aab.service.PageParams;
import com.att.api.error.AttSdkError;
import com.att.sdk.listener.AttSdkListener;

public class GroupContactList extends Activity implements OnClickListener{

	private AabManager aabManager;
	private PageParams pageParams;
	private GroupContactListAdapter adapter;
	private ListView groupContactListView;
	private String groupId;
	private ArrayList<Contact> groupContactList;
	 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_contact_list);
		
		groupContactList = new ArrayList<Contact>();
		
		groupContactListView = (ListView) findViewById(R.id.groupContactListViewItem);
		Intent intent = getIntent();
		groupId = intent.getStringExtra("groupId");
		
		pageParams = new PageParams("ASC", "firstName", "12", "0");
		aabManager = new AabManager(Config.fqdn, Config.authToken, new getGroupContactListener());
		aabManager.GetGroupContacts(groupId, pageParams);
	}
	
	
	private class getGroupContactListener implements AttSdkListener {

		@Override
		public void onSuccess(Object response) {
			String strText;
			String[] result = (String[] ) response;
			if (null != result) {
				strText = "\nPassed: " +  " test.";
				for(int i = 0; i < result.length; i++) {
					String  contactId = result[i];
					strText = "\n" + contactId;
					AabManager aabManager = new AabManager(Config.fqdn, Config.authToken, new getContactListener());
					aabManager.GetContact(contactId, " ");
					Log.i("getGroupContactListener on success", "onSuccess" + strText);
				}
		}
		else {
				strText = "Unknown: " +  " test.\nNo data returned.";				
			}
				return;
			
		}

		@Override
		public void onError(AttSdkError error) {
			Log.i("getGroupContactAPI on error", "onError");

		}
	}

	private class getContactListener implements AttSdkListener {

		@Override
		public void onSuccess(Object response) {	
			 ContactWrapper contactWrapper;
			 Contact contact;
			 String strText;
			contactWrapper = (ContactWrapper) response;
			if (null != contactWrapper) { 
				contact = contactWrapper.getContact();
				groupContactList.add(contact);
				
				adapter = new GroupContactListAdapter(getApplicationContext(),  groupContactList);
				groupContactListView.setAdapter(adapter);
				adapter.notifyDataSetChanged();
				
				if (null != contact) {
					strText = "\n" + contact.getContactId() + ", " + 
							contact.getFormattedName();
					Log.i("getContactAPI","OnSuccess : ContactID :  " + strText);
					
				}
			}
		}

		@Override
		public void onError(AttSdkError error) {
			Log.i("getContactAPI on error", "onError");

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.group_contact_list, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

}
