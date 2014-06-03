package com.att.testaab;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.att.api.aab.manager.AabManager;
import com.att.api.aab.service.Group;
import com.att.api.aab.service.PageParams;
import com.att.api.aab.service.SearchParams;
import com.att.api.error.AttSdkError;
import com.att.api.oauth.OAuthToken;
import com.att.sdk.listener.AttSdkListener;

public class TestAAB extends Activity implements OnClickListener {

	private AabManager aabManager;
	private PageParams pageParams;
	private SearchParams searchParams;
	private Button btnRun;
	private Button btnClear;
	
	private TextView displayContacts;
	//private ContactWrapper contactWrapper;	
	
	private EditText testApi;

	private final int OAUTH_CODE = 1;
	
	
	 OAuthToken authToken = new OAuthToken(Config.token, Config.accessTokenExpiry, Config.refreshToken);
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_test_aab);
		
		btnRun = (Button) findViewById(R.id.getContactsBtn);
		btnClear = (Button) findViewById(R.id.clearbutton);
		displayContacts = (TextView)findViewById(R.id.displayContacts1);
		
		testApi = (EditText)findViewById(R.id.editText1);
		testApi.setText("1"); // set default to 2
		
		
		pageParams = new PageParams("ASC", "firstName", "2", "0");
		//SearchParams.Builder builder = new SearchParams.Builder();
		//searchParams = new SearchParams(builder.setZipcode("94086"));
		
		btnClear.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				displayContacts.setText("");
			}
		});
	
		
		btnRun.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ContactsTestCase ctc = new ContactsTestCase(displayContacts, null);
				GroupsTestCase gtc = new GroupsTestCase(displayContacts, null);
				OtherTestCase otc  = new OtherTestCase(displayContacts, null);
				
			    String apiNumber = testApi.getText().toString();
                int iOperation = 1;
                try { iOperation = Integer.valueOf(apiNumber); }
                catch(Exception e) {}
                
//                1 2 - contacts, display contact
//                7 8 7 - groups, create group, groups
//                13 11 13 - group contacts, add contact to group, group contacts
//                3 - contact groups
//                4 1 2 create contact, contacts, display
//                5 1 2 update contact, contacts, display
//                6 1 delete contact, contacts
                
				switch (iOperation) {
				
					case 1: //GetContacts
							ctc.testGetContacts("shallow", pageParams, searchParams);
							break;
						
					case 2: //GetContact
							ctc.testGetContact(ContactsTestCase.lastContactId, "shallow");	
							break;
						
					case 3: //GetContactGroups
							pageParams = new PageParams("ASC", "groupName", "12", "0");
							ctc.testGetContactGroups(ContactsTestCase.lastContactId, pageParams);
							break;
						
					case 4: //CreateContact
							ctc.testCreateContact("TestFirstFive", "Last");
							break;
						
					case 5: //UpdateContact, Fist and Last, Verify, Switch back
							//ctc.testUpdateContact("12CE1C28362013082800371294D457585C50A2B1", "FirstView", "LastUsage");
							ctc.testUpdateContact(ContactsTestCase.newContactId, "Last", "TestFirstFive");
							// Verify and then switch back
							ctc.testUpdateContact(ContactsTestCase.newContactId, "TestFirstFive", "Last");
							break; 
					
					case 6: //DeleteContact
							ctc.testDeleteContact(ContactsTestCase.newContactId);
							break;
						
					case 7 : //GetGroups
							pageParams = new PageParams("ASC", "groupName", "2", "0");
							gtc.testGetGroups( pageParams, null);
							break;
							
					case 8:  //CreateGroup 
							gtc.testCreateGroup("TestGroup8","USER");
							break;
						
					case 9: //DeleteGroup
							gtc.testDeleteGroup(GroupsTestCase.newGroupId);
							break;
						
					case 10: //UpdateGroup
							Group group = new Group(GroupsTestCase.newGroupId, "TestUpdateGroup", "USER" );
							gtc.testUpdateGroup(group);
							break;
						
					case 11: //AddContactsToGroup
							gtc.testAddContactsToGroup(GroupsTestCase.newGroupId, ContactsTestCase.lastContactId);
							break;
					
					case 12: //RemoveContactsFromGroup
							gtc.testRemoveContactsFromGroup(GroupsTestCase.newGroupId, ContactsTestCase.lastContactId);
							break;
					
					case 13: //GetGroupContacts
							pageParams = new PageParams("ASC", "firstName", "10", "0");
							gtc.testGetGroupContacts(GroupsTestCase.lastGroupId, pageParams);
							break;
							
					case 14: //GetMyInfo
							otc.testGetMyInfo();
							break;
					
					case 15: //UpdateMyInfo,  Switch first and last
							otc.testUpdateMyInfo("LastMyInfo", "FirstMyInfo");
							// Verify that two are switched. and change back to original
							otc.testUpdateMyInfo("FirstMyInfo", "LastMyInfo");
							break;					
					
					case 16: //DeleteLastContact
						ctc.testDeleteContact(ContactsTestCase.lastContactId);
						break;
					case 17: //DeleteLastGroup
						gtc.testDeleteGroup(GroupsTestCase.lastGroupId);
						break;
				}
			}
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.test_aab, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent i;
		switch (item.getItemId()) {
			
			case R.id.action_logout : 
				logOutOfAddressBook();
				break;
				
			case R.id.action_login :
				logIntoAddressBook(Config.fqdn,Config.clientID,Config.secretKey,Config.redirectUri,Config.appScope);
				break;
			
			case R.id.action_contacts :
				 i = new Intent(TestAAB.this, ContactsList.class);
				startActivity(i);	
				break;
			
			case R.id.action_groups :
				Intent intent = new Intent(TestAAB.this, GroupList.class);
				intent.putExtra("contactId", "-1");
				startActivity(intent);
				break;
			
			case R.id.action_tabview :
				 i = new Intent(TestAAB.this, ContactsTabView.class);
				//Intent i = new Intent(TestAAB.this, ContactsFragmentView.class);
				startActivity(i);
				break;
			
			default :
				return super.onOptionsItemSelected(item);
		
		}
		return true;
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == OAUTH_CODE) {
			String oAuthCode = null;
			if (resultCode == RESULT_OK) {
				oAuthCode = data.getStringExtra("oAuthCode");
				Log.i("TestAAB", "oAuthCode:" + oAuthCode);
				if (null != oAuthCode) {
					aabManager = new AabManager(Config.fqdn, Config.clientID,Config.secretKey,new getTokenListener());
					aabManager.getOAuthToken(oAuthCode);
				} else {
					Log.i("TestAAB", "oAuthCode: is null");

				}
			} else if(resultCode == RESULT_CANCELED) {
						oAuthCode = data.getStringExtra("oAuthCode");
						Log.i("TestAAB", "oAuthCode:" + oAuthCode);
						if (null != oAuthCode) {
							aabManager = new AabManager(Config.fqdn, Config.clientID,Config.secretKey,new getTokenListener());
							aabManager.getOAuthToken(oAuthCode);
						} else {
							Log.i("TestAAB", "oAuthCode: is null");

				}
								
			}
		} 
	}

	

	public void logOutOfAddressBook() {
		CookieSyncManager.createInstance(this);
		CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.removeAllCookie();		
	}

	public void logIntoAddressBook(String fqdn, String clientId, String secretKey, String refirectUri, String appScope) {
		Intent i = new Intent(TestAAB.this, com.att.api.consentactivity.UserConsentActivity.class);
		i.putExtra("fqdn", Config.fqdn);
		i.putExtra("clientId", Config.clientID);
		i.putExtra("clientSecret", Config.secretKey);
		i.putExtra("redirectUri", Config.redirectUri);
		i.putExtra("appScope", Config.appScope);
		startActivityForResult(i, OAUTH_CODE);
		
	}

	public class getTokenListener implements AttSdkListener {

		@Override
		public void onSuccess(Object response) {
			OAuthToken authToken = (OAuthToken) response;
			if (null != authToken) {
				Config.token = authToken.getAccessToken();
				// Config.accessTokenExpiry = authToken.getAccessTokenExpiry();
				Config.refreshToken = authToken.getRefreshToken();
				Log.i("getTokenListener",
						"onSuccess Message : " + authToken.getAccessToken());
				Intent i = new Intent(TestAAB.this, ContactsList.class);
				startActivity(i);	
			}
		}

		@Override
		public void onError(AttSdkError error) {
			/*authToken = new OAuthToken("abcd", 1, "xyz");
			Config.token = authToken.getAccessToken().toString();*/
			Log.i("getTokenListener",
					"onError Message : " );
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}


}
