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
	private boolean isNewGroup = false;
	private String groupId;
	
	ListView groupListView;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_all_groups);
		groupListView = (ListView) findViewById(R.id.groupsListViewItem);
	
		Intent intent = getIntent();
		groupId = intent.getStringExtra("groupId");
		
		aabManager = new AabManager(Config.fqdn, 
									Config.authToken,
									new getGroupsListener());
		pageParams = new PageParams("ASC", "groupName", "10", "0");
		
			aabManager.GetGroups(pageParams, null);
		
		
		groupListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				Group grpResult = (Group) groupListView.getItemAtPosition(position);
				
				CharSequence popUpList[] = new CharSequence[] {"Update Group", "Show Contacts","Delete Group", "Create Group" };
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
				case 0: //Update GroupName
					isNewGroup = false;
					editGroupName(grp.getGroupId(), isNewGroup);
					break;
					
				case 1: //Show Contacts
					break;
					
				case 2: //Delete Group
					 // deleteGroup(grp); 
					break;
					
				case 3: //Create Group
					isNewGroup = true;
					createGroup(isNewGroup);
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
	
	public void createGroup(boolean isNewGroup) {
		
		editGroupName(null, isNewGroup);
	}

	public void editGroupName( final String grpId, final boolean isNewGroup) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Enter Group Name");
		builder.setMessage("Enter the Group name");
		final EditText input = new EditText(getBaseContext());
		builder.setView(input);
		
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
        	public void onClick(DialogInterface dialog, int whichButton) {
   
        	 String editGroupName = input.getEditableText().toString();
        	 if(!isNewGroup) {
        		 Group group = new Group(grpId, editGroupName, "USER" );
        		 aabManager = new AabManager(Config.fqdn, Config.authToken, new updateGroupListener());
        		 aabManager.UpdateGroup(group);
        	 	}
        	 else {
        		 
        		 Group group = new Group ("",editGroupName, "USER" );
        		 aabManager = new AabManager(Config.fqdn, Config.authToken, new createGroupListener());
        		 aabManager.CreateGroup(group);
        	 }
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
	
	private class createGroupListener implements AttSdkListener {

		@Override
		public void onSuccess(Object response) {
			
			String result = (String) response;
			
			aabManager = new AabManager(Config.fqdn, Config.authToken,new getGroupsListener());
			pageParams = new PageParams("ASC", "groupName", "10", "0");
			aabManager.GetGroups(pageParams, null);
			
			Log.i("createGroupAPI onSuccess", result);
		}

		@Override
		public void onError(AttSdkError error) {
			Log.i("createGroupAPI on error", "onError");
			
		}
		
	}
	
	
	private class updateGroupListener implements AttSdkListener {

		@Override
		public void onSuccess(Object response) {
			String result = (String) response;
			
			aabManager = new AabManager(Config.fqdn, Config.authToken,new getGroupsListener());
			pageParams = new PageParams("ASC", "groupName", "10", "0");
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
	
