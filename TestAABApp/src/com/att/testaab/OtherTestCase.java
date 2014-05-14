package com.att.testaab;

import android.widget.TextView;

import com.att.api.aab.manager.AABManager;
import com.att.api.aab.service.Contact;

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
	
	public void testUpdateMyInfo (Contact contact) {
		aabManager = new AABManager(Config.fqdn, authToken, new updateMyInfoListener());
		aabManager.UpdateMyInfo(contact);
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
