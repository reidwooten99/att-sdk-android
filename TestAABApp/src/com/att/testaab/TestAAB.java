package com.att.testaab;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.att.api.aab.service.AABManager;
import com.att.api.aab.service.ContactResultSet;
import com.att.api.aab.service.ContactWrapper;
import com.att.api.aab.service.PageParams;
import com.att.api.aab.service.QuickContact;
import com.att.api.aab.service.SearchParams;
import com.att.api.error.InAppMessagingError;
import com.att.api.immn.listener.ATTIAMListener;
import com.att.api.oauth.OAuthToken;

public class TestAAB extends Activity {

	private AABManager aabManager;
	private PageParams pageParams;
	private SearchParams searchParams;
	private ContactResultSet contactResultSet;
	private OAuthToken authToken;
	private Button getContacts;
	private TextView displayContacts;
	private ContactWrapper contactWrapper;	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_aab);
		getContacts = (Button) findViewById(R.id.getContactsBtn);
		displayContacts = (TextView)findViewById(R.id.displayContacts1);
		
		pageParams = new PageParams("ASC", "firstName", "2", "0");
		SearchParams.Builder builder = new SearchParams.Builder();
		searchParams = new SearchParams(builder.setZipcode("94086"));
		
		getContacts.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int iApi = 2; // use to switch between GetContacts=1 and GetContact=2
				switch (iApi) {
					case 1:
						aabManager = new AABManager("http://ldev.code-api-att.com:8888", 
													authToken,
													new getContactsListener());
						aabManager.GetContacts("shallow", pageParams, searchParams );
						break;
					case 2:
						aabManager = new AABManager("http://ldev.code-api-att.com:8888", 
													authToken,
													new getContactListener());
						//aabManager.GetContact("09876544321", "shallow");
						aabManager.GetContact("0987654432123", "shallow");	
						break;
				}
			}
		});
		
	}
	
	private class getContactsListener implements ATTIAMListener {

		@Override
		public void onSuccess(Object response) {
			contactResultSet = (ContactResultSet) response;
			if (null != contactResultSet) {
				String strText = new String("");
				QuickContact[] quickContacts_arr = contactResultSet.getQuickContacts();
				for (int i=0; i < quickContacts_arr.length; i++) {
					QuickContact qc = quickContacts_arr[i];
					strText += "\n" + qc.getContactId() + ", " + 
								qc.getFormattedName() + ", " + qc.getPhone().getNumber();
				}
				Log.i("getContactsAPI","OnSuccess : ContactID :  " + strText);
				//Log.i("getContactsAPI", "OnSuccess : ContactID :  " +contactResultSet.getQuickContacts()[1].getContactId().toString());
				displayContacts.setText(strText);
				return;
			}
		}

		@Override
		public void onError(InAppMessagingError error) {
			Log.i("getContactsAPI on error", "onError");

		}
	}

	private class getContactListener implements ATTIAMListener {

		@Override
		public void onSuccess(Object response) {
			contactWrapper = (ContactWrapper) response;
			if (null != contactWrapper) {
				String strText = new String("");
				QuickContact qc = contactWrapper.getQuickContact();
				if (null != qc) {
					strText += "\n" + qc.getContactId() + ", " + 
								qc.getFormattedName() + ", " + qc.getPhone().getNumber();
					Log.i("getContactsAPI","OnSuccess : ContactID :  " + strText);
					//Log.i("getContactsAPI", "OnSuccess : ContactID :  " +contactResultSet.getQuickContacts()[1].getContactId().toString());
					displayContacts.setText(strText);
				}
				return;
			}
		}

		@Override
		public void onError(InAppMessagingError error) {
			Log.i("getContactAPI on error", "onError");

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.test_aab, menu);
		return true;
	}

}
