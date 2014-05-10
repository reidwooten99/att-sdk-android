package com.att.testaab;

import com.att.api.aab.manager.AABManager;
import com.att.api.aab.service.Contact;
import com.att.api.aab.service.ContactResultSet;
import com.att.api.aab.service.ContactWrapper;
import com.att.api.aab.service.PageParams;
import com.att.api.aab.service.Phone;
import com.att.api.aab.service.QuickContact;
import com.att.api.aab.service.SearchParams;
import android.widget.TextView;

public class ContactsTestCase extends AabTestCase {
	public String newContactId;
	
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
		aabManager = new AABManager(Config.fqdn, authToken, new getContactsListener());
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
				for (int i=0; i < quickContacts_arr.length; i++) {
					QuickContact qc = quickContacts_arr[i];
					strText += "\n" + qc.getContactId() + ", " + 
								qc.getFormattedName() + ", " + qc.getPhone().getNumber();					
				}
			} else {
				strText = "Unknown: " + strTestName + " test.\nNo data returned.";				
			}
			updateTextDisplay(strText);
			return;
		}
	}

	public void testGetContact(String contactId, String xFields) {
		aabManager = new AABManager(Config.fqdn, authToken, new getContactListener());
		aabManager.GetContact(contactId, xFields);	
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
				strText = "\nPassed: " + strTestName + " test.";
				if (null != qc) {
					strText += "\n" + qc.getContactId() + ", " + 
								qc.getFormattedName() + ", " + qc.getPhone().getNumber();
				} else if (null != c) {
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
		aabManager = new AABManager(Config.fqdn, authToken, new createContactListener());
		Contact.Builder builder = new Contact.Builder(); 
		builder.setFirstName(firstName);
		builder.setLastName(lastName);
		long time= System.currentTimeMillis();
		builder.setContactId(String.valueOf(time));
		Phone [] phones = new Phone[1];
		phones[0] = new Phone("Work", "1234567890", true);
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
		aabManager = new AABManager(Config.fqdn, authToken, new deleteContactListener());
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
			strText += "\n" +"DeleteContactAPI : " + "\n" + result;
			updateTextDisplay(strText);
			return;
			
		}
	}

}
