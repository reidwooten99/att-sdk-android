package com.att.iamsampleapp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.att.api.immn.listener.ATTIAMListener;
import com.att.api.immn.service.IAMManager;
import com.att.api.immn.service.MessageContent;
import com.att.api.oauth.OAuthToken;

public class MMSContent extends Activity {

	private static final String TAG = "MMS Attachment Activity";
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
		// mmsType = (String[]) ext.get("MMSType");
		token = new OAuthToken(Config.token, OAuthToken.NO_EXPIRATION,
				Config.refreshToken);

		String attachments = "";
		for (int n = 0; n < mmsContentName.length; n++) {
			if (n != 0 && !(mmsContentName[n - 1].equalsIgnoreCase("smil.xml")))
				attachments = attachments.concat(", ");
			if (!(mmsContentName[n].equalsIgnoreCase("smil.xml")))
				attachments = attachments.concat(mmsContentName[n]);

			iamManager = new IAMManager(Config.fqdn, token,
					new getMessageContentListener());
			String mmsContentDetails[] = mmsContentUrl[n].split("/");
			iamManager.GetMessageContent(
					mmsContentDetails[mmsContentDetails.length - 3],
					mmsContentDetails[mmsContentDetails.length - 1]);
		}
		TextView attachmentFiles = (TextView) findViewById(R.id.attachmentsFileName);
		attachmentFiles.setText(attachments.concat("."));
	}

	private class getMessageContentListener implements ATTIAMListener {

		@Override
		public void onSuccess(Object response) {
			// TODO Auto-generated method stub

			MessageContent msg = (MessageContent) response;
			String binaryData = msg.getContent();
			if (msg.getContentType().contains("TEXT/PLAIN")) {
				TextView txt = (TextView) findViewById(R.id.mmsmessage);
				txt.setText(binaryData);
			} else if (msg.getContentType().contains("IMAGE/")) {

				byte[] decodedString = Base64
						.decode(binaryData, Base64.URL_SAFE);
				Bitmap decodedByte = BitmapFactory.decodeByteArray(
						decodedString, 0, decodedString.length);
				ImageView image = (ImageView) findViewById(R.id.mmsImageAttachment);
				image.setImageBitmap(decodedByte);
			} else {
				Log.d(TAG, "MMS Attachment : " + binaryData);
			}

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
	
public void writeLog(byte[] text, String fileName) {
		
    	if (!Environment.MEDIA_MOUNTED.equals(Environment
    			.getExternalStorageState()))
    	return;

    	File sdCard = Environment.getExternalStorageDirectory();
    	File logFile = new File(sdCard.getAbsolutePath() + File.separator + fileName);

    	// delete the old file
    	if (logFile.exists()) {
    		logFile.delete();
    	}

    	// create a new shiny file
    	try {
    		logFile.createNewFile();
    	} catch (IOException e) {
    		new AlertDialog.Builder(this).setMessage(
    				"Unable to create log email file: " + e.getMessage())
    				.show();
    	}

    	FileOutputStream outFileStream = null;
    	try {
    		outFileStream = new FileOutputStream(logFile);
    	} catch (FileNotFoundException e) {
    		return;
    	}

    	try {
			outFileStream.write(text);
			outFileStream.close();
    	} catch (IOException e) {
	    	new AlertDialog.Builder(this).setMessage(
	    	"Unable to create log email file: " + e.getMessage())
	    	.show();
    	}
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.mmscontent, menu);
		return true;
	}

}
