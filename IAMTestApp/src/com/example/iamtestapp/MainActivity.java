package com.example.iamtestapp;

import java.text.ParseException;

import org.json.JSONException;

import android.R.string;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.att.api.immn.listener.ATTIAMListener;
import com.att.api.immn.service.DeltaChange;
import com.att.api.immn.service.DeltaResponse;
import com.att.api.immn.service.IAMManager;
import com.att.api.immn.service.IMMNService;
import com.att.api.immn.service.Message;
import com.att.api.immn.service.MessageContent;
import com.att.api.immn.service.MessageIndexInfo;
import com.att.api.immn.service.MessageList;
import com.att.api.immn.service.NotificationConnectionDetails;
import com.att.api.immn.service.SendResponse;
import com.att.api.oauth.OAuthService;
import com.att.api.oauth.OAuthToken;
import com.att.api.rest.RESTException;

public class MainActivity extends Activity implements ATTIAMListener {

	IMMNService immnSrvc;
	IAMManager iamManager;
	TextView value;
	OAuthService osrvc;
	final int REQUEST_CODE = 1;
	OAuthToken msg ;
	final String fqdn = "https://api.att.com"; //"https://api-stage.mars.bf.sl.attcompute.com";

	// Enter the value from 'App Key' field
	final String clientId = "hahcoflonje5cxctdbpwtjg966imi6v1";

	// Enter the value from 'Secret' field
	final String clientSecret = "csy2s6hseuwkydi2lcixj8j6emh6skq8";

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		value = (TextView) findViewById(R.id.textView);
		value.setText("Textview");
		
		// Use the app settings from developer.att.com for the following
		// values. Make sure IMMN is enabled for the app key/secret.
		/*final String fqdn = "https://api.att.com"; //"https://api-stage.mars.bf.sl.attcompute.com";

		// Enter the value from 'App Key' field
		final String clientId = "hahcoflonje5cxctdbpwtjg966imi6v1";

		// Enter the value from 'Secret' field
		final String clientSecret = "csy2s6hseuwkydi2lcixj8j6emh6skq8";
*/
		// Create service for requesting an OAuth token
		 osrvc = new OAuthService(fqdn, clientId, clientSecret);

		// Get the OAuth code by opening a browser to the following URL:
		// https://api.att.com/oauth/authorize?client_id=CLIENT_ID&scope=SCOPE&redirect_uri=REDIRECT_URI
		// replacing CLIENT_ID, SCOPE, and REDIRECT_URI with the values
		// configured at
		// developer.att.com. After authenticating, copy the oauth code from the
		// browser URL.
		final String oauthCode = "ENTER VALUE!";

		// Get OAuth token using the code
		//osrvc.Authorize(this, new getTokenListener());
				
		/*OAuthToken token = new OAuthToken("Fc2cS00WsFv0AHxzbH88Y8Ip7KWNgSz7",
				0, null);*/
		Intent i = new Intent(this,com.att.api.consentactivity.UserConsentActivity.class);
		i.putExtra("fqdn", fqdn);
		i.putExtra("clientId", clientId);
		i.putExtra("clientSecret", clientSecret);
		startActivityForResult(i, REQUEST_CODE);


		// SendMessage Call from SampleApp
		/*iamManager = new IAMManager(fqdn, token, new sendMessageListener());
		iamManager.SendMessage("4257492983",
				"This is an example message for Android App");*/

		// GetMessage Call from SampleApp
		/*iamManager = new IAMManager(fqdn, token, new getMessageListener());
		iamManager.GetMessage("t191");*/
		
		//GetMessageList call from SampleApp
		/*iamManager = new IAMManager(fqdn, msg, new getMessageListListener());
		iamManager.GetMessageList(10, 0);*/
	
		//GetMessageContent call from SampleApp
		/*iamManager = new IAMManager(fqdn, token, new getMessageContentListener());
		iamManager.GetMessageContent("S60", "0");*/
		
		//GetDelta call from SampleApp
		/*iamManager = new IAMManager(fqdn, token, new getDeltaListener());
		iamManager.GetDelta("1390033973822");*/
		
		//DeleteMessages call from SampleApp
		/*String[] delMessageIds = {"t191", "t192"};
		iamManager = new IAMManager(fqdn, token, new deleteMessagesListener());
		iamManager.DeleteMessages(delMessageIds);*/
		
		//DeleteMessage call from SampleApp
		/*iamManager = new IAMManager(fqdn, token,  new deleteMessageListener());
		iamManager.DeleteMessage("t179");*/
		
		//CreateMessageIndexInfo call from Sample App
		/*iamManager = new IAMManager(fqdn, msg,  new createMessageIndexListener());
		iamManager.CreateMessageIndex();*/
		
		//GetMessageIndexInfo call from Sample App
		/*iamManager = new IAMManager(fqdn, token, new getMessageIndexInfoListener());
		iamManager.GetMessageIndexInfo();*/
		
		//GetNotificationConnectionDetails call from Sample App
		/*iamManager = new IAMManager(fqdn, token,  new getNotificationConnectionDetailsListener());
		iamManager.GetNotificationConnectionDetails("MMS");*/
		
		//UpdateMessages call from Sample App
		/*DeltaChange[] updateMessages = new DeltaChange[1];
		updateMessages[0] = new DeltaChange("t204",true,true);	
		iamManager = new IAMManager(fqdn, token, new updateMessagesListener());
		iamManager.UpdateMessages(updateMessages);*/
		
		//UpdateMessage call form Sample App
		/*iamManager = new IAMManager(fqdn, token, new updateMessageListener());
		iamManager.UpdateMessage("t204", true, true);*/
	}
	 
	//getTokenListener gtnl = new getTokenListener();
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		String oAuthCode = null;
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == REQUEST_CODE) {
			if(resultCode == RESULT_OK) {
				 oAuthCode  = data.getStringExtra("oAuthCode");
				 Log.i("mainActivity","oAuthCode:" + oAuthCode );
				 if(null != oAuthCode) {
					 osrvc.getOAuthToken(oAuthCode,new getTokenListener());				  
				 } else {
					 Log.i("mainActivity","oAuthCode: is null" );

				 }
			}
		}
		 
	}

	private class getTokenListener implements ATTIAMListener {

		@Override
		public void onSuccess(Object response) {
			// TODO Auto-generated method stub
			 msg = (OAuthToken) response;
			if(null != msg) {
				Log.i("getTokenListener","onSuccess Message : "  + msg.getAccessToken());
				/*Toast toast = Toast.makeText(getApplicationContext(),
						" getTokenListener onSuccess Message : " + msg.getAccessToken(), Toast.LENGTH_LONG);
				toast.show();
*/			}
			iamManager = new IAMManager(fqdn, msg,  new createMessageIndexListener());
			iamManager.CreateMessageIndex();
			
			iamManager = new IAMManager(fqdn, msg, new sendMessageListener());
			//iamManager.SendMessage("4257492983","This is an example message for Android App Demo rehearsal");
			String addresses[] = { "4257492983" };
			String attachments[] = { null };
		
			iamManager.SendMessage(addresses, "This is an example message for Android App Demo rehearsal",
									null, false, attachments);

			
			iamManager = new IAMManager(fqdn, msg, new getMessageListListener());
			iamManager.GetMessageList(10, 0);
			
			
			
		}

		@Override
		public void onError(Object error) {
			// TODO Auto-generated method stub
			Toast toast = Toast.makeText(getApplicationContext(), "Message : "
					+ "Iam in  getTokenListener Error Callback", Toast.LENGTH_LONG);
			toast.show();
		}
		
	}
	
	private class updateMessageListener implements ATTIAMListener {
		
		@Override
		public void onSuccess(Object response) {
			// TODO Auto-generated method stub
			
			Boolean msg = (Boolean) response;
			if (msg) {
				Toast toast = Toast.makeText(getApplicationContext(),
						"updateMessageListener onSuccess : Message : " + msg, Toast.LENGTH_LONG);
				toast.show();
			}
			
		}

		@Override
		public void onError(Object error) {
			// TODO Auto-generated method stub
			Toast toast = Toast.makeText(getApplicationContext(), "Message : "
					+ "Iam in  updateMessageListener Error Callback", Toast.LENGTH_LONG);
			toast.show();
		}		
		
	}

	
	private class updateMessagesListener implements ATTIAMListener {
		
		@Override
		public void onSuccess(Object response) {
			// TODO Auto-generated method stub
			
			Boolean msg = (Boolean) response;
			if (msg) {
				Toast toast = Toast.makeText(getApplicationContext(),
						"updateMessagesListener onSuccess : Message : " + msg, Toast.LENGTH_LONG);
				toast.show();
			}
			
		}

		@Override
		public void onError(Object error) {
			// TODO Auto-generated method stub
			Toast toast = Toast.makeText(getApplicationContext(), "Message : "
					+ "Iam in  updateMessagesListener Error Callback", Toast.LENGTH_LONG);
			toast.show();
		}		
		
	}

	private class getNotificationConnectionDetailsListener implements ATTIAMListener {
		
		@Override
		public void onSuccess(Object response) {
			// TODO Auto-generated method stub
			
			NotificationConnectionDetails msg = (NotificationConnectionDetails) response;
			if (null != msg) {
				Toast toast = Toast.makeText(getApplicationContext(),
						"getNotificationConnectionDetailsListener onSuccess : Message : " + msg.getUsername(), Toast.LENGTH_LONG);
				toast.show();
			}
			
		}

		@Override
		public void onError(Object error) {
			// TODO Auto-generated method stub
			Toast toast = Toast.makeText(getApplicationContext(), "Message : "
					+ "Iam in  getNotificationConnectionDetailsListener Error Callback", Toast.LENGTH_LONG);
			toast.show();
		}		
		
	}

	
	
	private class getMessageIndexInfoListener implements ATTIAMListener {

		@Override
		public void onSuccess(Object response) {
			// TODO Auto-generated method stub
			
			MessageIndexInfo msg = (MessageIndexInfo) response;
			if (null != msg) {
				Toast toast = Toast.makeText(getApplicationContext(),
						"getMessageIndexInfoListener onSuccess : Message : " + msg.getState(), Toast.LENGTH_LONG);
				toast.show();
			}
			
		}

		@Override
		public void onError(Object error) {
			// TODO Auto-generated method stub
			Toast toast = Toast.makeText(getApplicationContext(), "Message : "
					+ "Iam in  getMessageIndexInfoListener Error Callback", Toast.LENGTH_LONG);
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
						"createMessageIndexListener onSuccess : Message : " + msg, Toast.LENGTH_LONG);
				toast.show();
			}
			
		}

		@Override
		public void onError(Object error) {
			// TODO Auto-generated method stub
			Toast toast = Toast.makeText(getApplicationContext(), "Message : "
					+ "Iam in  createMessageIndexListener Error Callback", Toast.LENGTH_LONG);
			toast.show();
		}		
		
	}

	
	private class deleteMessageListener implements ATTIAMListener {

		@Override
		public void onSuccess(Object response) {
			// TODO Auto-generated method stub
			
			Boolean msg = (Boolean) response;
			if (msg) {
				Toast toast = Toast.makeText(getApplicationContext(),
						"deleteMessageListener onSuccess : Message : " + msg, Toast.LENGTH_LONG);
				toast.show();
			}
			
		}

		@Override
		public void onError(Object error) {
			// TODO Auto-generated method stub
			Toast toast = Toast.makeText(getApplicationContext(), "Message : "
					+ "Iam in  deleteMessageListener Error Callback", Toast.LENGTH_LONG);
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
						"deleteMessagesListener onSuccess : Message : " + msg, Toast.LENGTH_LONG);
				toast.show();
			}
			
		}

		@Override
		public void onError(Object error) {
			// TODO Auto-generated method stub
			Toast toast = Toast.makeText(getApplicationContext(), "Message : "
					+ "Iam in  deleteMessagesListener Error Callback", Toast.LENGTH_LONG);
			toast.show();
		}		
		
	}

	private class getDeltaListener implements ATTIAMListener {

		@Override
		public void onSuccess(Object response) {
			// TODO Auto-generated method stub
			
			DeltaResponse msg = (DeltaResponse) response;
			if (null != msg) {
				Toast toast = Toast.makeText(getApplicationContext(),
						"getDeltaListener onSuccess : Message : " + msg.getState(), Toast.LENGTH_LONG);
				toast.show();
			}
			
		}

		@Override
		public void onError(Object error) {
			// TODO Auto-generated method stub
			Toast toast = Toast.makeText(getApplicationContext(), "Message : "
					+ "Iam in  getDeltaListener Error Callback", Toast.LENGTH_LONG);
			toast.show();
		}		
		
	}
		
	private class getMessageContentListener implements ATTIAMListener {

		@Override
		public void onSuccess(Object response) {
			// TODO Auto-generated method stub
			
			MessageContent msg = (MessageContent) response;
			if (null != msg) {
				Toast toast = Toast.makeText(getApplicationContext(),
						"getMessageContentListener onSuccess : Message : " + msg.getContentType(), Toast.LENGTH_LONG);
				toast.show();
			}
			
		}

		@Override
		public void onError(Object error) {
			// TODO Auto-generated method stub
			Toast toast = Toast.makeText(getApplicationContext(), "Message : "
					+ "Iam in  getMessageContentListener Error Callback", Toast.LENGTH_LONG);
			toast.show();
		}		
		
	}
	
	private class getMessageListListener implements ATTIAMListener {

		@Override
		public void onSuccess(Object response) {
			// TODO Auto-generated method stub
			
			MessageList msg = (MessageList) response;
			if (null != msg) {
				Toast toast = Toast.makeText(getApplicationContext(),
						"getMessageListListener onSuccess : Message : " + msg.getMessages()[0].getText() + ", From : " + msg.getMessages()[0].getFrom(), Toast.LENGTH_LONG);
				toast.show();
				Log.i("getMessageListListener onSuccess " ,": Message : " + msg.getMessages()[0].getText() + ", From : " + msg.getMessages()[0].getFrom());

			}
			
		}

		@Override
		public void onError(Object error) {
			// TODO Auto-generated method stub
			Toast toast = Toast.makeText(getApplicationContext(), "Message : "
					+ "Iam in  getMessageListListener Error Callback", Toast.LENGTH_LONG);
			toast.show();
		}		
		
	}

	private class getMessageListener implements ATTIAMListener {

		@Override
		public void onError(Object arg0) {
			// TODO Auto-generated method stub
			Toast toast = Toast.makeText(getApplicationContext(), "Message : "
					+ "Iam in  getMessageListener Error Callback", Toast.LENGTH_LONG);
			toast.show();
		}

		@Override
		public void onSuccess(Object arg0) {
			// TODO Auto-generated method stub
			Message msg = (Message) arg0;
			if (null != msg) {
				Toast toast = Toast.makeText(getApplicationContext(),
						" getMessageListener onSuccess Message : " + msg.getText(), Toast.LENGTH_LONG);
				toast.show();
			}
		}
	}

	private class sendMessageListener implements ATTIAMListener {

		@Override
		public void onError(Object arg0) {
			// TODO Auto-generated method stub
			Toast toast = Toast.makeText(getApplicationContext(), " Message :  "
					+ "Iam in sendMessageListener Error Callback", Toast.LENGTH_LONG);
			toast.show();
		}

		@Override
		public void onSuccess(Object arg0) {
			// TODO Auto-generated method stub
			SendResponse msg = (SendResponse) arg0;
			if (null != msg) {
				Toast toast = Toast.makeText(getApplicationContext(),
						"sendMessageListener onSuccess : Message : " + msg.getId(), Toast.LENGTH_LONG);
				toast.show();
			}
		}
	}

	@Override
	public void onSuccess(Object adViewResponse) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onError(Object error) {
		// TODO Auto-generated method stub
		
	}
}
