package com.att.testaab;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
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
		if(contactId == "-1") {
			aabManager.GetGroups(pageParams, null);
		} else {
			aabManager.GetContactGroups(contactId, pageParams);	
		}
		
		groupListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub
				Group grpResult = (Group) groupListView.getItemAtPosition(position);
				
				CharSequence popUpList[] = new CharSequence[] {"Edit GroupName", "Show Contacts","Delete Group" };
				popUpActionList(popUpList, grpResult, position);
				return true;
			}
		});
	}
	
	public void popUpActionList(final CharSequence popUpList[],
			final Group grp, int position) {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Groups Options");
		builder.setItems(popUpList, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int options) {
				switch (options) {
				case 2:
					 // deleteGroup(grp); 
					break;
			default:
					break;
				}
			}
		});
		builder.show();
	}
	
	/*public void deleteGroup(Group grp) {
		String deleteGroupID;

		deleteGroupID = grp.getGroupId();
		aabManager = new AABManager(Config.fqdn, authToken, new deleteGroupListener());
		aabManager.DeleteGroup(deleteGroupID);
	}*/

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
