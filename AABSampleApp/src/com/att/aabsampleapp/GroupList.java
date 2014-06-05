package com.att.aabsampleapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.att.api.aab.manager.AabManager;
import com.att.api.aab.service.Group;
import com.att.api.aab.service.GroupResultSet;
import com.att.api.aab.service.PageParams;
import com.att.api.error.AttSdkError;
import com.att.sdk.listener.AttSdkListener;

public class GroupList extends Activity implements OnClickListener {
	
	private AabManager aabManager;
	private PageParams pageParams;
	private GroupListAdapter adapter;
	
	ListView groupListView;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_all_groups);
		groupListView = (ListView) findViewById(R.id.groupsListViewItem);
	
		Intent intent = getIntent();
		String groupId = intent.getStringExtra("groupId");
		
		aabManager = new AabManager(Config.fqdn, 
									Config.authToken,
									new getGroupsListener());
		pageParams = new PageParams("ASC", "groupName", "2", "0");
		
			aabManager.GetGroups(pageParams, null);
		
		
		groupListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				Group grpResult = (Group) groupListView.getItemAtPosition(position);
				
				CharSequence popUpList[] = new CharSequence[] {"Edit GroupName", "Show Contacts","Delete Group", "Create Group" };
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
				case 0:
					editGroupName(grp.getGroupId());
					break;
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
		AabManager = new AabManager(Config.fqdn, authToken, new deleteGroupListener());
		AabManager.DeleteGroup(deleteGroupID);
	}*/

	public void editGroupName(final String groupId) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Enter Group Name");
		builder.setMessage("Enter the Group name");
		final EditText input = new EditText(getBaseContext());
		builder.setView(input);
		
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
        	public void onClick(DialogInterface dialog, int whichButton) {
   
        	 String editGroupName = input.getEditableText().toString();
        	 
        	 Group group = new Group(groupId, editGroupName, "USER" );
        	 aabManager = new AabManager(Config.fqdn, Config.authToken, new updateGroupListener());
        	 aabManager.UpdateGroup(group);
        	} 
        }); 
		builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
        	  public void onClick(DialogInterface dialog, int whichButton) {
        		  dialog.cancel();
        	  }
        }); 
		builder.create();	
		builder.show();
	}
	
	private class updateGroupListener implements AttSdkListener {

		@Override
		public void onSuccess(Object response) {
			String result = (String) response;
			
			aabManager = new AabManager(Config.fqdn, Config.authToken,new getGroupsListener());
			pageParams = new PageParams("ASC", "groupName", "5", "0");
			aabManager.GetGroups(pageParams, null);
			
			Log.i("updateGroupAPI onSuccess", result);
		}

		@Override
		public void onError(AttSdkError error) {
			Log.i("updateGroupAPI on error", "onError");
			
		}
		
	}
	
	
	private class getGroupsListener implements AttSdkListener {
		public GroupResultSet groupResultSet;
		Group[] groupList;

		@Override
		public void onSuccess(Object response) {
			groupResultSet = (GroupResultSet) response;
			if (null != groupResultSet) {
				 groupList = groupResultSet.getGroups();
				adapter = new GroupListAdapter(getApplicationContext(), groupList);
				groupListView.setAdapter(adapter);
				adapter.notifyDataSetChanged();
				
				for (int i=0; i < groupList.length; i++) {
					Group grp = groupList[i];
					//strText += "\n" + grp.getGroupId() + ", " + grp.getGroupName() + ", "  + grp.getGroupType();
					Log.i("getGroupsAPI","OnSuccess : ContactID :  " + grp.getGroupId());
					//Log.i("getContactsAPI", "OnSuccess : ContactID :  " +contactResultSet.getQuickContacts()[1].getContactId().toString());
					//displayContacts.setText(strText);
				}
				return;
			}
		}

		@Override
		public void onError(AttSdkError error) {
			Log.i("getGroupsAPI on error", "onError");

		}
	}

	@Override
	public void onClick(View v) {
		
	}

	
}
	
