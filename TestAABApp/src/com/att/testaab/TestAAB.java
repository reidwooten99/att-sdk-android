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
import com.att.api.aab.service.ContactResultSet;
import com.att.api.aab.service.ContactWrapper;
import com.att.api.aab.service.Group;
import com.att.api.aab.service.GroupResultSet;
import com.att.api.aab.service.PageParams;
import com.att.api.aab.service.Phone;
import com.att.api.aab.service.QuickContact;
import com.att.api.aab.service.SearchParams;
import com.att.api.error.InAppMessagingError;
import com.att.api.oauth.OAuthToken;

public class TestAAB extends Activity implements OnClickListener {

	private AABManager aabManager;
	private PageParams pageParams;
	private SearchParams searchParams;
	private Button getContacts;
	private TextView displayContacts;
	private ContactWrapper contactWrapper;	
	private Button BtnContactsList;
	private EditText testApi;
	private String strText = "";
	private Button btnGroups;
	private final int OAUTH_CODE = 1;
	private Button btnLogIn;
	private Button btnLogOut;
	private Button btnTabView;
	private String contactSubscriberId;
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
				// TODO Auto-generated method stub
				Intent intent = new Intent(TestAAB.this, GroupList.class);
				intent.putExtra("contactId", "-1");
				startActivity(intent);
			
				
			}
		});
		
		pageParams = new PageParams("ASC", "firstName", "2", "0");
		SearchParams.Builder builder = new SearchParams.Builder();
		//searchParams = new SearchParams(builder.setZipcode("94086"));
		
		BtnContactsList.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(TestAAB.this, ContactsList.class);
				startActivity(i);					
			}
		});
	
		
		getContacts.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			    String apiNumber = testApi.getText().toString();
                int iOperation = 1;
                try { iOperation = Integer.valueOf(apiNumber); }
                catch(Exception e) {}
                
               // OAuthToken authToken = new OAuthToken(Config.token, Config.accessTokenExpiry, Config.refreshToken);
				switch (iOperation) {
				
					case 1: //GetContacts
						aabManager = new AABManager(Config.fqdn, 
													authToken,
													new getContactsListener());
						aabManager.GetContacts("shallow", pageParams, searchParams);
						break;
						
					case 2: //GetContact
						aabManager = new AABManager(Config.fqdn, 
													authToken,
													new getContactListener());
						//aabManager.GetContact("09876544321", "shallow");
						aabManager.GetContact("0987654432123", "shallow");	
						break;
						
					case 3: //GetContactGroups
						aabManager = new AABManager(Config.fqdn, 
													authToken,
													new getContactGroupsListener());
						//aabManager.GetContact("09876544321", "shallow");
						pageParams = new PageParams("ASC", "firstName", "2", "0");
						aabManager.GetContactGroups("0987654432123", pageParams);	
						break;
						
					case 4: //CreateContact
						aabManager = new AABManager(Config.fqdn, 
													authToken,
													new createContactListener());
						Contact.Builder builder = new Contact.Builder(); 
						builder.setFirstName("First");
						builder.setLastName("Last");
						builder.setFormattedName("First Last");
						long time= System.currentTimeMillis();
						builder.setContactId(String.valueOf(time));
						Phone [] phones = new Phone[1];
						phones[0] = new Phone("Work", "1234567890", true);
						builder.setPhones(phones);
						Contact contact = builder.build();
						aabManager.CreateContact(contact);
						break;
						
					case 5: //UpdateContact //getcontacts n then update
						aabManager = new AABManager(Config.fqdn,
													authToken,
													new getContactsforUpdateListener());
						aabManager.GetContacts("shallow", pageParams, searchParams);
						/*Contact.Builder builderForUpdate = new Contact.Builder(); 
						builderForUpdate.setFirstName("Last");
						builderForUpdate.setLastName("First");
						builderForUpdate.setFormattedName("LastFirst");
						
						Contact contactForupdate = builderForUpdate.build();
						aabManager.UpdateContact(contactForupdate);		*/			
						break; 
					
					case 6: //DeleteContact
						aabManager = new AABManager(Config.fqdn,
													authToken,
													new deleteContactListener());
						aabManager.DeleteContact(contactSubscriberId);
						break;
						
					case 7 : //GetGroups
						aabManager = new AABManager(Config.fqdn,
													authToken,
													new getGroupsListener());
						aabManager.GetGroups(pageParams, null);
						break;
						
					case 8:  //CreateGroup 
						aabManager = new AABManager(Config.fqdn,
													authToken,
													new createGroupListener());
						Group newGroup = new Group("0505","TestGroup","USER");
						aabManager.CreateGroup(newGroup);
						break;
						
					case 9: //DeleteGroup
						aabManager = new AABManager(Config.fqdn,
													authToken,
													new deleteGroupListener());
						aabManager.DeleteGroup(groupSubscriberId);		
						break;
						
					case 10: //UpdateGroup
						aabManager = new AABManager(Config.fqdn,
													authToken,
													new getGroupsForUpdateListener());
						//aabManager.UpdateGroup(group);
						break;
						
					case 11: //AddContactsToGroup
							break;
					
					case 12: //RemoveContactsFromGroup
							break;
					
					case 13: //GetGroupContacts
							break;
							
					case 14: //GetMyInfo
							break;
					
					case 15: //UpdateMyInfo
							break;
					
						
				}
			}
		});
		
	}
	
	private class getContactsListener implements ATTIAMListener {

		@Override
		public void onSuccess(Object response) {
			ContactResultSet contactResultSet = (ContactResultSet) response;
			
			if (null != contactResultSet) {
				strText = (String) displayContacts.getText();
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
				strText = (String) displayContacts.getText();
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

	private class getContactGroupsListener implements ATTIAMListener {
		public GroupResultSet groupResultSet;

		@Override
		public void onSuccess(Object response) {
			groupResultSet = (GroupResultSet) response;
			if (null != groupResultSet) {
				strText = (String) displayContacts.getText();
				//QuickContact qc = contactWrapper.getQuickContact();
				Group[] groups_arr = groupResultSet.getGroups();
				for (int i=0; i < groups_arr.length; i++) {
					Group grp = groups_arr[i];
					strText += "\n" + grp.getGroupId() + ", " + grp.getGroupName() + ", "  + grp.getGroupType();
					Log.i("getContactGroupsAPI","OnSuccess : ContactID :  " + strText);
					//Log.i("getContactsAPI", "OnSuccess : ContactID :  " +contactResultSet.getQuickContacts()[1].getContactId().toString());
					displayContacts.setText(strText);
				}
				return;
			}
		}

		@Override
		public void onError(InAppMessagingError error) {
			Log.i("getContactGroups on error", "onError");

		}
	}

	private class createContactListener implements ATTIAMListener {
		@Override
		public void onSuccess(Object response) {
			String location = (String) response;
			if (null != location) {
				strText = (String) displayContacts.getText();
				Log.i("createContactAPI","Contact created : Location :  " + location);
				strText += "\n" + location;
				
				String[] locationUrl = location.split("contacts/");
				contactSubscriberId = locationUrl[1];
				Log.i("createContactAPI","Contact created : contactId :  " + contactSubscriberId);
				
				displayContacts.setText(strText);
				return;
			}
		}

		@Override
		public void onError(InAppMessagingError error) {
			Log.i("createContactAPI on error", "onError");

		}
	}
	
	
	private class getContactsforUpdateListener implements ATTIAMListener {

		@Override
		public void onSuccess(Object response) {
			ContactResultSet contactResultSet = (ContactResultSet) response;
			
			if (null != contactResultSet) {
				strText = (String) displayContacts.getText();
				QuickContact[] quickContactsArray = contactResultSet.getQuickContacts();
				Contact[] contactsArray = contactResultSet.getContacts();
				
				Contact.Builder builderForUpdate = new Contact.Builder(); 
				builderForUpdate.setContactId(contactsArray[0].getContactId());
				builderForUpdate.setFirstName("Last");
				builderForUpdate.setLastName("First");
				builderForUpdate.setFormattedName("LastFirst");
				Contact contactForUpdate  = builderForUpdate.build();
				
				aabManager = new AABManager(Config.fqdn,
											authToken,
											new updateContactListener());
				aabManager.UpdateContact(contactForUpdate);
				
				Log.i("updateContactAPI","OnSuccess : ContactID :  " + strText);
				//Log.i("getContactsAPI", "OnSuccess : ContactID :  " +contactResultSet.getQuickContacts()[1].getContactId().toString());
				displayContacts.setText(strText);
				return;
			}
			
		}

		@Override
		public void onError(InAppMessagingError error) {
			Log.i("updateContactAPI on error", "onError");

		}
	}
	
	private class updateContactListener implements ATTIAMListener {

		@Override
		public void onSuccess(Object response) {
			String result = (String) response;
			strText = (String) displayContacts.getText();
			strText += "\n" +"updateContactAPI : " + "\n" + result;
			Log.i("updateContactAPI","OnSuccess : RESULT :  " + result);
			displayContacts.setText(strText);
			return;
			
		}

		@Override
		public void onError(InAppMessagingError error) {
			Log.i("updateContactAPI on error", "onError");

		}
	}
	
	private class deleteContactListener implements ATTIAMListener {

		@Override
		public void onSuccess(Object response) {
			String result = (String) response;
			strText = (String) displayContacts.getText();
			strText += "\n" +"deleteContactAPI : " + "\n" + result;
			Log.i("deleteContactAPI","OnSuccess : RESULT :  " + result);
			displayContacts.setText(strText);
			return;
			
		}

		@Override
		public void onError(InAppMessagingError error) {
			Log.i("deleteContactAPI on error", "onError");

		}
	}
	
	private class getGroupsListener implements ATTIAMListener {

		@Override
		public void onSuccess(Object response) {
			GroupResultSet groupResultSet = (GroupResultSet) response;
			
			if (null != groupResultSet) {
				strText = (String) displayContacts.getText();
				Group[] groups_arr = groupResultSet.getGroups();
				for (int i=0; i < groups_arr.length; i++) {
					Group grp = groups_arr[i];
					strText += "\n" + grp.getGroupId() + ", " + 
							grp.getGroupName() + ", " + grp.getGroupType();
					
				}
			Log.i("getGroupsAPI","OnSuccess : RESULT :  " + strText);
			displayContacts.setText(strText);
			
			return;
			}	
		}

		@Override
		public void onError(InAppMessagingError error) {
			Log.i("getGroupsAPI on error", "onError");

		}
	}
	
	private class createGroupListener implements ATTIAMListener {

		@Override
		public void onSuccess(Object response) {
			String location = (String) response;
			if (null != location) {
				strText = (String) displayContacts.getText();
				Log.i("createGroupAPI","Group created : Location :  " + location);
				strText += "\n" + location;
				
				String[] locationUrl = location.split("groups/");
				groupSubscriberId = locationUrl[1];
				Log.i("createGroupAPI","Group created : groupId :  " + groupSubscriberId);
				
				displayContacts.setText(strText);
				return;
			}	
		}

		@Override
		public void onError(InAppMessagingError error) {
			Log.i("createGroupAPI on error", "onError");

		}
	}
	
	private class deleteGroupListener implements ATTIAMListener {

		@Override
		public void onSuccess(Object response) {
			String result = (String) response;
			strText = (String) displayContacts.getText();
			strText += "\n" +"deleteGroupAPI : " + "\n" + result;
			Log.i("deleteGroupAPI","OnSuccess : RESULT :  " + result);
			displayContacts.setText(strText);
			return;
			
		}

		@Override
		public void onError(InAppMessagingError error) {
			Log.i("deleteGroupAPI on error", "onError");

		}
	}
	
	private class updateGroupListener implements ATTIAMListener {

		@Override
		public void onSuccess(Object response) {
			GroupResultSet groupResultSet = (GroupResultSet) response;
			
			if (null != groupResultSet) {
				strText = (String) displayContacts.getText();
				Group[] groups_arr = groupResultSet.getGroups();
				for (int i=0; i < groups_arr.length; i++) {
					Group grp = groups_arr[i];
					strText += "\n" + grp.getGroupId() + ", " + 
							grp.getGroupName() + ", " + grp.getGroupType();
					
				}
			Log.i("updateGroupAPI","OnSuccess : RESULT :  " + strText);
			displayContacts.setText(strText);
			
			return;
			}	
		}

		@Override
		public void onError(InAppMessagingError error) {
			Log.i("updateGroupAPI on error", "onError");

		}
	}
	
	private class getGroupsForUpdateListener implements ATTIAMListener {

		@Override
		public void onSuccess(Object response) {
			GroupResultSet groupResultSet = (GroupResultSet) response;
			
			if (null != groupResultSet) {
				strText = (String) displayContacts.getText();
				Group[] groups_arr = groupResultSet.getGroups();
				for (int i=0; i < groups_arr.length; i++) {
					Group grp = groups_arr[i];
					strText += "\n" + grp.getGroupId() + ", " + 
							grp.getGroupName() + ", " + grp.getGroupType();
					
				}
			Log.i("getGroupsForUpdateAPI","OnSuccess : RESULT :  " + strText);
			String grpName = groups_arr[0].getGroupName();
			aabManager.UpdateGroup(groups_arr[0]);
			displayContacts.setText(strText);
			
			return;
			}
		}

		@Override
		public void onError(InAppMessagingError error) {
			Log.i("getGroupsForUpdateAPI on error", "onError");

		}
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
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub	
		CookieSyncManager.createInstance(this);
		CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.removeAllCookie();		
	}

	public void logIntoAddressBook(String fqdn, String clientId, String secretKey, String refirectUri, String appScope) {
		// TODO Auto-generated method stub
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
