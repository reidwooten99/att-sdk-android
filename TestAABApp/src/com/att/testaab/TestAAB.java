package com.att.testaab;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import com.att.api.aab.service.AABManager;
import com.att.api.aab.service.ContactResultSet;
import com.att.api.aab.service.PageParams;
import com.att.api.aab.service.SearchParams;
import com.att.api.error.InAppMessagingError;
import com.att.api.immn.listener.ATTIAMListener;
import com.att.api.oauth.OAuthToken;

public class TestAAB extends Activity {

	AABManager aabManager;
	PageParams pageParams;
	SearchParams searchParams;
	ContactResultSet contactResultSet;
	private OAuthToken authToken;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_aab);
		pageParams = new PageParams("ASC", "firstName", "2", "0");
		SearchParams.Builder builder = new SearchParams.Builder();
		searchParams = new SearchParams(builder.setZipcode("94086"));
		aabManager = new AABManager("http://ldev.code-api-att.com:8888", 
									authToken,
									new getContactsListener());
		aabManager.GetContacts("shallow", pageParams, searchParams );
		
	}
	
	private class getContactsListener implements ATTIAMListener {

		@Override
		public void onSuccess(Object response) {
			contactResultSet = (ContactResultSet) response;
			if (null != contactResultSet) {
				Log.i("getContactsAPI","OnSuccess : ContactID :  " + contactResultSet.getQuickContacts()[0].getContactId().toString());
				Log.i("getContactsAPI", "OnSuccess : ContactID :  " +contactResultSet.getQuickContacts()[1].getContactId().toString());

				return;
			}
		}

		@Override
		public void onError(InAppMessagingError error) {
			Log.i("getContactsAPI on error", "onError");

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.test_aab, menu);
		return true;
	}

}
