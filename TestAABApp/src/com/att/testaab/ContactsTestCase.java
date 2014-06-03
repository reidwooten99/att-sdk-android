package com.att.testaab;

import com.att.api.aab.manager.AabManager;
import com.att.api.aab.service.Contact;
import com.att.api.aab.service.ContactResultSet;
import com.att.api.aab.service.ContactWrapper;
import com.att.api.aab.service.Group;
import com.att.api.aab.service.GroupResultSet;
import com.att.api.aab.service.PageParams;
import com.att.api.aab.service.Phone;
import com.att.api.aab.service.QuickContact;
import com.att.api.aab.service.SearchParams;
import android.widget.TextView;

public class ContactsTestCase extends AabTestCase {
	public static String newContactId = null;
	public static String lastContactId = null;
	
	public ContactsTestCase(TextView textView, String strLogFilePath) {
		super(textView, strLogFilePath);
	}
	
    protected void setUp() throws Exception {
        super.setUp();
	}
	
	protected void tearDown() throws Exception {
	    super.tearDown();
	}

	public void testGetContacts(String xFields, PageParams pageParams, SearchParams searchParams) {
		aabManager = new AabManager(Config.fqdn, authToken, new getContactsListener());
		aabManager.GetContacts(xFields, pageParams, searchParams);
		return;
	}
	
	private class getContactsListener extends UnitTestListener {

		public getContactsListener() {
			super("GetContacts", display, null);
		}

		@Override
		public void onSuccess(Object response) {
			ContactResultSet contactResultSet = (ContactResultSet) response;			
			if (null != contactResultSet) {
				strText = "\nPassed: " + strTestName + " test.";
				QuickContact[] quickContacts_arr = contactResultSet.getQuickContacts();
				String strPhone = null;
				for (int i=0; i < quickContacts_arr.length; i++) {
					QuickContact qc = quickContacts_arr[i];
					strPhone = (qc.getPhone() != null) ? qc.getPhone().getNumber() : "0001112222";
					strText += "\n" + qc.getContactId() + ", " + 
								qc.getFormattedName() + ", " + strPhone;
					lastContactId = qc.getContactId();
				}
			} else {
				strText = "Unknown: " + strTestName + " test.\nNo data returned.";				
			}
			updateTextDisplay(strText);
			return;
		}
	}

	public void testGetContact(String contactId, String xFields) {
		if (null == contactId) {
			display.setText("Error: Please create a new contact or call GetContacts first.");
			return;
		}
		aabManager = new AabManager(Config.fqdn, authToken, new getContactListener());
		aabManager.GetContact(contactId, xFields);	
		lastContactId = contactId;
		return;
	}

	private class getContactListener extends UnitTestListener {

		public getContactListener() {
			super("GetContact", display, null);
		}

		@Override
		public void onSuccess(Object response) {
			ContactWrapper contactWrapper = (ContactWrapper) response;
			if (null != contactWrapper) {
				QuickContact qc = contactWrapper.getQuickContact();
				Contact c = contactWrapper.getContact();
				String strPhone = null;
				strText = "\nPassed: " + strTestName + " test.";
				if (null != qc) {
					strPhone = (qc.getPhone() != null) ? qc.getPhone().getNumber() : "0001112222";
					strText += "\n" + qc.getContactId() + ", " + 
								qc.getFormattedName() + ", " + strPhone;
				} else if (null != c) {
					strPhone = (c.getPhones() != null && c.getPhones().length > 0) ? c.getPhones()[0].getNumber() : "0001112222";
					strText += "\n" + c.getContactId() + ", " + 
								c.getFormattedName() + ", " + c.getPhones()[0].getNumber();
				}
			} else {
				strText = "Unknown: " + strTestName + " test.\nNo data returned.";				
			}
			updateTextDisplay(strText);
			return;
		}
	}

	public void testCreateContact(String firstName, String lastName) {
		aabManager = new AabManager(Config.fqdn, authToken, new createContactListener());
		Contact.Builder builder = new Contact.Builder(); 
		builder.setFirstName(firstName);
		builder.setLastName(lastName);
		long time= System.currentTimeMillis();
		builder.setContactId(String.valueOf(time));
		Phone [] phones = new Phone[1];
		phones[0] = new Phone("WORK,CELL", "1234567890", true);
		builder.setPhones(phones);
		Contact contact = builder.build();
		aabManager.CreateContact(contact);
		return;
	}

	private class createContactListener extends UnitTestListener {

		public createContactListener() {
			super("CreateContact", display, null);
		}

		@Override
		public void onSuccess(Object response) {
			String location = (String) response;
			if (null != location) {
				strText = "\nPassed: " + strTestName + " test.";
				strText += "\n" + location;				
				String[] locationUrl = location.split("contacts/");
				newContactId = locationUrl[1];
			} else {
				strText = "Unknown: " + strTestName + " test.\nNo data returned.";				
			}
			updateTextDisplay(strText);
			return;
		}
	}


	public void testDeleteContact(String contactId) {
		if (null == contactId) {
			display.setText("Error: Please create a new contact or call GetContacts first.");
			return;
		}
		aabManager = new AabManager(Config.fqdn, authToken, new deleteContactListener());
		aabManager.DeleteContact(contactId);	
		return;
	}
	
	private class deleteContactListener extends UnitTestListener {

		public deleteContactListener() {
			super("DeleteContact", display, null);
		}

		@Override
		public void onSuccess(Object response) {
			String result = (String) response;
			strText = "\nPassed: " + strTestName + " test.";
			strText += "\n" +"DeleteContactAPI : " + " "+ result;
			updateTextDisplay(strText);
			newContactId = null;
			return;
			
		}
	}
	
	public void testGetContactGroups(String contactId, PageParams pageParams) {
		if (null == contactId) {
			display.setText("Error: Please create a new contact or call GetContacts first.");
			return;
		}
		aabManager = new AabManager(Config.fqdn, authToken, new getContactGroupsListener());
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
	
	public void testUpdateContact(String contactId, String firstName, String lastName) {
		if (null == contactId) {
			display.setText("Error: Please create a new contact or call GetContacts first.");
			return;
		}
		aabManager = new AabManager(Config.fqdn, authToken, new updateContactListener());
		Contact.Builder builder = new Contact.Builder(); 
		builder.setFirstName(firstName);
		builder.setLastName(lastName);
		builder.setContactId(contactId);
//		Phone [] phones = new Phone[2];
//		phones[0] = new Phone("WORK,CELL", "1234567890", true);
//		phones[1] = new Phone("HOME,CELL", "1234567800", true);
//		builder.setPhones(phones);
		Contact contact = builder.build();
	
		aabManager.UpdateContact(contact);	
	}
	
	private class updateContactListener extends UnitTestListener {
		
		public updateContactListener() {
			super("UpdateContact", display, null);
		}
		
		@Override
		public void onSuccess(Object response) {
			String result = (String) response;
			strText = "\nPassed: " + strTestName + " test.";
			strText += "\n" +"UpdateContactAPI : " + "  " + result;
			updateTextDisplay(strText);
			return;			
		}	
	}
}
