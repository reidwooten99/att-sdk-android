package com.att.iamsampleapp;

import com.att.api.immn.listener.ATTIAMListener;
import com.att.api.immn.service.IAMManager;
import com.att.api.immn.service.MessageContent;
import com.att.api.oauth.OAuthToken;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MMSContent extends Activity {

	String[] mmsContentName, mmsContentType, mmsContentUrl, mmsType;
	OAuthToken token;
	IAMManager iamManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mmscontent);

		Bundle ext = getIntent().getExtras();
		mmsContentName = (String[]) ext.get("MMSContentName");
		mmsContentType = (String[]) ext.get("MMSContentName");
		mmsContentUrl = (String[]) ext.get("MMSContentUrl");
		//mmsType = (String[]) ext.get("MMSType");
		token = new OAuthToken(Config.token, OAuthToken.NO_EXPIRATION,
				Config.refreshToken);

		String attachments = "";
		for (int n = 0; n < mmsContentName.length; n++) {
			if(n != 0 && !(mmsContentName[n-1].equalsIgnoreCase("smil.xml")))
				attachments = attachments.concat(", ");
			if(!(mmsContentName[n].equalsIgnoreCase("smil.xml")))
				attachments = attachments.concat(mmsContentName[n]);
			
			iamManager = new IAMManager(Config.fqdn, token,
					new getMessageContentListener());
			String mmsContentDetails[] = mmsContentUrl[n].split("/");
			iamManager.GetMessageContent(
					mmsContentDetails[mmsContentDetails.length - 3],
					mmsContentDetails[mmsContentDetails.length - 1]);
		}
		TextView attachmentFiles = (TextView)findViewById(R.id.attachmentsFileName);
		attachmentFiles.setText(attachments.concat("."));
	}

	private class getMessageContentListener implements ATTIAMListener {

		@Override
		public void onSuccess(Object response) {
			// TODO Auto-generated method stub

			MessageContent msg = (MessageContent) response;
			if(msg.getContentType().contains("TEXT/PLAIN")){
				TextView txt = (TextView)findViewById(R.id.mmsmessage);
				String binaryData = msg.getContent();
				txt.setText(msg.getContent());
			}
				
			String binaryData = msg.getContent();
			if (null != msg) {
				Toast toast = Toast.makeText(getApplicationContext(),
						"getMessageContentListener onSuccess : Message : "
								+ msg.getContentType(), Toast.LENGTH_LONG);
				toast.show();
			}
		}

		@Override
		public void onError(Object error) {
			// TODO Auto-generated method stub
			Toast toast = Toast.makeText(getApplicationContext(), "Message : "
					+ "Iam in  getMessageContentListener Error Callback",
					Toast.LENGTH_LONG);
			toast.show();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.mmscontent, menu);
		return true;
	}

}
