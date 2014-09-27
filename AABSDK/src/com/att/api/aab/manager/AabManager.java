package com.att.api.aab.manager;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import android.os.AsyncTask;
import android.util.Log;

import com.att.api.aab.service.AABService;
import com.att.api.aab.service.Contact;
import com.att.api.aab.service.ContactResultSet;
import com.att.api.aab.service.ContactWrapper;
import com.att.api.aab.service.Group;
import com.att.api.aab.service.GroupResultSet;
import com.att.api.aab.service.PageParams;
import com.att.api.error.AttSdkError;
import com.att.api.error.Utils;
import com.att.api.oauth.OAuthService;
import com.att.api.oauth.OAuthToken;
import com.att.api.rest.RESTException;
import com.att.sdk.listener.AttSdkListener;
import com.att.sdk.listener.AttSdkTokenUpdater;

/**
* This class encapsulates the AT&T RESTfull APIs for AddressBook.
* 
* @author sm095n
* @author ps350r

 */
public class AabManager {	
	//private AABService aabService = null; // Just storing the token now.
	private static String aabSdkVersion = "att.aab.android.1.1";
	private AttSdkListener aabListener = null;
	private static AttSdkTokenUpdater tokenListener = null;
	private static OAuthService osrvc = null;
	private static OAuthToken currentToken = null;
	private static String apiFqdn = "https://api.att.com";
	private static long reduceTokenExpiryInSeconds_Debug = 0;
	private static String appendToRefreshToken_Debug = "";
	
	private final static CountDownLatch checkTokenExpirySignal = new CountDownLatch(1);
	private final static Object lockRefreshToken = new Object();
	
	/**
	 * The AabManager method creates an AabManager object.
	 * @param paramNotUsed - This param is not used anymore.
	 * @param token - Overrides the default OAuth token used for authorization.
	 * @param aabListener - Specifies the Listener for callbacks.
	 */	
	public AabManager(String paramNotUsed, OAuthToken token, final AttSdkListener listener) {
		if (token != null) {
			currentToken = token;
		}
	
		assert (currentToken != null); 
		//aabService = new AABService(apiFqdn, currentToken, "att.aab.android.1.1");
		aabListener = listener;
	}
	
	// Note: This constructor is used to obtain the auth code 
	public AabManager(String fqdn, String clientId, String clientSecret, AttSdkListener listener) {
		osrvc = new OAuthService(fqdn, clientId, clientSecret);
		aabListener = listener;
	}
	
	// Note: This constructor is used for RefreshToken 
	public AabManager(AttSdkListener listener) {
		assert(osrvc != null);
		aabListener = listener;
	}
	
	public static void SetCurrentToken(OAuthToken token) {
		currentToken = token;
	}
	
	public static void SetReduceTokenExpiryInSeconds_Debug (long value) {
		reduceTokenExpiryInSeconds_Debug = value;
	}
	
	public static void SetAppendToRefreshToken_Debug (String value) {
		appendToRefreshToken_Debug = value;
	}
	
	public static void SetApiFqdn(String fqdn) {
		apiFqdn = fqdn;
	}
	
	public static void SetTokenUpdatedListener(AttSdkTokenUpdater listener) {
		tokenListener = listener;
	}
	
	public static Boolean isCurrentTokenExpired() {
		// Replace to simple expiry check after testing
		//return currentToken.isAccessTokenExpired();
		return (currentToken.getAccessTokenExpiry() - (System.currentTimeMillis() / 1000) < reduceTokenExpiryInSeconds_Debug);		
	}
	
	public Boolean CheckAndRefreshExpiredToken(final AttSdkListener listener) {
		class getRefreshTokenListener implements AttSdkListener {
			@Override
			public void onSuccess(Object response) {
				OAuthToken authToken = (OAuthToken) response;
				if (null != authToken) {
					currentToken = authToken;
					Log.i("getRefreshTokenListener",
							"onSuccess Message : " + authToken.getAccessToken());
					if (tokenListener != null) {
						tokenListener.onTokenUpdate(authToken);
					}
					checkTokenExpirySignal.countDown();
				}
			}

			@Override
			public void onError(AttSdkError error) {
				Log.i("getRefreshTokenListener", "Error:" + error.getHttpResponse());
				currentToken = null;
				if (tokenListener != null) {
					tokenListener.onTokenDelete();
				}
				checkTokenExpirySignal.countDown();
				listener.onError(error);						
			}
		} 
		try {
			synchronized (lockRefreshToken) {
				if (isCurrentTokenExpired()) {
					AabManager refreshManager = new AabManager(new getRefreshTokenListener());
					// Following line just appends another string to test RefreshToken expiry case for debugging only
					refreshManager.getRefreshToken(currentToken.getRefreshToken() + appendToRefreshToken_Debug);
				} else {
					checkTokenExpirySignal.countDown();					
				}
				checkTokenExpirySignal.await(60, TimeUnit.SECONDS); // Allow 60 seconds for token refresh
			}
		} catch (Exception /*InterruptedException*/ e) {
			currentToken = null;
		}	
		return (currentToken != null);
	}
	
	/**
     * Gets an access token using the specified code.
     *
     * <p>
     * The parameters set during object creation will be used when requesting
     * the access token.
     * </p>
     * <p>
     * The token request is done using the 'authorization_code' grant type.
     * </p>
     *
     * @param code code to use when requesting access token
     * @return OAuthToken object if successful
     *
     */
	public void getOAuthToken(String code){
    	GetTokenUsingCodeTask getTokenUsingCodetask  = new GetTokenUsingCodeTask();
		getTokenUsingCodetask.execute(code);
    }
	
	/**
     * Gets an access token using the specified code.
     *
     * <p>
     * The parameters set during object creation will be used when requesting
     * the access token.
     * </p>
     * <p>
     * The token request is done using the 'authorization_code' grant type.
     * </p>
     *
     * @param code code to use when requesting access token
     * @return OAuthToken object if successful
     *
     */
	public void getRefreshToken(String refreshToken){
		RefreshExpiredTokenTask refreshExpiredTokenTask  = new RefreshExpiredTokenTask();
		refreshExpiredTokenTask.execute(refreshToken);
    }
	
	/**
     * Creates a new Contact which specifies AT&T Mobile Subscriber&#8217;s Contact data model.
     *
     * @param contact a new contact to be created
     * @return result a string if successful
     *
     */
	
	public void CreateContact(Contact contact) {
		if (!CheckAndRefreshExpiredToken(aabListener)) return;
		CreateContactTask createContactTask = new CreateContactTask();
		createContactTask.execute(contact);
	}

	/**
	 * The Get Contact method enables retrieving all the contact information with some order and pagination criteria
	 * 
	 * @param xFields Specifies field names that are expected on the response. 
	 * @param pParams Pagination Parameters
	 * @param sParams Search Parameters
	 * 
	 * @return ContactResultSet Object if successful
	 */
	public void GetContacts(String xFields, PageParams pParams, String sParams) {
		if (!CheckAndRefreshExpiredToken(aabListener)) return;
		GetContactParams contactParams;
		contactParams = new GetContactParams(xFields, pParams, sParams);
		GetContactsTask task = new GetContactsTask();
		task.execute(contactParams);
	}
	
	/**
	 * The Get Contact Id method enables retrieving the contact data structure via a contactId.
	 * 
	 * @param contactId Specifies subscriber contact ID.
	 * @param xFields Specifies field names that are expected on the response. 
	 * 
	 * @return ContactWrapper Object
	 * <ul>
	 * 		<li> AT&T Mobile Subscriber&#8217;s Contact object. Returns when x-fields:no-value or x-fields:attribute names.
	 * 		<li>  Quick Contact data model. Returns when x-fields: shallow. 
	 * </ul>
	 */
	
	public void GetContact(String contactId, String xFields) {
		if (!CheckAndRefreshExpiredToken(aabListener)) return;
		GetContactTask task = new GetContactTask();
		task.execute(contactId, xFields);
	}
	
	/**
	 * The Get Contact Groups enables retrieving the list of groups a contact is belonging to.
	 * 
	 * @param contactId Specifies subscriber contact ID
	 * @param params Pagination Parameters 
	 * 
	 * @return GroupResultSet Object if successful
	 * <li> list of group data models with pagination parameters.
	 */
	public void GetContactGroups(String contactId, PageParams params) {
		if (!CheckAndRefreshExpiredToken(aabListener)) return;
		GetContactGroupsTask getContactGroupsTask = new GetContactGroupsTask();
		getContactGroupsTask.execute(contactId, params.getOrder(), params.getOrderBy(),
				params.getLimit(), params.getOffset());
	}
	
	/**
	 * The Update Contact method updates a contact based on the provided contact data structure.
	 * The contact&#8217;s Id is a mandatory field in the request
	 * 
	 * @param contact  Specifies AT&T Mobile Subscriber&#8217;s Contact data structure.
	 * 
	 * @return result  string if successful
	 */

	public void UpdateContact(Contact contact) {
		if (!CheckAndRefreshExpiredToken(aabListener)) return;
		UpdateContactTask task = new UpdateContactTask();
		task.execute(contact);
	}

	/**
	 * Contact identified by the contactId are moved to the trash.
	 * 
	 * @param contactId Specifies contact id to delete
	 * 
	 * @return result  string if successful
	 */
	public void DeleteContact(String contactId) {
		if (!CheckAndRefreshExpiredToken(aabListener)) return;
		DeleteContactTask task = new DeleteContactTask();
		task.execute(contactId);
	}
	
	/**
	 * The Create Group method enables creating a USER group, if the subscriber&#8217;s address book did not reach its configurable 
	 * maximum number of groups (configurable at the system level, 200 by default). 
	 * 
	 * @param group Specifies ATT&#8217;s subscriber group data model
	 * 
	 * @return result  string if successful
	 */
	public void CreateGroup(Group group) {
		if (!CheckAndRefreshExpiredToken(aabListener)) return;
		CreateGroupTask task = new CreateGroupTask();
		task.execute(group);
	}
	
	/**
	 * The Get Groups method enables retrieving the list of the subscriber&#8217;s groups
	 * 
	 * @param params Pagination Parameters
	 * @param groupName Specifies group name.
	 * 
	 * @return GroupResultSet Object if successful
	 */
	public void GetGroups(PageParams params, String groupName) {
		if (!CheckAndRefreshExpiredToken(aabListener)) return;
		GetGroupsTask task = new GetGroupsTask();
		task.execute(groupName, params.getOrder(), params.getOrderBy(),
				params.getLimit(), params.getOffset());
	}
	
	/**
	 * The Delete Group method enables deleting a list of groups.
	 * 
	 * @param groupId Specifies group id to delete.
	 * 
	 * @return result String if successful
	 */

	public void DeleteGroup(String groupId) {
		if (!CheckAndRefreshExpiredToken(aabListener)) return;
		DeleteGroupTask task = new DeleteGroupTask();
		task.execute(groupId);
	}
	
	/**
	 * The Update Group method enables updating an existing USER group identified by its Group ID.
	 *  group ID is mandatory in a group
	 *  
	 * @param group Specifies the subscriber&#8217;s contact group object
	 * 
	 * If group contains an empty name, this API will return an InvalidGroupException.
	 * If group does not contain a name, it will leave unchanged the name on the server side.
	 * 
	 * @return result String if successful
	 */
	
	public void UpdateGroup(Group group) {
		if (!CheckAndRefreshExpiredToken(aabListener)) return;
		UpdateGroupTask task = new UpdateGroupTask();
		task.execute(group);
	}

	/**
	 * This method associates a list of contacts to a group
	 * 
	 * @param groupId Specifies the group identifier
	 * @param contactIds Specifies  contact id&#8217;s to add. Note: Max 20 group ids are allowed to add.
	 * 
	 * @return result String if successful
	 */
	public void AddContactsToGroup(String groupId, String contactIds) {
		if (!CheckAndRefreshExpiredToken(aabListener)) return;
		AddContactsToGroupTask task = new AddContactsToGroupTask();
		task.execute(groupId, contactIds);
	}

	/**
	 * This method will remove the association between the specified group and list of contacts.
	 * 
	 * @param groupId Specifies the group identifiers
	 * @param contactIds Specifies list of  contact id&#8217;s to delete.
	 * Note: Max 20 contact ids are allowed to delete.
	 */
	public void RemoveContactsFromGroup(String groupId, String contactIds) {
		if (!CheckAndRefreshExpiredToken(aabListener)) return;
		RemoveContactsFromGroupTask task = new RemoveContactsFromGroupTask();
		task.execute(groupId, contactIds);
	}

	/**
	 * The Get Groups Contact method enables retrieving the list of contacts owned by a group. 
	 * It takes a group identifier as an argument and returns a list of contacts. 
	 * 
	 * @param groupId Specifies subscriber group ID.
	 * @param params Pagination Parameters
	 * 
	 * @return result  returns the list of the contactId identifying the contacts present in the group if call is successful
	 */
	public void GetGroupContacts(String groupId, PageParams params) {
		if (!CheckAndRefreshExpiredToken(aabListener)) return;
		GetGroupContactsTask task = new GetGroupContactsTask();
		task.execute(groupId, params.getOrder(), params.getOrderBy(),
				params.getLimit(), params.getOffset());
	}
	
	/**
	 * The "myInfo" URI enables retrieving the subscriber&#8217;s personal contact card. 
	 * This personal contact card is called myInfo or my user profile and follows the contact data model.
	 * 
	 * @return result string if successful
	 * Returns a myInfo data structure containing all the contact details.
	 */
	public void GetMyInfo() {
		if (!CheckAndRefreshExpiredToken(aabListener)) return;
		GetMyInfoTask task = new GetMyInfoTask();
		task.execute();
	}

	/**
	 * The Update MyInfo method updates a subscriber personal profile MyInfo based on the provided contact data structure.
	 * 
	 * @param contact myInfo -- Specifies AT&T Mobile Subscriber&#8217;s Contact data structure.
	 * 
	 * @return result String if successful
	 */
	public void UpdateMyInfo(Contact contact) {
		if (!CheckAndRefreshExpiredToken(aabListener)) return;
		UpdateMyInfoTask task = new UpdateMyInfoTask();
		task.execute(contact);
	}
	
	public class GetTokenUsingCodeTask extends AsyncTask<String, Void, OAuthToken> {

		@Override
		protected OAuthToken doInBackground(String... params) {
			OAuthToken accestoken = null;
			AttSdkError errorObj = new AttSdkError();
			try {
				accestoken = osrvc.getTokenUsingCode(params[0]);
			} catch (RESTException e) {
				errorObj = Utils.CreateErrorObjectFromException( e );
				if (null != aabListener) {
					aabListener.onError(errorObj);
				}
			}		
			return accestoken;
		}

		@Override
		protected void onPostExecute(OAuthToken accestoken) {
			super.onPostExecute(accestoken);
			if(null != accestoken) {
				if (null != aabListener) {
					aabListener.onSuccess(accestoken);
				}
			}
		}
    	
    }
	
	public class RefreshExpiredTokenTask extends AsyncTask<String, Void, OAuthToken> {

		@Override
		protected OAuthToken doInBackground(String... params) {
			OAuthToken accestoken = null;
			AttSdkError errorObj = new AttSdkError();
			try {
				accestoken = osrvc.refreshToken(params[0]);
			} catch (RESTException e) {
				errorObj = Utils.CreateErrorObjectFromException( e );
				if (null != aabListener) {
					aabListener.onError(errorObj);
				}
			}		
			return accestoken;
		}

		@Override
		protected void onPostExecute(OAuthToken accestoken) {
			super.onPostExecute(accestoken);
			if(null != accestoken) {
				if (null != aabListener) {
					aabListener.onSuccess(accestoken);
				}
			}
		}
    	
    }
	
	public class  CreateContactTask extends AsyncTask<Contact, Void, String> {
		@Override
		protected String doInBackground(Contact... params) {
			String result = null;
			AttSdkError errorObj = new AttSdkError();

			try {
				AABService aabService = new AABService(apiFqdn, currentToken, aabSdkVersion);
				result = aabService.createContact(
								params[0] //contact
							    );
			} catch (RESTException e) {
				errorObj = Utils.CreateErrorObjectFromException( e );
				if (null != aabListener) {
					aabListener.onError(errorObj);
				}
			}
			
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if( null != result ) {
				if (null != aabListener) {
					aabListener.onSuccess(result);
				}
			}			
		}		
	}
	
	public class  GetContactsTask extends AsyncTask<GetContactParams, Void, ContactResultSet> {
		@Override
		protected ContactResultSet doInBackground(GetContactParams... params) {
			ContactResultSet contactResultSet = null;
			AttSdkError errorObj = new AttSdkError();

			try {
				AABService aabService = new AABService(apiFqdn, currentToken, aabSdkVersion);
				contactResultSet = aabService.getContacts(
								params[0].getxFields(), //xFields
							    params[0].getPageParams(), //PageParams
							    params[0].getSearchParams() //SearchParams 
							    );
			} catch (RESTException e) {
				errorObj = Utils.CreateErrorObjectFromException( e );
				if (null != aabListener) {
					aabListener.onError(errorObj);
				}
			}
			
			return contactResultSet;
		}

		@Override
		protected void onPostExecute(ContactResultSet contactResultSet) {
			super.onPostExecute(contactResultSet);
			if( null != contactResultSet ) {
				if (null != aabListener) {
					aabListener.onSuccess(contactResultSet);
				}
			}			
		}		
	}
	
	public class  GetContactTask extends AsyncTask<String, Void, ContactWrapper> {
		@Override
		protected ContactWrapper doInBackground(String... params) {
			ContactWrapper result = null;
			AttSdkError errorObj = new AttSdkError();

			try {
				AABService aabService = new AABService(apiFqdn, currentToken, aabSdkVersion);
				result = aabService.getContact(
								params[0], //contactId
							    params[1] //xFields 
							    );
			} catch (RESTException e) {
				errorObj = Utils.CreateErrorObjectFromException( e );
				if (null != aabListener) {
					aabListener.onError(errorObj);
				}
			}
			
			return result;
		}

		@Override
		protected void onPostExecute(ContactWrapper result) {
			super.onPostExecute(result);
			if( null != result ) {
				if (null != aabListener) {
					aabListener.onSuccess(result);
				}
			}			
		}		
	}
	
	public class  GetContactGroupsTask extends AsyncTask<String, Void, GroupResultSet> {
		@Override
		protected GroupResultSet doInBackground(String... params) {
			GroupResultSet result = null;
			AttSdkError errorObj = new AttSdkError();

			try {
				PageParams pageParams = new PageParams(params[1], params[2], params[3], params[4]);
				AABService aabService = new AABService(apiFqdn, currentToken, aabSdkVersion);
				result = aabService.getContactGroups(
								params[0], //contactId
								pageParams //pageParams 
							    );
			} catch (RESTException e) {
				errorObj = Utils.CreateErrorObjectFromException( e );
				if (null != aabListener) {
					aabListener.onError(errorObj);
				}
			}
			
			return result;
		}
		
		@Override
		protected void onPostExecute(GroupResultSet result) {
			super.onPostExecute(result);
			if( null != result ) {
				if (null != aabListener) {
					aabListener.onSuccess(result);
				}
			}			
		}		
	}
	
	public class  UpdateContactTask extends AsyncTask<Contact, Void, String> {
		@Override
		protected String doInBackground(Contact... params) {
			AttSdkError errorObj = new AttSdkError();
			String result = "success";

			try {
				AABService aabService = new AABService(apiFqdn, currentToken, aabSdkVersion);
				aabService.updateContact(
								params[0], //contact
								params[0].getContactId() //contactId
							    );
			} catch (RESTException e) {
				errorObj = Utils.CreateErrorObjectFromException( e );
				if (null != aabListener) {
					aabListener.onError(errorObj);
				}
			}
			
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if( null != result ) {
				if (null != aabListener) {
					aabListener.onSuccess(result);
				}
			}			
		}		
	}
	
	public class  DeleteContactTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... params) {
			AttSdkError errorObj = new AttSdkError();
			String result = "success";

			try {
				AABService aabService = new AABService(apiFqdn, currentToken, aabSdkVersion);
				aabService.deleteContact(
								params[0] //contactId
							    );
			} catch (RESTException e) {
				errorObj = Utils.CreateErrorObjectFromException( e );
				if (null != aabListener) {
					aabListener.onError(errorObj);
				}
			}
			
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if( null != result ) {
				if (null != aabListener) {
					aabListener.onSuccess(result);
				}
			}			
		}		
	}
	
	public class  CreateGroupTask extends AsyncTask<Group, Void, String> {
		@Override
		protected String doInBackground(Group... params) {
			String result = null;
			AttSdkError errorObj = new AttSdkError();
	
			try {
				AABService aabService = new AABService(apiFqdn, currentToken, aabSdkVersion);
				result = aabService.createGroup(
								params[0] //group
							    );
			} catch (RESTException e) {
				errorObj = Utils.CreateErrorObjectFromException( e );
				if (null != aabListener) {
					aabListener.onError(errorObj);
				}
			}
			
			return result;
		}
	
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if( null != result ) {
				if (null != aabListener) {
					aabListener.onSuccess(result);
				}
			}			
		}		
	}
	
	public class  GetGroupsTask extends AsyncTask<String, Void, GroupResultSet> {
		@Override
		protected GroupResultSet doInBackground(String... params) {
			GroupResultSet result = null;
			AttSdkError errorObj = new AttSdkError();

			try {
				PageParams pageParams = new PageParams(params[1], params[2], params[3], params[4]);
				AABService aabService = new AABService(apiFqdn, currentToken, aabSdkVersion);
				result = aabService.getGroups(
								pageParams, //pageParams 
								params[0] //groupName
							    );
			} catch (RESTException e) {
				errorObj = Utils.CreateErrorObjectFromException( e );
				if (null != aabListener) {
					aabListener.onError(errorObj);
				}
			}
			
			return result;
		}
		
		@Override
		protected void onPostExecute(GroupResultSet result) {
			super.onPostExecute(result);
			if( null != result ) {
				if (null != aabListener) {
					aabListener.onSuccess(result);
				}
			}			
		}		
	}
	
	public class  DeleteGroupTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... params) {
			AttSdkError errorObj = new AttSdkError();
			String result = "success";

			try {
				AABService aabService = new AABService(apiFqdn, currentToken, aabSdkVersion);
				aabService.deleteGroup(
								params[0] //groupId
							    );
			} catch (RESTException e) {
				errorObj = Utils.CreateErrorObjectFromException( e );
				if (null != aabListener) {
					aabListener.onError(errorObj);
				}
			}
			
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if( null != result ) {
				if (null != aabListener) {
					aabListener.onSuccess(result);
				}
			}			
		}		
	}
	
	public class  UpdateGroupTask extends AsyncTask<Group, Void, String> {
		@Override
		protected String doInBackground(Group... params) {
			String result = "success";
			AttSdkError errorObj = new AttSdkError();
	
			try {
				AABService aabService = new AABService(apiFqdn, currentToken, aabSdkVersion);
				aabService.updateGroup(
								params[0], //group
								params[0].getGroupId() //groupId
							    );
			} catch (RESTException e) {
				errorObj = Utils.CreateErrorObjectFromException( e );
				if (null != aabListener) {
					aabListener.onError(errorObj);
				}
			}
			
			return result;
		}
	
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if( null != result ) {
				if (null != aabListener) {
					aabListener.onSuccess(result);
				}
			}			
		}		
	}
	
	public class  AddContactsToGroupTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... params) {
			AttSdkError errorObj = new AttSdkError();
			String result = "success";

			try {
				AABService aabService = new AABService(apiFqdn, currentToken, aabSdkVersion);
				aabService.addContactsToGroup(
								params[0], //groupId
								params[1]  //contactIds
							    );
			} catch (RESTException e) {
				errorObj = Utils.CreateErrorObjectFromException( e );
				if (null != aabListener) {
					aabListener.onError(errorObj);
				}
			}
			
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if( null != result ) {
				if (null != aabListener) {
					aabListener.onSuccess(result);
				}
			}			
		}		
	}
	
	public class  RemoveContactsFromGroupTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... params) {
			AttSdkError errorObj = new AttSdkError();
			String result = "success";

			try {
				AABService aabService = new AABService(apiFqdn, currentToken, aabSdkVersion);
				aabService.removeContactsFromGroup(
								params[0], //groupId
								params[1]  //contactIds
							    );
			} catch (RESTException e) {
				errorObj = Utils.CreateErrorObjectFromException( e );
				if (null != aabListener) {
					aabListener.onError(errorObj);
				}
			}
			
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if( null != result ) {
				if (null != aabListener) {
					aabListener.onSuccess(result);
				}
			}			
		}		
	}
	
	public class  GetGroupContactsTask extends AsyncTask<String, Void, String[]> {
		@Override
		protected String[] doInBackground(String... params) {
			String[] result = null;
			AttSdkError errorObj = new AttSdkError();

			try {
				PageParams pageParams = new PageParams(params[1], params[2], params[3], params[4]);
				AABService aabService = new AABService(apiFqdn, currentToken, aabSdkVersion);
				result = aabService.getGroupContacts(
								params[0], //groupId
								pageParams //pageParams 
							    );
			} catch (RESTException e) {
				errorObj = Utils.CreateErrorObjectFromException( e );
				if (null != aabListener) {
					aabListener.onError(errorObj);
				}
			}
			
			return result;
		}
		
		@Override
		protected void onPostExecute(String[] result) {
			super.onPostExecute(result);
			if( null != result ) {
				if (null != aabListener) {
					aabListener.onSuccess(result);
				}
			}			
		}		
	}
	
	public class  GetMyInfoTask extends AsyncTask<Void, Void, Contact> {
		@Override
		protected Contact doInBackground(Void... params) {
			Contact result = null;
			AttSdkError errorObj = new AttSdkError();

			try {
				AABService aabService = new AABService(apiFqdn, currentToken, aabSdkVersion);
				result = aabService.getMyInfo();
			} catch (RESTException e) {
				errorObj = Utils.CreateErrorObjectFromException( e );
				if (null != aabListener) {
					aabListener.onError(errorObj);
				}
			}
			
			return result;
		}

		@Override
		protected void onPostExecute(Contact result) {
			super.onPostExecute(result);
			if( null != result ) {
				if (null != aabListener) {
					aabListener.onSuccess(result);
				}
			}			
		}		
	}
	
	public class  UpdateMyInfoTask extends AsyncTask<Contact, Void, String> {
		@Override
		protected String doInBackground(Contact... params) {
			AttSdkError errorObj = new AttSdkError();
			String result = "success";

			try {
				AABService aabService = new AABService(apiFqdn, currentToken, aabSdkVersion);
				aabService.updateMyInfo(
								params[0] //contact
							    );
			} catch (RESTException e) {
				errorObj = Utils.CreateErrorObjectFromException( e );
				if (null != aabListener) {
					aabListener.onError(errorObj);
				}
			}
			
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if( null != result ) {
				if (null != aabListener) {
					aabListener.onSuccess(result);
				}
			}			
		}		
	}
}
