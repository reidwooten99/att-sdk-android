package com.att.testaab;

import android.widget.TextView;

import com.att.api.aab.manager.AABManager;
import com.att.api.aab.service.Contact;
import com.att.api.aab.service.Phone;

public class OtherTestCase extends AabTestCase {

	public OtherTestCase(TextView textView, String strLogFilePath) {
		super(textView, strLogFilePath);
		// TODO Auto-generated constructor stub
	}
	
	public void testGetMyInfo() {
		aabManager = new AABManager(Config.fqdn, authToken, new getMyInfoListener());
		aabManager.GetMyInfo();
	}

	private class getMyInfoListener extends UnitTestListener {
		
		public getMyInfoListener() {
			super("GetMyInfo", display, null);
		}

		@Override
		public void onSuccess(Object response) {
			Contact myInfoContact = (Contact) response;			
			if (null != myInfoContact) {
				strText = "\nPassed: " + strTestName + " test.";
			} else {
				strText = "Unknown: " + strTestName + " test.\nNo data returned.";				
			}
			updateTextDisplay(strText);
			return;
		}
	}
	
	public void testUpdateMyInfo (String firstName, String lastName) {
		aabManager = new AABManager(Config.fqdn, authToken, new updateMyInfoListener());
		Contact.Builder builderForMyInfo = new Contact.Builder(); 
		builderForMyInfo.setFirstName("FirstMyInfo");
		builderForMyInfo.setLastName("LastMyInfo");
		builderForMyInfo.setContactId("3acc524a-0600-4548-a9f2-2d94b9bfcd0e");
//		Phone [] phonesForMyInfo = new Phone[2];
//		phonesForMyInfo[0] = new Phone("WORK,CELL", "1234567890", true);
//		phonesForMyInfo[1] = new Phone("HOME,CELL", "1234567800", true);
//		builderForMyInfo.setPhones(phonesForMyInfo);
		Contact contactForMyInfo = builderForMyInfo.build();
		
		aabManager.UpdateMyInfo(contactForMyInfo);
	}
	
private class updateMyInfoListener extends UnitTestListener {
		
		public updateMyInfoListener() {
			super("UpdateMyInfo", display, null);
		}

		@Override
		public void onSuccess(Object response) {
			String result = (String) response;		
			strText = "\nPassed: " + strTestName + " test.";
			strText += "\n" +"UpdateMyInfoAPI : " + " "+ result;
			updateTextDisplay(strText);
			return;
		}
	}
}
