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
import android.widget.AdapterView.OnItemClickListener;
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
	private String contactId;
	public static String newGroupId = null;
	ListView groupListView;
	private String selectedGroupId;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_all_groups);
		groupListView = (ListView) findViewById(R.id.groupsListViewItem);
	
		Intent intent = getIntent();
		groupId = intent.getStringExtra("groupId");
		contactId = intent.getStringExtra("contactId");
		
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
				
				CharSequence popUpList[] = new CharSequence[] {"Update Group", "Show Contacts","Delete Group", 
																"Create Group" ,"Add Contact to this Group", "Remove Contact from this group"};
				popUpActionList(popUpList, grpResult, position);
				return true;
			}
		});
		
		groupListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
				//  Disable on click
				
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
					
				case 1: //GetGroupContacts
					getGroupContacts(grp.getGroupId(), pageParams);
					break;
					
				case 2: //Delete Group
					  deleteGroup(grp); 
					  break;
					
				case 3: //Create Group
					isNewGroup = true;
					createGroup(isNewGroup);
					break;
					
				case 4: //Add Contact to Group
					selectGroup( grp, contactId);
					break;
			default:
					break;
				}
			}
		});
		builder.show();
	}
	
	public void getGroupContacts(String groupId, PageParams pageParams) {
		
		/*Intent i = new Intent(GroupList.this, ContactDetails.class);
		i.putExtra("contactId", contactId);
		startActivity(i);*/
		pageParams = new PageParams("ASC", "firstName", "10", "0");
		aabManager = new AabManager(Config.fqdn, Config.authToken, new getGroupContactsListener());
		aabManager.GetGroupContacts(groupId, pageParams);	
	}
	
	public void deleteGroup(final Group grp) {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Delete Group");
		builder.setMessage("Do you want to delete the group :  " + grp.getGroupName() + "?" );
		
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				String deleteGroupID;
				deleteGroupID = grp.getGroupId();
				aabManager = new AabManager(Config.fqdn, Config.authToken, new deleteGroupListener());
				aabManager.DeleteGroup(deleteGroupID);			
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
	
	public void selectGroup(final Group group, final String contactId) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Select Group");
		builder.setMessage("Do you want to select the group :  " + group.getGroupName() + "?" );
		
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				selectedGroupId = group.getGroupId();
				aabManager = new AabManager(Config.fqdn, Config.authToken, new addContactsToGroupListener());
				aabManager.AddContactsToGroup(selectedGroupId, contactId);			
			}
			
		});
		
		builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
      	  public void onClick(DialogInterface dialog, int whichButton) {
      		  dialog.cancel();
      	  }
      }); 
		builder.create();	
		builder.show();	}
	
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
	
	
	private class deleteGroupListener implements AttSdkListener {

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
			Log.i("deleteGroupAPI on error", "onError");
		}
		
	}
	private class createGroupListener implements AttSdkListener {

		@Override
		public void onSuccess(Object response) {
			
			String result = (String) response;
			
			String[] locationUrl = result.split("com/");
			newGroupId = locationUrl[1];
			
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
	
	private class getGroupContactsListener implements AttSdkListener {
		String  contactId ;

		@Override
		public void onSuccess(Object response) {
			String strText;
			String[] result = (String[] ) response;
			if (null != result) {
				strText = "\nPassed: " +  " test.";
				for(int i = 0; i < result.length; i++) {
					String  contactId = result[i];
					strText += "\n" + contactId;
				}
				
				Intent i = new Intent(GroupList.this, ContactDetails.class);
				i.putExtra("contactId", contactId);
				startActivity(i);
			}
		else {
				strText = "Unknown: " +  " test.\nNo data returned.";				
			}
				return;
			
		}

		@Override
		public void onError(AttSdkError error) {
			Log.i("getGroupsAPI on error", "onError");

		}
	}

	private class addContactsToGroupListener implements AttSdkListener {
		String  contactId ;

		@Override
		public void onSuccess(Object response) {
			String strText;
			String result = (String) response;
			if (null != result) {
				strText = "\nPassed: " +  " test.";
				strText += "\n" +"AddContactsToGroup : " + "  " + result;
			} else {
				strText = "Unknown: " +  " test.\nNo data returned.";				
			}
			//getGroupContacts(selectedGroupId, pageParams);	
			Log.i("addContactsToGroupAPI on success ", "Success" );
			finish();
		}

		@Override
		public void onError(AttSdkError error) {
			Log.i("addContactsToGroupAPI on error", "onError");

		}
	}

	@Override
	public void onClick(View v) {
	}

	

	
}
	
