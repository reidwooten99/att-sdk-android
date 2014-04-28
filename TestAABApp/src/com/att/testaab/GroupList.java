package com.att.testaab;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.att.api.aab.listener.ATTIAMListener;
import com.att.api.aab.manager.AABManager;
import com.att.api.aab.service.Group;
import com.att.api.aab.service.GroupResultSet;
import com.att.api.aab.service.PageParams;
import com.att.api.error.InAppMessagingError;
import com.att.api.oauth.OAuthToken;
public class GroupList extends Activity implements OnClickListener {
	
	private AABManager aabManager;
	private PageParams pageParams;
	private GroupListAdapter adapter;
	private String contactId;
	ListView groupListView;
	private Button grpNew;
	private Button grpSettings;
	private Button grpContacts;
	private Button grpMyInfo;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        OAuthToken authToken = new OAuthToken(Config.token, Config.accessTokenExpiry, Config.refreshToken);

        super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_group_list);
		groupListView = (ListView) findViewById(R.id.groupsListViewItem);
		
		grpNew = (Button) findViewById(R.id.newGrp);
		grpNew.setOnClickListener(this);
		
		grpSettings = (Button) findViewById(R.id.settings);
		grpSettings.setOnClickListener(this);
		
		grpContacts = (Button) findViewById(R.id.contacts);
		grpContacts.setOnClickListener(this);
		
		grpMyInfo = (Button) findViewById(R.id.myInfo);
		grpMyInfo.setOnClickListener(this);
		
		
		Intent intent = getIntent();
		contactId = intent.getStringExtra("contactId");
		
		aabManager = new AABManager(Config.fqdn, 
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

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent;
		switch(v.getId()) {	
		case R.id.myInfo :
			 intent = new Intent(GroupList.this, ContactDetails.class);
			intent.putExtra("contactId", contactId);
			startActivity(intent);	
			break;
			
		case R.id.contacts :
			 intent = new Intent(GroupList.this, ContactsList.class);
			startActivity(intent);
			break;
			
		case R.id.newGrp :
			Toast.makeText(getApplicationContext(), "TO BE IMPLEMENTED", Toast.LENGTH_LONG).show();
			break;
			
		case R.id.settings :
			Toast.makeText(getApplicationContext(), "TO BE IMPLEMENTED", Toast.LENGTH_LONG).show();
			break;
		
		}
	}
}
	
