package com.att.testaab;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ListView;

import com.att.api.aab.listener.ATTIAMListener;
import com.att.api.aab.service.AABManager;
import com.att.api.aab.service.Group;
import com.att.api.aab.service.GroupResultSet;
import com.att.api.aab.service.PageParams;
import com.att.api.error.InAppMessagingError;
import com.att.api.oauth.OAuthToken;

public class GroupList extends Activity {
	
	private AABManager aabManager;
	private PageParams pageParams;
	private OAuthToken authToken;
	private GroupListAdapter adapter;
	private String contactId;
	ListView groupListView;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_list);
		groupListView = (ListView) findViewById(R.id.groupsListViewItem);

		Intent intent = getIntent();
		contactId = intent.getStringExtra("contactId");
		
		aabManager = new AABManager("http://ldev.code-api-att.com:8888", 
									authToken,
									new getContactGroupsListener());
		pageParams = new PageParams("ASC", "firstName", "2", "0");
		aabManager.GetContactGroups(contactId, pageParams);	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.group_list, menu);
		return true;
	}
	
	private class getContactGroupsListener implements ATTIAMListener {
		public GroupResultSet groupResultSet;
		Group[] groupList;

		@Override
		public void onSuccess(Object response) {
			// TODO Auto-generated method stub
			groupResultSet = (GroupResultSet) response;
			if (null != groupResultSet) {
				 groupList = groupResultSet.getGroups();
				adapter = new GroupListAdapter(getApplicationContext(), groupList);
				groupListView.setAdapter(adapter);
				adapter.notifyDataSetChanged();
				
				for (int i=0; i < groupList.length; i++) {
					Group grp = groupList[i];
					//strText += "\n" + grp.getGroupId() + ", " + grp.getGroupName() + ", "  + grp.getGroupType();
					Log.i("getContactGroupsAPI","OnSuccess : ContactID :  " + grp.getGroupId());
					//Log.i("getContactsAPI", "OnSuccess : ContactID :  " +contactResultSet.getQuickContacts()[1].getContactId().toString());
					//displayContacts.setText(strText);
				}
				return;
			}
		}

		@Override
		public void onError(InAppMessagingError error) {
			// TODO Auto-generated method stub
			Log.i("getContactsAPI on error", "onError");

		}
	}

}
