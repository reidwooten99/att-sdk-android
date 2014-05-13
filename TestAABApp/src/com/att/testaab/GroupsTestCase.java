package com.att.testaab;

import android.widget.TextView;

import com.att.api.aab.manager.AABManager;
import com.att.api.aab.service.Group;
import com.att.api.aab.service.GroupResultSet;
import com.att.api.aab.service.PageParams;
import com.att.api.aab.service.SearchParams;

public class GroupsTestCase extends AabTestCase {
	public String newGroupId;
	
	public GroupsTestCase(TextView textView, String strLogFilePath) {
		super(textView, strLogFilePath);
		// TODO Auto-generated constructor stub
	}
	
	public void testGetGroups(PageParams pageParams, SearchParams searchParams) {
		aabManager = new AABManager(Config.fqdn, authToken, new getGroupsListener());
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
	
	public void testGetContactGroups(String contactId, PageParams pageParams) {
		aabManager = new AABManager(Config.fqdn, authToken, new getContactGroupsListener());
		aabManager.GetContactGroups(contactId, pageParams);	
	}
	
	private class getContactGroupsListener extends UnitTestListener {

		public getContactGroupsListener() {
			super("GetContactGroups", display, null);
		}

		@Override
		public void onSuccess(Object response) {
			GroupResultSet groupResultSet = (GroupResultSet) response;
			if (null != groupResultSet) {
				strText = "\nPassed: " + strTestName + " test.";
				Group[] groups_arr = groupResultSet.getGroups();
				for (int i=0; i < groups_arr.length; i++) {
					Group grp = groups_arr[i];
					strText += "\n" + grp.getGroupId() + ", " + grp.getGroupName() + ", "  + grp.getGroupType();
				}
			} else {
				strText = "Unknown: " + strTestName + " test.\nNo data returned.";				
			}
			updateTextDisplay(strText);
			return;
		}
	}
	
	public void testCreateGroup( Group group) {
		aabManager = new AABManager(Config.fqdn, authToken, new createGroupListener());
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
		aabManager = new AABManager(Config.fqdn, authToken, new deleteGroupListener());
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
		aabManager = new AABManager(Config.fqdn, authToken, new addContactsToGroupListener());
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
 }
