package com.att.testaab;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.att.api.aab.listener.ATTIAMListener;
import com.att.api.aab.manager.AABManager;
import com.att.api.aab.service.Contact;
import com.att.api.aab.service.Group;
import com.att.api.aab.service.PageParams;
import com.att.api.aab.service.Phone;
import com.att.api.aab.service.SearchParams;
import com.att.api.error.InAppMessagingError;
import com.att.api.oauth.OAuthToken;

public class TestAAB extends Activity implements OnClickListener {

	private AABManager aabManager;
	private PageParams pageParams;
	private SearchParams searchParams;
	private Button getContacts;
	private TextView displayContacts;
	//private ContactWrapper contactWrapper;	
	private Button BtnContactsList;
	private EditText testApi;
	private String strText = "";
	private Button btnGroups;
	private final int OAUTH_CODE = 1;
	private Button btnLogIn;
	private Button btnLogOut;
	private Button btnTabView;
	private String groupSubscriberId;
	
	 OAuthToken authToken = new OAuthToken(Config.token, Config.accessTokenExpiry, Config.refreshToken);
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_test_aab);
		getContacts = (Button) findViewById(R.id.getContactsBtn);
		displayContacts = (TextView)findViewById(R.id.displayContacts1);
		BtnContactsList = (Button)findViewById(R.id.contactsListView);
		testApi = (EditText)findViewById(R.id.editText1);
		testApi.setText("1"); // set default to 2
		
		btnLogIn = (Button) findViewById(R.id.btnLogin);
		btnLogIn.setOnClickListener(this);
		btnLogOut = (Button) findViewById(R.id.btnLogout);
		btnLogOut.setOnClickListener(this);
		btnTabView = (Button) findViewById(R.id.ContactsTabView);
		btnTabView.setOnClickListener(this);
		
		
		btnGroups = (Button) findViewById(R.id.btnGroups);
		btnGroups.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(TestAAB.this, GroupList.class);
				intent.putExtra("contactId", "-1");
				startActivity(intent);
			
				
			}
		});
		
		pageParams = new PageParams("ASC", "firstName", "2", "0");
		//SearchParams.Builder builder = new SearchParams.Builder();
		//searchParams = new SearchParams(builder.setZipcode("94086"));
		
		BtnContactsList.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(TestAAB.this, ContactsList.class);
				startActivity(i);					
			}
		});
	
		
		getContacts.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ContactsTestCase ctc = new ContactsTestCase(displayContacts, null);
				GroupsTestCase gtc = new GroupsTestCase(displayContacts, null);
				OtherTestCase otc  = new OtherTestCase(displayContacts, null);
				
			    String apiNumber = testApi.getText().toString();
                int iOperation = 1;
                try { iOperation = Integer.valueOf(apiNumber); }
                catch(Exception e) {}
                
				switch (iOperation) {
				
					case 1: //GetContacts
							ctc.testGetContacts("shallow", pageParams, searchParams);
							break;
						
					case 2: //GetContact
							ctc.testGetContact("12CE1C28362013082800371294D457585C50A2B1", "shallow");	
							break;
						
					case 3: //GetContactGroups
							pageParams = new PageParams("ASC", "groupName", "12", "0");
							ctc.testGetContactGroups("12CE1C28362013082800371294D457585C50A2B1", pageParams);
							break;
						
					case 4: //CreateContact
							ctc.testCreateContact("TestFirstFive", "Last");
							break;
						
					case 5: //UpdateContact
							Contact.Builder builder = new Contact.Builder(); 
							builder.setFirstName("FirstView");
							builder.setLastName("LastUsage");
							builder.setContactId("12CE1C28362013082800371294D457585C50A2B1");
							Phone [] phones = new Phone[2];
							phones[0] = new Phone("WORK,CELL", "1234567890", true);
							phones[1] = new Phone("HOME,CELL", "1234567800", true);
							builder.setPhones(phones);
							Contact contact = builder.build();
						
							ctc.testUpdateContact(contact);
							break; 
					
					case 6: //DeleteContact
							ctc.testDeleteContact(ctc.newContactId);
							break;
						
					case 7 : //GetGroups
							pageParams = new PageParams("ASC", "groupName", "2", "0");
							gtc.testGetGroups( pageParams, null);
							break;
							
					case 8:  //CreateGroup 
							Group newGroup = new Group("05058","TestGroup8","USER");
							gtc.testCreateGroup(newGroup);
							break;
						
					case 9: //DeleteGroup
							gtc.testDeleteGroup("39597cb2-a609-4696-96c7-71d09f09907d");
							break;
						
					case 10: //UpdateGroup
							Group group = new Group("a749f6b6-a563-42e9-89c3-0cda468a043e", "TestUpdateGroup", "USER" );
							gtc.testUpdateGroup(group);
							break;
						
					case 11: //AddContactsToGroup
							gtc.testAddContactsToGroup("a749f6b6-a563-42e9-89c3-0cda468a043e" , "39FC506FE22013082800371294D457585C50A2B1");
							break;
					
					case 12: //RemoveContactsFromGroup
							gtc.testRemoveContactsFromGroup("a749f6b6-a563-42e9-89c3-0cda468a043e", "12CE1C28362013082800371294D457585C50A2B1");
							break;
					
					case 13: //GetGroupContacts
							pageParams = new PageParams("ASC", "firstName", "10", "0");
							gtc.testGetGroupContacts("a749f6b6-a563-42e9-89c3-0cda468a043e", pageParams);
							break;
							
					case 14: //GetMyInfo
							otc.testGetMyInfo();
							break;
					
					case 15: //UpdateMyInfo
							//otc.testUpdateMyInfo(contact);
							break;					
				}
			}
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.test_aab, menu);
		return true;
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == OAUTH_CODE) {
			String oAuthCode = null;
			if (resultCode == RESULT_OK) {
				oAuthCode = data.getStringExtra("oAuthCode");
				Log.i("TestAAB", "oAuthCode:" + oAuthCode);
				if (null != oAuthCode) {
					aabManager = new AABManager(Config.fqdn, Config.clientID,Config.secretKey,new getTokenListener());
					aabManager.getOAuthToken(oAuthCode);
				} else {
					Log.i("TestAAB", "oAuthCode: is null");

				}
			} else if(resultCode == RESULT_CANCELED) {
						oAuthCode = data.getStringExtra("oAuthCode");
						Log.i("TestAAB", "oAuthCode:" + oAuthCode);
						if (null != oAuthCode) {
							aabManager = new AABManager(Config.fqdn, Config.clientID,Config.secretKey,new getTokenListener());
							aabManager.getOAuthToken(oAuthCode);
						} else {
							Log.i("TestAAB", "oAuthCode: is null");

				}
								
			}
		} 
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.btnLogin : logIntoAddressBook(Config.fqdn,Config.clientID,Config.secretKey,Config.redirectUri,Config.appScope);
				break;
			case R.id.btnLogout : logOutOfAddressBook();
				break;		
			case R.id.ContactsTabView : 
				Intent i = new Intent(TestAAB.this, ContactsTabView.class);
				//Intent i = new Intent(TestAAB.this, ContactsFragmentView.class);
				startActivity(i);
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

	public class getTokenListener implements ATTIAMListener {

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
		public void onError(InAppMessagingError error) {
			/*authToken = new OAuthToken("abcd", 1, "xyz");
			Config.token = authToken.getAccessToken().toString();*/
			Log.i("getTokenListener",
					"onError Message : " );
		}
	}


}
