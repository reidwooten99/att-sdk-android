package com.att.iamsampleapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.Toast;

import com.att.api.immn.listener.ATTIAMListener;
import com.att.api.immn.service.ChangeType;
import com.att.api.immn.service.DeltaChange;
import com.att.api.immn.service.DeltaResponse;
import com.att.api.immn.service.IAMManager;
import com.att.api.immn.service.IMMNService;
import com.att.api.immn.service.Message;
import com.att.api.immn.service.MessageIndexInfo;
import com.att.api.immn.service.MessageList;
import com.att.api.immn.service.MmsContent;
import com.att.api.oauth.OAuthService;
import com.att.api.oauth.OAuthToken;

public class ConversationList extends Activity {

	private static final String TAG = "Conversation List";

	ListView messageListView;
	MessageListAdapter adapter;
	IMMNService immnSrvc;
	IAMManager iamManager;
	OAuthService osrvc;
	final int REQUEST_CODE = -1;
	final int NEW_MESSAGE = 2;
	final int OAUTH_CODE = 1;
	OAuthToken authToken;
	MessageIndexInfo msgIndexInfo;
	DeltaResponse delta;
	MessageList msgList;
	Message messageList[];
	String prevMailboxState;
	String deleteMessageID;
	int prevIndex;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_conversation_list);
		showProgressDialog("Loading Messages .. ");
		messageListView = (ListView) findViewById(R.id.messageListViewItem);

		// Create service for requesting an OAuth token
		osrvc = new OAuthService(Config.fqdn, Config.clientID, Config.secretKey);

		Intent i = new Intent(this,
				com.att.api.consentactivity.UserConsentActivity.class);
		i.putExtra("fqdn", Config.fqdn);
		i.putExtra("clientId", Config.clientID);
		i.putExtra("clientSecret", Config.secretKey);
		startActivityForResult(i, OAUTH_CODE);
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

			startActivityForResult(new Intent(ConversationList.this,
					NewMessage.class), NEW_MESSAGE);
			break;
		}
		case R.id.action_logout: {
			// pablo - Logout scenario check - S
			CookieSyncManager.createInstance(this);
			CookieManager cookieManager = CookieManager.getInstance();
			cookieManager.removeAllCookie();
			cookieManager.removeExpiredCookie();
			cookieManager.removeSessionCookie();
			// pablo - Logout scenario check - E
			finish();
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

	public void onResume() {
		super.onResume();

		setupMessageListListener();

		updateDelta();
	}

	// MessageList Listener
	public void setupMessageListListener() {

		messageListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				if (((Message) messageListView.getItemAtPosition(position))
						.getType().equalsIgnoreCase("MMS")) {
					Message mmsMessage = (Message) messageListView
							.getItemAtPosition(position);
					MmsContent[] mmsContent = mmsMessage.getMmsContents();
					Log.d(TAG, "MMS Attachments : " + mmsContent.length);

					String[] mmsContentName = new String[mmsContent.length], mmsContentType = new String[mmsContent.length], mmsContentUrl = new String[mmsContent.length], mmsType = new String[mmsContent.length];

					for (int n = 0; n < mmsContent.length; n++) {
						mmsContentName[n] = mmsContent[n].getContentName();
						mmsContentType[n] = mmsContent[n].getContentType();
						mmsContentUrl[n] = mmsContent[n].getContentUrl();
						//mmsType[n] = mmsContent[n].getType().toString();
					}

					Intent i = new Intent(ConversationList.this,
							MMSContent.class);
					i.putExtra("MMSContentName", mmsContentName);
					i.putExtra("MMSContentType", mmsContentType);
					i.putExtra("MMSContentUrl", mmsContentUrl);
					//i.putExtra("MMSType", mmsType);
					startActivity(i);

				} else {
					infoDialog((Message) messageListView
							.getItemAtPosition(position));
				}
				// Launch the Message View Screen here
				Utils.toastHere(getApplicationContext(), TAG,
						msgList.getMessages()[position].getText());
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

		new AlertDialog.Builder(ConversationList.this)
				.setTitle("Message details")
				.setMessage(
						"Type : " + selMessage.getType() + "\n" + "From : "
								+ selMessage.getFrom() + "\n" + "Received : "
								+ selMessage.getTimeStamp())
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

	public void updateMessageStatus(DeltaChange[] statusChange) {
		iamManager = new IAMManager(Config.fqdn, authToken,
				new updateMessageStatusListener());
		iamManager.UpdateMessages(statusChange);

	}

	public void deleteMessage(Message msg) {

		deleteMessageID = msg.getMessageId();
		iamManager = new IAMManager(Config.fqdn, authToken,
				new deleteMessagesListener());
		iamManager.DeleteMessage(deleteMessageID);
	}

	ProgressDialog pDialog;

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

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == NEW_MESSAGE) {
			if (resultCode == RESULT_OK) {

				Utils.toastHere(getApplicationContext(), TAG, "Message Sent : "
						+ data.getStringExtra("MessageResponse"));

				// updateDelta();
			}
		} else if (requestCode == OAUTH_CODE) {
			String oAuthCode = null;
			if (resultCode == RESULT_OK) {
				oAuthCode = data.getStringExtra("oAuthCode");
				Log.i("mainActivity", "oAuthCode:" + oAuthCode);
				if (null != oAuthCode) {
					osrvc.getOAuthToken(oAuthCode, new getTokenListener());
				} else {
					Log.i("mainActivity", "oAuthCode: is null");

				}
			}
		}
	}

	public void updateDelta() {

		if (msgList != null && msgList.getState() != null) {
			// showProgressDialog("Checking for new messages ...");
			iamManager = new IAMManager(Config.fqdn, authToken,
					new getDeltaListener());
			iamManager.GetDelta(prevMailboxState);
		}
	}

	private class getTokenListener implements ATTIAMListener {

		@Override
		public void onSuccess(Object response) {
			// TODO Auto-generated method stub
			authToken = (OAuthToken) response;
			if (null != authToken) {
				Config.token = authToken.getAccessToken();
				Config.refreshToken = authToken.getRefreshToken();
				Log.i("getTokenListener",
						"onSuccess Message : " + authToken.getAccessToken());
				createMessageIndex();
				updateDelta();
			}
		}

		@Override
		public void onError(Object error) {
			// TODO Auto-generated method stub
			Toast toast = Toast.makeText(getApplicationContext(), "Message : "
					+ "Iam in  getTokenListener Error Callback",
					Toast.LENGTH_LONG);
			toast.show();
		}
	}

	public void createMessageIndex() {

		// CreateMessageIndexInfo call from Sample App
		iamManager = new IAMManager(Config.fqdn, authToken,
				new createMessageIndexListener());
		iamManager.CreateMessageIndex();
	}

	public void getMessageList() {

		// GetMessageList call from SampleApp
		iamManager = new IAMManager(Config.fqdn, authToken,
				new getMessageListListener());

		// Check how can you provide a dynamic values here ???
		iamManager.GetMessageList(Config.messageLimit, Config.messageOffset);
	}

	private class updateMessageStatusListener implements ATTIAMListener {

		@Override
		public void onSuccess(Object response) {

			Boolean msg = (Boolean) response;
			if (msg) {
				Toast toast = Toast.makeText(getApplicationContext(),
						"updateMessagesListener onSuccess : Message : " + msg,
						Toast.LENGTH_LONG);
				toast.show();
				deleteMessageFromList(deleteMessageID);
				iamManager = new IAMManager(Config.fqdn, authToken,
						new getMessageListener());
				iamManager.GetMessage(deleteMessageID);
				deleteMessageID = null;
			}

		}

		@Override
		public void onError(Object error) {

			Toast toast = Toast.makeText(getApplicationContext(), "Message : "
					+ "Iam in  updateMessagesListener Error Callback",
					Toast.LENGTH_LONG);
			toast.show();
		}

	}

	private class deleteMessagesListener implements ATTIAMListener {

		@Override
		public void onSuccess(Object response) {

			Boolean msg = (Boolean) response;
			if (msg) {

				deleteMessageFromList(deleteMessageID);
				deleteMessageID = null;

				Utils.toastHere(getApplicationContext(), TAG,
						"deleteMessagesListener onSuccess : " + msg);
			}
			dismissProgressDialog();
		}

		@Override
		public void onError(Object error) {

			Toast toast = Toast.makeText(getApplicationContext(), "Message : "
					+ "Iam in  deleteMessagesListener Error Callback",
					Toast.LENGTH_LONG);
			toast.show();
			dismissProgressDialog();
		}
	}

	private class createMessageIndexListener implements ATTIAMListener {

		@Override
		public void onSuccess(Object response) {

			Boolean msg = (Boolean) response;
			if (msg)
				Utils.toastHere(getApplicationContext(), TAG,
						"createMessageIndexListener onSuccess : Message : "
								+ msg);

			getMessageList();
		}

		@Override
		public void onError(Object error) {

			Utils.toastHere(getApplicationContext(), TAG, "Message : "
					+ "Iam in  createMessageIndexListener Error Callback");
		}
	}

	public void getMessageIndexInfo() {

		iamManager = new IAMManager(Config.fqdn, authToken,
				new getMessageIndexInfoListener());
		iamManager.GetMessageIndexInfo();
	}

	public void getMessage(String messageID) {

		// GetMessage Call from SampleApp
		iamManager = new IAMManager(Config.fqdn, authToken,
				new getMessageListener());
		iamManager.GetMessage(messageID);
	}

	public void onMessageListReady(MessageList messageList) {

		if (adapter != null) {
			adapter.notifyDataSetChanged();
		}
	}

	private class getMessageListener implements ATTIAMListener {

		@Override
		public void onError(Object arg0) {
			dismissProgressDialog();
			Utils.toastHere(getApplicationContext(), TAG,
					"In  getMessageListener Error Callback");
		}

		@Override
		public void onSuccess(Object arg0) {

			Message msg = (Message) arg0;
			if (null != msg) {

				Message[] msgs = new Message[messageList.length + 1];

				msgs[prevIndex] = msg;

				if (prevIndex > 0)
					System.arraycopy(messageList, 0, msgs, 0, prevIndex);
				System.arraycopy(messageList, prevIndex, msgs, prevIndex + 1,
						messageList.length - prevIndex);

				messageList = msgs;
				prevIndex = 0;

				adapter = new MessageListAdapter(getApplicationContext(),
						messageList);

				messageListView.setAdapter(adapter);

				dismissProgressDialog();
				Utils.toastHere(
						getApplicationContext(),
						TAG,
						" getMessageListener onSuccess Message : "
								+ msg.getText());
			}
		}
	}

	public void clearCache() {
		CookieSyncManager.createInstance(this);
		CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.removeAllCookie();
	}

	private class getDeltaListener implements ATTIAMListener {

		@Override
		public void onSuccess(Object response) {

			delta = (DeltaResponse) response;

			if (null != delta) {

				prevMailboxState = delta.getState();

				Utils.toastHere(
						getApplicationContext(),
						TAG,
						"getDeltaListener onSuccess : Message : "
								+ delta.getState());

				/*
				 * int nChanges = delta.getDeltaChanges().length;
				 * 
				 * String messageID[] = new String[nChanges];
				 * 
				 * for (int n = 0; n < nChanges; n++) { messageID[n] =
				 * delta.getDeltaChanges()[n].getMessageId(); }
				 */

				updateMessageList(delta);
			} else {
				dismissProgressDialog();
			}
		}

		@Override
		public void onError(Object error) {
			dismissProgressDialog();
			Utils.toastHere(getApplicationContext(), TAG, "Message : "
					+ "Iam in  getDeltaListener Error Callback");
		}
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
				iamManager = new IAMManager(Config.fqdn, authToken,
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
				iamManager = new IAMManager(Config.fqdn, authToken,
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
		for (deleteNthMessage = 0; deleteNthMessage < messageList.length; deleteNthMessage++) {
			if (messageList[deleteNthMessage].getMessageId().equalsIgnoreCase(
					msgID))
				break;
		}
		if (deleteNthMessage < messageList.length) {
			prevIndex = deleteNthMessage;
			adapter.deleteItem(deleteNthMessage);
			adapter.notifyDataSetChanged();
		}
		dismissProgressDialog();
	}

	private class getMessageIndexInfoListener implements ATTIAMListener {

		@Override
		public void onSuccess(Object response) {

			msgIndexInfo = (MessageIndexInfo) response;
			if (null != msgIndexInfo) {
				Utils.toastHere(getApplicationContext(), TAG,
						"getMessageIndexInfoListener onSuccess : Message : "
								+ msgIndexInfo.getState());

				// getDelta(msgIndexInfo.getState());
			}

		}

		@Override
		public void onError(Object error) {

			Utils.toastHere(getApplicationContext(), TAG, "Message : "
					+ "Iam in  getMessageIndexInfoListener Error Callback");
		}

	}

	private class getMessageListListener implements ATTIAMListener {

		@Override
		public void onSuccess(Object response) {

			msgList = (MessageList) response;

			messageList = msgList.getMessages();
			prevMailboxState = msgList.getState();
			if (null != msgList && null != msgList.getMessages() && msgList.getMessages().length>0) {
				Utils.toastHere(
						getApplicationContext(),
						TAG,
						"getMessageListListener onSuccess : Message : "
								+ msgList.getMessages()[0].getText()
								+ ", From : "
								+ msgList.getMessages()[0].getFrom());
				adapter = new MessageListAdapter(getApplicationContext(),
						msgList.getMessages());

				messageListView.setAdapter(adapter);

				dismissProgressDialog();
				getMessageIndexInfo();
			}
		}

		@Override
		public void onError(Object error) {
			Utils.toastHere(getApplicationContext(), TAG, "Message : "
					+ "Iam in  getMessageListListener Error Callback");
		}

	}

}
