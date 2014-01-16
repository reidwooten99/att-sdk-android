package com.example.iamtestapp;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.att.api.immn.listener.ATTIAMListener;
import com.att.api.immn.service.IAMManager;
import com.att.api.immn.service.IMMNService;
import com.att.api.immn.service.Message;
import com.att.api.immn.service.SendResponse;
import com.att.api.oauth.OAuthService;
import com.att.api.oauth.OAuthToken;
import com.example.mavensampleapp.R;

public class MainActivity extends Activity {

	IMMNService immnSrvc;
	IAMManager iamManager;
	TextView value;
	TextView messageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Use the app settings from developer.att.com for the following
		// values. Make sure IMMN is enabled for the app key/secret.
		final String fqdn = "https://api-stage.mars.bf.sl.attcompute.com";

		// Enter the value from 'App Key' field
		final String clientId = "ENTER VALUE!";

		// Enter the value from 'Secret' field
		final String clientSecret = "ENTER VALUE!";

		// Create service for requesting an OAuth token
		OAuthService osrvc = new OAuthService(fqdn, clientId, clientSecret);

		// Get the OAuth code by opening a browser to the following URL:
		// https://api.att.com/oauth/authorize?client_id=CLIENT_ID&scope=SCOPE&redirect_uri=REDIRECT_URI
		// replacing CLIENT_ID, SCOPE, and REDIRECT_URI with the values
		// configured at
		// developer.att.com. After authenticating, copy the oauth code from the
		// browser URL.
		final String oauthCode = "ENTER VALUE!";

		// Get OAuth token using the code
		// OAuthToken token = osrvc.getTokenUsingCode(oauthCode);
		OAuthToken token = new OAuthToken("bY02hSSp3BCxD9dGqi0W38NS7F0WDXZY",
				0, null);

		// SendMessage Call from SampleApp
		iamManager = new IAMManager(fqdn, token, new sendMessageListener());
		iamManager.SendMessage("4257492983",
				"This is an example message for Android App");

		// GetMessage Call from SampleApp
		iamManager = new IAMManager(fqdn, token, new getMessageListener());
		iamManager.GetMessage("SVC0001");

	}

	private class getMessageListener implements ATTIAMListener {

		@Override
		public void onError(Object arg0) {
			// TODO Auto-generated method stub
			Toast toast = Toast.makeText(getApplicationContext(), "Message : "
					+ "Iam in Error Callback", Toast.LENGTH_LONG);
			toast.show();
		}

		@Override
		public void onSuccess(Object arg0) {
			// TODO Auto-generated method stub
			Message msg = (Message) arg0;
			if (null != msg) {
				Toast toast = Toast.makeText(getApplicationContext(),
						"Message : " + msg.getText(), Toast.LENGTH_LONG);
				toast.show();
			}
		}
	}

	private class sendMessageListener implements ATTIAMListener {

		@Override
		public void onError(Object arg0) {
			// TODO Auto-generated method stub
			Toast toast = Toast.makeText(getApplicationContext(), "Message : "
					+ "Iam in Error Callback", Toast.LENGTH_LONG);
			toast.show();
		}

		@Override
		public void onSuccess(Object arg0) {
			// TODO Auto-generated method stub
			SendResponse msg = (SendResponse) arg0;
			if (null != msg) {
				Toast toast = Toast.makeText(getApplicationContext(),
						"SendMessage onSuccess : Message : " + msg.getId(), Toast.LENGTH_LONG);
				toast.show();
			}
		}
	}
}
