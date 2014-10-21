package com.att.aabsampleapp;

import com.att.api.aab.manager.AabManager;
import com.att.api.aab.service.Group;
import com.att.api.aab.service.GroupResultSet;
import com.att.api.aab.service.PageParams;
import com.att.api.error.AttSdkError;
import com.att.sdk.listener.AttSdkListener;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

public class ContactGroupList extends Activity implements OnClickListener {

	private AabManager aabManager;
	private PageParams pageParams;
	private ContactGroupListAdapter adapter;
	private String contactId;
	public static String newGroupId = null;
	ListView contactGroupListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact_group_list);

		contactGroupListView = (ListView) findViewById(R.id.contactGroupsListViewItem);
		Intent intent = getIntent();
		contactId = intent.getStringExtra("contactId");

		pageParams = new PageParams("ASC", "groupName", "12", "0");
		aabManager = new AabManager(new getContactGroupsListener());
		aabManager.GetContactGroups(contactId, pageParams);
	}

	private class getContactGroupsListener extends AttSdkSampleListener {

		public getContactGroupsListener() {
			super("getContactGroupsAPI");
		}

		public GroupResultSet groupResultSet;
		Group[] groupList;

		@Override
		public void onSuccess(Object response) {
			groupResultSet = (GroupResultSet) response;
			if (null != groupResultSet) {
				groupList = groupResultSet.getGroups();
				adapter = new ContactGroupListAdapter(getApplicationContext(),
						groupList);
				contactGroupListView.setAdapter(adapter);
				adapter.notifyDataSetChanged();

				for (int i = 0; i < groupList.length; i++) {
					Group grp = groupList[i];
					Log.i("getContactGroupsAPI", "OnSuccess : ContactID :  "
							+ grp.getGroupId());
				}
				return;
			}
		}

		@Override
		public void onError(AttSdkError error) {
			super.onError(error);
		}		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.contact_group_list, menu);

		return true;
	}

	@Override
	public void onClick(View v) {

	}

}
