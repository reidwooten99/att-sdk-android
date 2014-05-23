package com.att.testaab;

import android.widget.TextView;

import com.att.api.aab.manager.AabManager;
import com.att.api.aab.service.Group;
import com.att.api.aab.service.GroupResultSet;
import com.att.api.aab.service.PageParams;
import com.att.api.aab.service.SearchParams;

public class GroupsTestCase extends AabTestCase {
	public String newGroupId;
	
	public GroupsTestCase(TextView textView, String strLogFilePath) {
		super(textView, strLogFilePath);
	}
	
	public void testGetGroups(PageParams pageParams, SearchParams searchParams) {
		aabManager = new AabManager(Config.fqdn, authToken, new getGroupsListener());
		aabManager.GetGroups(pageParams, null);
		return;
	}
	
	private class getGroupsListener extends UnitTestListener {

		public getGroupsListener() {
			super("GetGroups", display, null);
		}
		@Override
		public void onSuccess(Object response) {
			GroupResultSet groupResultSet = (GroupResultSet) response;
			if (null != groupResultSet) {
				strText = "\nPassed: " + strTestName + " test.";
				Group[] groups_arr = groupResultSet.getGroups();
				for (int i=0; i < groups_arr.length; i++) {
					Group grp = groups_arr[i];
					strText += "\n" + grp.getGroupId() + ", " + 
							grp.getGroupName() + ", " + grp.getGroupType();				
				}			
			} else {
				strText = "Unknown: " + strTestName + " test.\nNo data returned.";				
			}
			updateTextDisplay(strText);
			return;	
		}

	}
	
	public void testCreateGroup(String groupName, String groupType) {
		aabManager = new AabManager(Config.fqdn, authToken, new createGroupListener());
		Group group = new Group("05058", groupName, groupType);
		aabManager.CreateGroup(group);
	}
	
	private class createGroupListener extends UnitTestListener {

		public createGroupListener() {
			super("CreateGroup", display, null);
		}

		@Override
		public void onSuccess(Object response) {
			String location = (String) response;
			if (null != location) {
				strText = "\nPassed: " + strTestName + " test.";
				strText += "\n" + location;				
				String[] locationUrl = location.split("com/");
				newGroupId = locationUrl[1];
			} else {
				strText = "Unknown: " + strTestName + " test.\nNo data returned.";				
			}
			updateTextDisplay(strText);
			return;
		}
	}
	
	public void testDeleteGroup (String groupId) {	
		aabManager = new AabManager(Config.fqdn, authToken, new deleteGroupListener());
		aabManager.DeleteGroup(groupId);
	}
	
	private class deleteGroupListener extends UnitTestListener {

		public deleteGroupListener() {
			super("DeleteGroup", display, null);
		}

		@Override
		public void onSuccess(Object response) {
			String result = (String) response;
			strText = "\nPassed: " + strTestName + " test.";
			strText += "\n" +"DeleteGroupAPI : " + "  " + result;
			updateTextDisplay(strText);
			return;
			
		}
	}
	
	public void testAddContactsToGroup(String groupId, String contactIds) {
		aabManager = new AabManager(Config.fqdn, authToken, new addContactsToGroupListener());
		aabManager.AddContactsToGroup(groupId, contactIds);
	}
	
	private class addContactsToGroupListener extends UnitTestListener {
		public addContactsToGroupListener() {
			super("AddContactsToGroup",display,null);
		}
		
		@Override
		public void onSuccess(Object response) {
			String result = (String) response;
			if (null != result) {
				strText = "\nPassed: " + strTestName + " test.";
				strText += "\n" +"AddContactsToGroup : " + "  " + result;
			} else {
				strText = "Unknown: " + strTestName + " test.\nNo data returned.";				
			}
			updateTextDisplay(strText);
			return;
		}
	}
	
	public void testGetGroupContacts( String groupId, PageParams params ) {
		aabManager = new AabManager(Config.fqdn, authToken, new getGroupContactsListener());
		aabManager.GetGroupContacts(groupId, params);
	}
	
	private class getGroupContactsListener  extends UnitTestListener {
		public getGroupContactsListener() {
			super("GetGroupContacts",display,null);
		}
		
		@Override
		public void onSuccess(Object response) {
			String[] result = (String[] ) response;
			if (null != result) {
				strText = "\nPassed: " + strTestName + " test.";
				for(int i = 0; i < result.length; i++) {
					String  contactId = result[i];
					strText += "\n" + contactId;
				}			
			} else {
				strText = "Unknown: " + strTestName + " test.\nNo data returned.";				
			}
			updateTextDisplay(strText);
			return;
		}
	}
	
	public void testRemoveContactsFromGroup(String groupId, String contactIds) {
		aabManager = new AabManager(Config.fqdn, authToken, new removeContactsFromGroupListener());
		aabManager.RemoveContactsFromGroup(groupId, contactIds);
	}
	
	private class removeContactsFromGroupListener extends UnitTestListener {
		public removeContactsFromGroupListener() {
			super("RemoveContactsFromGroup", display, null);
		}

		@Override
		public void onSuccess(Object response) {
			String result = (String) response;
			strText = "\nPassed: " + strTestName + " test.";
			strText += "\n" +"RemoveContactsFromGroupAPI : " + "  " + result;
			updateTextDisplay(strText);
			return;
			
		}
	}
	
	public void testUpdateGroup(Group group) {
		aabManager = new AabManager(Config.fqdn, authToken, new updateGroupListener());
		aabManager.UpdateGroup(group);
	}
	
	private class updateGroupListener extends UnitTestListener {
		public updateGroupListener() {
			super("UpdateGroup", display, null);
		}

		@Override
		public void onSuccess(Object response) {
			String result = (String) response;
			strText = "\nPassed: " + strTestName + " test.";
			strText += "\n" +"UpdateGroupAPI : " + "  " + result;
			updateTextDisplay(strText);
			return;
			
		}
	}
 }
