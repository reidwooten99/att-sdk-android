package com.att.iamsampleapp;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import com.att.api.error.InAppMessagingError;
import com.att.api.immn.listener.ATTIAMListener;
import com.att.api.immn.service.ChangeType;
import com.att.api.immn.service.DeltaChange;
import com.att.api.immn.service.DeltaResponse;
import com.att.api.immn.service.IAMManager;
import com.att.api.immn.service.Message;
import com.att.api.immn.service.MessageIndexInfo;
import com.att.api.immn.service.MessageList;
import com.att.api.immn.service.MmsContent;
import com.att.api.oauth.OAuthService;
import com.att.api.oauth.OAuthToken;
import com.att.api.util.Preferences;
import com.att.api.util.Sdk_Config;

public class ConversationList extends Activity {

	private static final String TAG = "Conversation List";

	private ListView messageListView;
	private MessageListAdapter adapter;
	private IAMManager iamManager;
	private OAuthService osrvc;
	private final int NEW_MESSAGE = 2;
	private final int OAUTH_CODE = 1;
	private OAuthToken authToken;
	private MessageIndexInfo msgIndexInfo;
	private DeltaResponse delta;
	private MessageList msgList;
	private ArrayList<Message> messageList;
	private String prevMailboxState;
	private String deleteMessageID;
	private int prevIndex;
	private ProgressDialog pDialog;
	Preferences pref = null;
	private String oAuthCode = null;
	String byPassANDsuppress = "";
	Context m_context;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_conversation_list);
		
		m_context = getApplicationContext();
		showProgressDialog("Loading Messages .. ");
		
		messageListView = (ListView) findViewById(R.id.messageListViewItem);
		pref = new Preferences(m_context);
	
		String presetedStr = pref.getString(Sdk_Config.preset, Sdk_Config.none);
		String tokenStr = pref.getString("Token", Sdk_Config.none);
		
		if (!tokenStr.contains(Sdk_Config.none))
		{
			boolean suppressed = presetedStr.contains(Sdk_Config.suppressLndgPageStr);
			boolean bypass  =    presetedStr.contains(Sdk_Config.byPassOnNetStr);

			// Create service for requesting an OAuth token
			osrvc = new OAuthService(Sdk_Config.fqdn, Config.clientID, Config.secretKey);
			
			if (suppressed || bypass){
				Intent i = new Intent(this,
						com.att.api.consentactivity.UserConsentActivity.class);
				i.putExtra("fqdn", Sdk_Config.fqdn);
				i.putExtra("fqdn_extend", Sdk_Config.fqdn_extend);
				i.putExtra("clientId", Config.clientID);
				i.putExtra("clientSecret", Config.secretKey);
				i.putExtra("redirectUri", Config.redirectUri);
				i.putExtra("appScope", Config.appScope);
				
				byPassANDsuppress = "";
					
				if (bypass && (!suppressed)){
							   byPassANDsuppress = Sdk_Config.byPassOnNetwork; // off_net  
							   i.putExtra("byPassAndsuppress", byPassANDsuppress);
							   startActivityForResult(i, OAUTH_CODE);
							   setupMessageListListener();
							   Log.i(" --- mainActivity ---", "BY-PASS");
				}
				else  
				  if ((!bypass) && suppressed){
							byPassANDsuppress = Sdk_Config.suppressLandingPage; // suppress landing page 
							 i.putExtra("byPassAndsuppress", byPassANDsuppress);
							 startActivityForResult(i, OAUTH_CODE);
							 setupMessageListListener();
						     Log.i(" --- mainActivity ---", "SUPPRESS");		   
				}
				else
				if (bypass && suppressed){
						           // off_net and suppress landing page
								   byPassANDsuppress = Sdk_Config.byPassOnNetANDsuppressLandingPage;
								   Log.i(" --- mainActivity ---", "OFF_NET and SUPPRESS");
								   i.putExtra("byPassAndsuppress", byPassANDsuppress);
								   startActivityForResult(i, OAUTH_CODE);
								   setupMessageListListener();
				}
					
			}
			else {
				   Log.i(" --- mainActivity ---", " NO OFF_NET NOR SUPPRESS");
	
				   Long tokenExpiredTime = pref.getLong("AccessTokenExpiry", Sdk_Config.tokenExpiredTime);
				   String refreshToken = pref.getString("RefreshToken", Sdk_Config.none );			  
				   authToken = new OAuthToken(tokenStr, tokenExpiredTime - OAuthToken.xtimestamp(), refreshToken);
				   getMessageIndexInfo();
			}
		}
		else {
			
			Log.i(" --- mainActivity ---", " FIRST TIME");
			pref.setString("FQDN", Sdk_Config.fqdn);
			pref.setString("clientID", Config.clientID);
			pref.setString("secretKey", Config.secretKey);
			
			osrvc = new OAuthService(Sdk_Config.fqdn, Config.clientID, Config.secretKey);
			Intent i = new Intent(this,
					com.att.api.consentactivity.UserConsentActivity.class);
			i.putExtra("fqdn", Sdk_Config.fqdn);
			i.putExtra("fqdn_extend", Sdk_Config.fqdn_extend);
			i.putExtra("clientId", Config.clientID);
			i.putExtra("clientSecret", Config.secretKey);
			i.putExtra("redirectUri", Config.redirectUri);
			i.putExtra("appScope", Config.appScope);
			
			byPassANDsuppress = "";
			i.putExtra("byPassAndsuppress", byPassANDsuppress);
			startActivityForResult(i, OAUTH_CODE);
		    setupMessageListListener();
		}
		
	}  
		

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == NEW_MESSAGE) {
			if (resultCode == RESULT_OK) {
				Utils.toastHere(getApplicationContext(), TAG, "Message Sent : "
						+ data.getStringExtra("MessageResponse"));				
			}
		} 
		else 
		  if (requestCode == OAUTH_CODE) {
			oAuthCode = null;
			if (resultCode == RESULT_OK) {
				oAuthCode = data.getStringExtra("oAuthCode");
				Log.i("mainActivity", "oAuthCode:" + oAuthCode);
				if (null != oAuthCode) {
					Sdk_Config.oAuthCode = oAuthCode;
					pref.setString(Sdk_Config.oAuthCodeStr, Sdk_Config.oAuthCode );
					
					/*
					 * STEP 1: Getting the oAuthToken
					 * 
					 * Get the OAuthToken using the oAuthCode,obtained from the
					 * Authentication page The Success/failure will be handled
					 * by the listener : getTokenListener()
					 */

					 osrvc.getOAuthToken(oAuthCode, new getTokenListener());
				}
				else {
						Log.i("mainActivity", "oAuthCode: is null");
						pref.setString(Sdk_Config.preset, Sdk_Config.none);
						Intent i = new Intent(this,
								com.att.api.consentactivity.UserConsentActivity.class);
						i.putExtra("fqdn", Sdk_Config.fqdn);
						i.putExtra("fqdn_extend", Sdk_Config.fqdn_extend);
						i.putExtra("clientId", Config.clientID);
						i.putExtra("clientSecret", Config.secretKey);
						i.putExtra("redirectUri", Config.redirectUri);
						i.putExtra("appScope", Config.appScope);
						
						byPassANDsuppress = "";
						i.putExtra("byPassAndsuppress", byPassANDsuppress);
	
						startActivityForResult(i, OAUTH_CODE);
						setupMessageListListener();
				}
			} 
			else 
			  if(resultCode == RESULT_CANCELED) {
				 String errorMessage = null;
				 if(null != data) {
					 errorMessage = data.getStringExtra("ErrorMessage");
				 } else 
					errorMessage = getResources().getString(R.string.title_close_application);
				 new AlertDialog.Builder(ConversationList.this)
				.setTitle("Error")
				.setMessage(errorMessage)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
						finish();
					}
				}).show();				
			}
		} 
	}
	
	/*
	 * getTokenListener will be called on getting the response from
	 * osrvc.getOAuthToken(..)
	 * 
	 * onSuccess : This is called when the oAuthToken is available. The
	 * AccessToken is extracted from oAuthToken and stored in Config.token.
	 * authToken will then be used to get access to any of the twelve methods
	 * supported by InApp Messaging.
	 * 
	 * onError: This is called when the oAuthToken is not generated/incorrect
	 * APP_KEY/ APP_SECRET /APP_SCOPE / REDIRECT_URI The Error along with the
	 * error code is displayed to the user
	 */
	private class getTokenListener implements ATTIAMListener {

		@Override
		public void onSuccess(Object response) {
			authToken = (OAuthToken) response;
			
			if (null != authToken) {
				pref.setString("Token", authToken.getAccessToken());
				pref.setString("RefreshToken", authToken.getRefreshToken() );
				pref.setLong("AccessTokenExpiry", authToken.getAccessTokenExpiry());
				
				/*
				 * STEP 2: Getting the MessageIndexInfo
				 * 
				 * Message count, state and status of the index cache is
				 * obtained by calling getMessageIndexInfo The response will be
				 * handled by the listener : getMessageIndexInfoListener()
				 * 
				 */
				getMessageIndexInfo();	
			
			}
		}

		@Override
		public void onError(InAppMessagingError error) {
			dismissProgressDialog();
			Utils.toastOnError(getApplicationContext(), error);
			Log.i("getTokenListener",
						"onError Message  222 : " + error.getHttpResponseCode());
			
		}
	}

	/*
	 * This operation allows the developer to get the state, status and message
	 * count of the index cache for the subscriber¿½s inbox. authToken will be
	 * used to get access to GetMessageIndexInfo of InApp Messaging.
	 * 
	 * The response will be handled by the listener :
	 * getMessageIndexInfoListener()
	 */
	public void getMessageIndexInfo() {

		iamManager = new IAMManager(Sdk_Config.fqdn, authToken, m_context,
				new getMessageIndexInfoListener());
		iamManager.GetMessageIndexInfo();

	}

	/*
	 * getMessageIndexInfoListener will be called on getting the response from
	 * GetMessageIndexInfo()
	 * 
	 * onSuccess : This is called when the response : status,state and message
	 * count of the inbox is avaialble
	 * 
	 * onError: This is called when the msgIndexInfo returns null An index cache
	 * has to be created for the inbox by calling createMessageIndex The Error
	 * along with the error code is displayed to the user
	 */

	private class getMessageIndexInfoListener implements ATTIAMListener {

		@Override
		public void onSuccess(Object response) {
			msgIndexInfo = (MessageIndexInfo) response;
			if (null != msgIndexInfo) {
				getMessageList();
				return;
			}
		}

		@Override
		public void onError(InAppMessagingError error) {

			 Utils.toastOnError(getApplicationContext(), error);
		
			 if ( error.getHttpResponseCode() == 403){
				 finish();
			 }
			 
			if ( error.getHttpResponseCode() == 401){
				GetNewTokenViaRefreshToken mGet = new GetNewTokenViaRefreshToken();
				try {
					
					authToken = mGet.execute(Sdk_Config.fqdn, Config.clientID, Config.secretKey, pref.getString("RefreshToken", Sdk_Config.none)).get();
				} 
				catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	/*
	 * This operation allows the developer to create an index cache for the
	 * subscriberï¿½s inbox. authToken will be used to get access to
	 * CreateMessageIndex of InApp Messaging.
	 * 
	 * The response will be handled by the listener :
	 * createMessageIndexListener()
	 */

	public void createMessageIndex() {
		iamManager = new IAMManager(Sdk_Config.fqdn, authToken, m_context,
				new createMessageIndexListener());
		iamManager.CreateMessageIndex();
	}

	/*
	 * createMessageIndexListener will be called on getting the response from
	 * createMessageIndex()
	 * 
	 * onSuccess : This is called when the Index is created for the subscriber&#8217;s
	 * inbox. A list of messages will be fetched from the GetMessageList of
	 * InApp messaging.
	 * 
	 * onError: This is called when the index info is not created successfully
	 * The Error along with the error code is displayed to the user
	 */

	private class createMessageIndexListener implements ATTIAMListener {

		@Override
		public void onSuccess(Object response) {
			Boolean msg = (Boolean) response;
			if (msg) {
				getMessageList();
			}
		}

		@Override
		public void onError(InAppMessagingError error) {
			Utils.toastOnError(getApplicationContext(), error);
			dismissProgressDialog();
		}
	}

	/*
	 * The Application will request a block of messages from the AT&T Systems by
	 * providing count, limited to 500 and an offset value . authToken will be
	 * used to get access to GetMessageList of InApp Messaging.
	 * 
	 * The response will be handled by the listener : getMessageListListener()
	 */
	public void getMessageList() {
		
		iamManager = new IAMManager(Sdk_Config.fqdn, authToken, m_context,
				new getMessageListListener());
		iamManager.GetMessageList(Config.messageLimit, Config.messageOffset);
	}

	/*
	 * getMessageListListener will be called on getting the response from
	 * GetMessageList(..)
	 * 
	 * onSuccess : This is called when the response returned is a MessageList A
	 * Listview is set with the response MessageList
	 * 
	 * onError: This is called when the response is incorrect The Error along
	 * with the error code is displayed to the user
	 */

	private class getMessageListListener implements ATTIAMListener {

		@Override
		public void onSuccess(Object response) {
			
			msgList = (MessageList) response;
			prevMailboxState = msgList.getState();
			if (null != msgList && null != msgList.getMessages()
					&& msgList.getMessages().size() > 0) {
				messageList = msgList.getMessages();
				adapter = new MessageListAdapter(getApplicationContext(),
						messageList);
				messageListView.setAdapter(adapter);
				adapter.notifyDataSetChanged();
				dismissProgressDialog();
			}
		}

		@Override
		public void onError(InAppMessagingError error) {
			dismissProgressDialog();
			Utils.toastOnError(getApplicationContext(), error);
		}

	}

	/*
	 * This request will check for updates by passing in a client state -
	 * prevMailboxState authToken will be used to get access to GetDelta of
	 * InApp Messaging.
	 * 
	 * The response will be handled by the listener : getDeltaListener()
	 */
	public void updateDelta() {

		if (msgList != null && msgList.getState() != null) {
			iamManager = new IAMManager(Sdk_Config.fqdn, authToken, m_context,
					new getDeltaListener());
			iamManager.GetDelta(prevMailboxState);
		}
	}

	/*
	 * getDeltaListener will be called on getting the response from GetDelta(..)
	 * 
	 * onSuccess : If there is any update in the mailbox, messageList will be
	 * updated. The mailBox state is stored
	 * 
	 * onError: This is called when the response is incorrect The Error along
	 * with the error code is displayed to the user
	 */

	private class getDeltaListener implements ATTIAMListener {

		@Override
		public void onSuccess(Object response) {

			delta = (DeltaResponse) response;

			if (null != delta) {
				prevMailboxState = delta.getState();
				updateMessageList(delta);
			} else {
				dismissProgressDialog();
			}
		}

		@Override
		public void onError(InAppMessagingError error) {
			dismissProgressDialog();
			Utils.toastOnError(getApplicationContext(), error);
		}
	}

	/*
	 * This will allow to update the flags associated with the collection of
	 * messages Any number of messages can be passed authToken will be used to
	 * get access to UpdateMessages of InApp Messaging.
	 * 
	 * The response will be handled by the listener :
	 * updateMessageStatusListener()
	 */

	public void updateMessageStatus(DeltaChange[] statusChange) {
		iamManager = new IAMManager(Sdk_Config.fqdn, authToken, m_context,
				new updateMessageStatusListener());
		iamManager.UpdateMessages(statusChange);

	}

	/*
	 * updateMessageStatusListener will be called on getting the response from
	 * UpdateMessages(..)
	 * 
	 * onSuccess : If there is any update, delete / get the message
	 * 
	 * onError: This is called when the response is false The Error along with
	 * the error code is displayed to the user
	 */

	private class updateMessageStatusListener implements ATTIAMListener {

		@Override
		public void onSuccess(Object response) {

			Boolean msg = (Boolean) response;
			if (msg) {
				deleteMessageFromList(deleteMessageID);
				iamManager = new IAMManager(Sdk_Config.fqdn, authToken, m_context,
						new getMessageListener());
				iamManager.GetMessage(deleteMessageID);
				deleteMessageID = null;
			}

		}

		@Override
		public void onError(InAppMessagingError error) {
			Utils.toastOnError(getApplicationContext(), error);
		}
	}

	/*
	 * The messageId will be passed to get the message associated with that ID
	 * This will get a single message from the message inbox. authToken will be
	 * used to get access to GetMessage of InApp Messaging.
	 * 
	 * The response will be handled by the listener : getMessageListener()
	 */
	public void getMessage(String messageID) {
		iamManager = new IAMManager(Sdk_Config.fqdn, authToken, m_context,
				new getMessageListener());
		iamManager.GetMessage(messageID);
	}

	/*
	 * getMessageListener will be called on getting the response from
	 * GetMessage(..)
	 * 
	 * onSuccess : Fetches the message with the specified ID Sets the
	 * messageListView adapter
	 * 
	 * onError: This is called when the response is incorrect The Error along
	 * with the error code is displayed to the user
	 */

	private class getMessageListener implements ATTIAMListener {

		@Override
		public void onSuccess(Object arg0) {

			Message msg = (Message) arg0;
			if (null != msg) {

				messageList.add(prevIndex, msg);
				prevIndex = 0;
				adapter = new MessageListAdapter(getApplicationContext(),
						messageList);
				Parcelable state = messageListView.onSaveInstanceState();
				messageListView.setAdapter(adapter);
				messageListView.onRestoreInstanceState(state);
				adapter.notifyDataSetChanged();
				dismissProgressDialog();
			}
		}

		@Override
		public void onError(InAppMessagingError arg0) {
			dismissProgressDialog();
			Utils.toastOnError(getApplicationContext(), arg0);
		}

	}

	/*
	 * The message passed will be deleted from the message inbox authToken will
	 * be used to get access to DeleteMessage(..) of InApp Messaging.
	 * 
	 * The response will be handled by the listener : deleteMessagesListener()
	 */

	public void deleteMessage(Message msg) {

		deleteMessageID = msg.getMessageId();
		iamManager = new IAMManager(Sdk_Config.fqdn, authToken, m_context,
				new deleteMessagesListener());
		iamManager.DeleteMessage(deleteMessageID);
	}

	/*
	 * deleteMessagesListener will be called on getting the response from
	 * DeleteMessage(..)
	 * 
	 * onSuccess : deletes the specified message
	 * 
	 * onError: This is called when the response is incorrect The Error along
	 * with the error code is displayed to the user
	 */

	private class deleteMessagesListener implements ATTIAMListener {

		@Override
		public void onSuccess(Object response) {

			Boolean msg = (Boolean) response;
			if (msg) {
				deleteMessageFromList(deleteMessageID);
				deleteMessageID = null;
			}
			dismissProgressDialog();
		}

		@Override
		public void onError(InAppMessagingError error) {

			Utils.toastOnError(getApplicationContext(), error);
			dismissProgressDialog();
		}
	}

	public void onResume() {
		super.onResume();
		updateDelta();
	}

	// MessageList Listener
	public void setupMessageListListener() {

		messageListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				if(((Message) messageListView.getItemAtPosition(position)).isUnread()) {
					DeltaChange[] statusChange = new DeltaChange[1];
					statusChange[0] = new DeltaChange(((Message) messageListView.getItemAtPosition(position)).getMessageId(),
													  ((Message) messageListView.getItemAtPosition(position)).isFavorite(), false);
					deleteMessageID = ((Message) messageListView.getItemAtPosition(position)).getMessageId();
					updateMessageStatus(statusChange);
				}
				
								
				if (((Message) messageListView.getItemAtPosition(position))
						.getType().equalsIgnoreCase("MMS")) {
					Message mmsMessage = (Message) messageListView
							.getItemAtPosition(position);
					ArrayList<MmsContent> mmsContent = mmsMessage
							.getMmsContents();
					Log.d(TAG, "MMS Attachments : " + mmsContent.size());

					String[] mmsContentName = new String[mmsContent.size()], mmsContentType = new String[mmsContent
							.size()], mmsContentUrl = new String[mmsContent
							.size()];

					for (int n = 0; n < mmsContent.size(); n++) {
						MmsContent tmpMmsContent = mmsContent.get(n);
						mmsContentName[n] = tmpMmsContent.getContentName();
						mmsContentType[n] = tmpMmsContent.getContentType();
						mmsContentUrl[n] = tmpMmsContent.getContentUrl();
					}

					/*
					 * STEP 5: getting the contents of the message
					 * 
					 * Returns the contents associated with the identifier
					 * provided in the request.
					 */

					Intent i = new Intent(ConversationList.this,
							MMSContent.class);
					i.putExtra("MMSContentName", mmsContentName);
					i.putExtra("MMSContentType", mmsContentType);
					i.putExtra("MMSContentUrl", mmsContentUrl);
					startActivity(i);

				} else {
					infoDialog((Message) messageListView
							.getItemAtPosition(position));
				}
			}
		});
		
		messageListView
				.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

					public boolean onItemLongClick(AdapterView<?> arg0, View v,
							int position, long arg3) {

						Message msg = (Message) messageListView
								.getItemAtPosition(position);

						CharSequence popUpList[] = new CharSequence[] {
								"Delete Message", "Add to favorites",
								"Mark as Unread" };
						if (msg.isFavorite())
							popUpList[1] = "Remove favorite";
						if (msg.isUnread())
							popUpList[2] = "Mark as Read";

						popUpActionList(popUpList, msg, position);

						return true;
					}
				});
	}

	public void infoDialog(Message selMessage) {
		
		String date = Utils.getDate(selMessage.getTimeStamp().replace('T', ' '));
		
		new AlertDialog.Builder(ConversationList.this)
				.setTitle("Message details")
				.setMessage(
						"Type : " + selMessage.getType() + "\n" + "From : "
								+ selMessage.getFrom() + "\n" + "Received : "
								+ date)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();					
					}
				}).show();
	}

	public void popUpActionList(final CharSequence popUpList[],
			final Message msg, int position) {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Message Options");
		builder.setItems(popUpList, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				DeltaChange[] statusChange = new DeltaChange[1];
				// the user clicked on colors[which]
				switch (which) {
				case 0:
					deleteMessage(msg);
					showProgressDialog("Deleting Message");
					break;

				case 1: {
					if (popUpList[1].toString().equalsIgnoreCase(
							"Add to favorites")) {
						// Set message as favorite
						statusChange[0] = new DeltaChange(msg.getMessageId(),
								true, msg.isUnread());
						showProgressDialog("Adding to favorites");
					} else {
						// Remove favorite
						statusChange[0] = new DeltaChange(msg.getMessageId(),
								false, msg.isUnread());
						showProgressDialog("Removing favorites");
					}
					deleteMessageID = msg.getMessageId();
					updateMessageStatus(statusChange);
				}
					break;

				case 2: {
					if (popUpList[2].toString().equalsIgnoreCase(
							"Mark as Unread")) {
						// Set as unread
						statusChange[0] = new DeltaChange(msg.getMessageId(),
								msg.isFavorite(), true);
						showProgressDialog("Marking as unread ...");
					} else {
						// Mark as read
						statusChange[0] = new DeltaChange(msg.getMessageId(),
								msg.isFavorite(), false);
						showProgressDialog("Marking as read ...");
					}
					deleteMessageID = msg.getMessageId();
					updateMessageStatus(statusChange);
				}
					break;

				default:
					break;
				}
			}
		});
		builder.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.action_conversation_list, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Take appropriate action for each action item click
		switch (item.getItemId()) {

		case R.id.action_new_message: {
			/*
			 * STEP 4: creating a new message
			 * 
			 * A new message will be created and sent to the recipient mentioned
			 * in the TO list
			 */

			startActivityForResult(new Intent(ConversationList.this,
					NewMessage.class), NEW_MESSAGE);
			break;
		}
		case R.id.action_logout: {
			CookieSyncManager.createInstance(this);
			CookieManager cookieManager = CookieManager.getInstance();
			cookieManager.removeAllCookie();
			cookieManager.removeExpiredCookie();
		    cookieManager.removeSessionCookie(); 
			finish();
			break;
		}
		case R.id.action_preset: {
			 Intent presetedIntent = new Intent(getApplicationContext(), DebugSettings.class);
	   	 	 startActivity(presetedIntent);
			break;
		}
		case R.id.action_refresh: {
			updateDelta();
			break;
		}
		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}

	// Progress Dialog
	public void showProgressDialog(String dialogMessage) {

		if (null == pDialog)
			pDialog = new ProgressDialog(this);
		pDialog.setCancelable(false);
		pDialog.setMessage(dialogMessage);
		pDialog.show();
	}

	public void dismissProgressDialog() {
		if (null != pDialog) {
			pDialog.dismiss();
		}
	}

	public void onMessageListReady(MessageList messageList) {

		if (adapter != null) {
			adapter.notifyDataSetChanged();
		}
	}

	public void clearCache() {
		CookieSyncManager.createInstance(this);
		CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.removeAllCookie();
	}

	public void updateMessageList(DeltaResponse deltaResponse) {

		int nChanges = deltaResponse.getDeltaChanges().length;

		String messageID[] = new String[nChanges];

		for (int n = 0; n < nChanges; n++) {
			messageID[n] = deltaResponse.getDeltaChanges()[n].getMessageId();

			ChangeType chType = deltaResponse.getDeltaChanges()[n]
					.getChangeType();

			switch (chType) {
			case ADD: {
					iamManager = new IAMManager(Sdk_Config.fqdn, authToken, m_context,
							new getMessageListener());
					iamManager.GetMessage(messageID[n]);
				}
				break;
				
			case DELETE: {
					deleteMessageFromList(deltaResponse.getDeltaChanges()[n]
							.getMessageId());
				}
				break;
				
			case NONE:
				break;
				
			case UPDATE: {

					deleteMessageFromList(deltaResponse.getDeltaChanges()[n]
							.getMessageId());
					adapter.notifyDataSetChanged();
					iamManager = new IAMManager(Sdk_Config.fqdn, authToken, m_context,
							new getMessageListener());
					iamManager.GetMessage(messageID[n]);
				}
				break;
				
			default:
				dismissProgressDialog();
				break;
			}
		}
	}

	public void deleteMessageFromList(String msgID) {

		int deleteNthMessage;
		for (deleteNthMessage = 0; deleteNthMessage < messageList.size(); deleteNthMessage++) {
			if (messageList.get(deleteNthMessage).getMessageId()
					.equalsIgnoreCase(msgID))
				break;
		}
		if (deleteNthMessage < messageList.size()) {
			prevIndex = deleteNthMessage;
			adapter.deleteItem(deleteNthMessage);
			adapter.notifyDataSetChanged();
		}
		dismissProgressDialog();
	}	
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		System.exit(0);
	}
	
	public class GetNewTokenViaRefreshToken extends AsyncTask<String, Void , OAuthToken> {
		
	     OAuthToken m_authToken;
		 
		@Override
		protected  OAuthToken doInBackground(String... params) {
			// TODO Auto-generated method stub
			Looper.prepare();
			OAuthService m_osrvc = new OAuthService(params[0], params[1], params[2]);
		
			try {
					m_authToken = m_osrvc.refreshToken(params[3]);
			} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return null;		
			}
			return m_authToken;
		}

		@Override
		protected void onPostExecute( OAuthToken ret_authToken) {
			super.onPostExecute(ret_authToken);
			if (null != ret_authToken) {
				onSuccess(ret_authToken);
			}
			else {
				pref.setString("Token", Sdk_Config.none);
				finish();
		   }
		}
		
	private void onSuccess(OAuthToken ret_Token) {	
		
			pref.setString("Token", ret_Token.getAccessToken());
			pref.setString("RefreshToken", ret_Token.getRefreshToken());
			pref.setLong("AccessTokenExpiry", ret_Token.getAccessTokenExpiry());	
			authToken = ret_Token;
			getMessageIndexInfo();
			Log.i("I AM HERE  " , "------------------------------");
		    
		}
			
	}

}
