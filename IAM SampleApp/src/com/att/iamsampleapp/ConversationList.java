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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.att.api.immn.listener.ATTIAMListener;
import com.att.api.immn.service.DeltaChange;
import com.att.api.immn.service.DeltaResponse;
import com.att.api.immn.service.IAMManager;
import com.att.api.immn.service.IMMNService;
import com.att.api.immn.service.Message;
import com.att.api.immn.service.MessageIndexInfo;
import com.att.api.immn.service.MessageList;
import com.att.api.oauth.OAuthService;
import com.att.api.oauth.OAuthToken;

public class ConversationList extends Activity {

	private static final String TAG = "Conversation List";

	ListView messageListView;
	ListCustomAdapter adapter;
	IMMNService immnSrvc;
	IAMManager iamManager;
	OAuthService osrvc;
	final int REQUEST_CODE = 1;
	OAuthToken authToken;
	MessageIndexInfo msgIndexInfo;
	DeltaResponse delta;
	MessageList msgList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_conversation_list);
		showProgressDialog("Loading Messages .. ");
		messageListView = (ListView) findViewById(R.id.messageListViewItem);

		// Create service for requesting an OAuth token
		osrvc = new OAuthService(Config.fqdn(), Config.clientID(),
				Config.secretKey());
		authToken = new OAuthToken(Config.token(), OAuthToken.NO_EXPIRATION,
				Config.refreshToken());
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
        
        case R.id.action_new_message:
        {
        	Intent newMessage = new Intent(getApplicationContext(),
					NewMessage.class);
			startActivityForResult(newMessage, REQUEST_CODE);
        }
        case R.id.action_settings:
            // refresh
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
	
	public void onResume() {
		super.onResume();

		createMessageIndex();

		setupMessageListListener();
	}

	// MessageList Listener
	public void setupMessageListListener() {

		messageListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				// Launch the Message View Screen here
				Toast toast = Toast.makeText(
						getApplicationContext(),
						"Message : "
								+ msgList.getMessages()[position].getText(),
						Toast.LENGTH_LONG);
				toast.show();
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

	public void popUpActionList(final CharSequence popUpList[],
			final Message msg, int position) {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Message Options");
		builder.setItems(popUpList, new DialogInterface.OnClickListener() {
			@SuppressWarnings("null")
			@Override
			public void onClick(DialogInterface dialog, int which) {
				DeltaChange[] statusChange = new DeltaChange[1];
				// the user clicked on colors[which]
				switch (which) {
				case 0:
					deleteMessage(msg);
					break;

				case 1: {
					if (popUpList[1].toString().equalsIgnoreCase(
							"Add to favorites")) {
						// Set message as favorite
						statusChange[0] = new DeltaChange(msg.getMessageId(),
								true, msg.isUnread());
					} else {
						// Remove favorite
						statusChange[0] = new DeltaChange(msg.getMessageId(),
								false, msg.isUnread());
					}
					updateMessageStatus(statusChange);
				}
					break;

				case 2: {
					if (popUpList[2].toString().equalsIgnoreCase(
							"Mark as Unread")) {
						// Set as unread
						statusChange[0] = new DeltaChange(msg.getMessageId(),
								msg.isFavorite(), true);
					} else {
						// Mark as read
						statusChange[0] = new DeltaChange(msg.getMessageId(),
								msg.isFavorite(), false);
					}
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
		iamManager = new IAMManager(Config.fqdn(), authToken,
				new updateMessageStatusListener());
		iamManager.UpdateMessages(statusChange);

	}

	public void deleteMessage(Message msg) {
		iamManager = new IAMManager(Config.fqdn(), authToken,
				new deleteMessagesListener());
		iamManager.DeleteMessage(msg.getMessageId());
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

	public void newMessage(View v) {

			Intent newMessage = new Intent(getApplicationContext(),
					NewMessage.class);
			startActivityForResult(newMessage, REQUEST_CODE);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == REQUEST_CODE) {

			if (resultCode == RESULT_OK) {

				String result = data.getStringExtra("MessageResponse");
				Toast toast = Toast.makeText(getApplicationContext(),
						"Message Sent : " + result, Toast.LENGTH_LONG);
				Utils.toastMe(toast);
			}
		}
	}

	public void createMessageIndex() {

		// CreateMessageIndexInfo call from Sample App
		iamManager = new IAMManager(Config.fqdn(), authToken,
				new createMessageIndexListener());
		iamManager.CreateMessageIndex();
	}

	public void getMessageList() {

		// GetMessageList call from SampleApp
		iamManager = new IAMManager(Config.fqdn(), authToken,
				new getMessageListListener());

		// Check how can you provide a dynamic values here ???
		// iamManager.GetMessageList(10, 0);
		iamManager.GetMessageList(Config.messageLimit(),
				Config.getMessageOffset());
	}

	private class updateMessageStatusListener implements ATTIAMListener {

		@Override
		public void onSuccess(Object response) {
			// TODO Auto-generated method stub

			Boolean msg = (Boolean) response;
			if (msg) {
				Toast toast = Toast.makeText(getApplicationContext(),
						"updateMessagesListener onSuccess : Message : " + msg,
						Toast.LENGTH_LONG);
				toast.show();
			}

		}

		@Override
		public void onError(Object error) {
			// TODO Auto-generated method stub
			Toast toast = Toast.makeText(getApplicationContext(), "Message : "
					+ "Iam in  updateMessagesListener Error Callback",
					Toast.LENGTH_LONG);
			toast.show();
		}

	}

	private class deleteMessagesListener implements ATTIAMListener {

		@Override
		public void onSuccess(Object response) {
			// TODO Auto-generated method stub

			Boolean msg = (Boolean) response;
			if (msg) {
				Toast toast = Toast.makeText(getApplicationContext(),
						"deleteMessagesListener onSuccess : Message : " + msg,
						Toast.LENGTH_LONG);
				toast.show();
			}
		}

		@Override
		public void onError(Object error) {
			// TODO Auto-generated method stub
			Toast toast = Toast.makeText(getApplicationContext(), "Message : "
					+ "Iam in  deleteMessagesListener Error Callback",
					Toast.LENGTH_LONG);
			toast.show();
		}
	}

	private class createMessageIndexListener implements ATTIAMListener {

		@Override
		public void onSuccess(Object response) {
			// TODO Auto-generated method stub

			Boolean msg = (Boolean) response;
			if (msg) {
				Toast toast = Toast.makeText(getApplicationContext(),
						"createMessageIndexListener onSuccess : Message : "
								+ msg, Toast.LENGTH_LONG);
				Utils.toastMe(toast);
			}

			getMessageList();

		}

		@Override
		public void onError(Object error) {
			// TODO Auto-generated method stub
			Toast toast = Toast.makeText(getApplicationContext(), "Message : "
					+ "Iam in  createMessageIndexListener Error Callback",
					Toast.LENGTH_LONG);
			Utils.toastMe(toast);
		}

	}

	public void getMessageIndexInfo() {

		iamManager = new IAMManager(Config.fqdn(), authToken,
				new getMessageIndexInfoListener());
		iamManager.GetMessageIndexInfo();
	}

	public void getDelta(String state) {

		// GetDelta call from SampleApp
		iamManager = new IAMManager(Config.fqdn(), authToken,
				new getDeltaListener());
		iamManager.GetDelta(state);

	}

	public void getMessage(String messageID) {

		// GetMessage Call from SampleApp
		iamManager = new IAMManager(Config.fqdn(), authToken,
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
			// TODO Auto-generated method stub
			Toast toast = Toast.makeText(getApplicationContext(), "Message : "
					+ "Iam in  getMessageListener Error Callback",
					Toast.LENGTH_LONG);
			Utils.toastMe(toast);
		}

		@Override
		public void onSuccess(Object arg0) {
			// TODO Auto-generated method stub
			Message msg = (Message) arg0;
			if (null != msg) {
				Toast toast = Toast.makeText(
						getApplicationContext(),
						" getMessageListener onSuccess Message : "
								+ msg.getText(), Toast.LENGTH_LONG);
				Utils.toastMe(toast);
			}
		}
	}

	private class getDeltaListener implements ATTIAMListener {

		@Override
		public void onSuccess(Object response) {
			// TODO Auto-generated method stub

			delta = (DeltaResponse) response;
			if (null != delta) {
				Toast toast = Toast.makeText(
						getApplicationContext(),
						"getDeltaListener onSuccess : Message : "
								+ delta.getState(), Toast.LENGTH_LONG);
				Utils.toastMe(toast);

				/*
				 * String getMsgID =
				 * delta.delta[0].getUpdates()[0].getMessageId();
				 * 
				 * Log.i(TAG,delta.delta[0].getUpdates()[0].getMessageId());
				 * 
				 * getMessage(delta.delta[0].getUpdates()[0].getMessageId());
				 */

			}

		}

		@Override
		public void onError(Object error) {
			// TODO Auto-generated method stub
			Toast toast = Toast.makeText(getApplicationContext(), "Message : "
					+ "Iam in  getDeltaListener Error Callback",
					Toast.LENGTH_LONG);
			Utils.toastMe(toast);
		}

	}

	private class getMessageIndexInfoListener implements ATTIAMListener {

		@Override
		public void onSuccess(Object response) {
			// TODO Auto-generated method stub

			msgIndexInfo = (MessageIndexInfo) response;
			if (null != msgIndexInfo) {
				Toast toast = Toast.makeText(getApplicationContext(),
						"getMessageIndexInfoListener onSuccess : Message : "
								+ msgIndexInfo.getState(), Toast.LENGTH_LONG);
				Utils.toastMe(toast);

				getDelta(msgIndexInfo.getState());
			}

		}

		@Override
		public void onError(Object error) {
			// TODO Auto-generated method stub
			Toast toast = Toast.makeText(getApplicationContext(), "Message : "
					+ "Iam in  getMessageIndexInfoListener Error Callback",
					Toast.LENGTH_LONG);
			Utils.toastMe(toast);
		}

	}

	private class getMessageListListener implements ATTIAMListener {

		@Override
		public void onSuccess(Object response) {
			// TODO Auto-generated method stub

			msgList = (MessageList) response;
			if (null != msgList) {
				Toast toast = Toast.makeText(
						getApplicationContext(),
						"getMessageListListener onSuccess : Message : "
								+ msgList.getMessages()[0].getText()
								+ ", From : "
								+ msgList.getMessages()[0].getFrom(),
						Toast.LENGTH_LONG);
				Utils.toastMe(toast);
				Log.i(TAG, "getMessageListListener onSuccess " + ": Message : "
						+ msgList.getMessages()[0].getText() + ", From : "
						+ msgList.getMessages()[0].getFrom());

				adapter = new ListCustomAdapter(getApplicationContext(),
						msgList.getMessages());
				messageListView.setAdapter(adapter);

				dismissProgressDialog();

				getMessageIndexInfo();
			}
		}

		@Override
		public void onError(Object error) {
			// TODO Auto-generated method stub
			Toast toast = Toast.makeText(getApplicationContext(), "Message : "
					+ "Iam in  getMessageListListener Error Callback",
					Toast.LENGTH_LONG);
			Utils.toastMe(toast);
		}

	}

}
