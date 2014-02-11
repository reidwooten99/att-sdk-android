package com.att.iamsampleapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputFilter.LengthFilter;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.att.api.immn.listener.ATTIAMListener;
import com.att.api.immn.service.IAMManager;
import com.att.api.immn.service.SendResponse;
import com.att.api.oauth.OAuthToken;

public class NewMessage extends Utils {

	private static final String TAG = "IAM_NewMessage";
	final String fqdn = Config.fqdn;
	String attachments[] = new String[10];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_message);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.action_new_message, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		Intent intent = new Intent();
		String attachmentContentType;
		String title;
		int requestCode;
		switch (item.getItemId()) {

		case R.id.action_ImageAttachment: {
			attachmentContentType = "image/*";
			title = "Choose image file";
			requestCode = SELECT_PICTURE;
			break;
		}
		case R.id.action_AudioAttachment: {
			attachmentContentType = "audio/*";
			title = "Choose audio file";
			requestCode = SELECT_AUDIO;
			break;
		}
		case R.id.action_VideoAttachment: {
			attachmentContentType = "video/*";
			title = "Choose video file";
			requestCode = SELECT_VIDEO;
			break;
		}
		default:
			return super.onOptionsItemSelected(item);
		}

		intent.setType(attachmentContentType);
		intent.setAction(Intent.ACTION_GET_CONTENT);
		intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
		startActivityForResult(Intent.createChooser(intent, title), requestCode);

		return true;
	}

	protected void onActivityResult(int requestCode, int resultCode,
			Intent intentContact) {
		super.onActivityResult(requestCode, resultCode, intentContact);
		if (requestCode == SELECT_PICTURE || requestCode == SELECT_AUDIO
				|| requestCode == SELECT_VIDEO) {
			if (resultCode == RESULT_OK) {
				Uri pickedAttachment = intentContact.getData();
				String mime = getContentResolver().getType(pickedAttachment);
				String str = getRealPathFromURI(pickedAttachment);
				attachments[0] = str;
				Utils.toastHere(getApplicationContext(), TAG, "File Path : "
						+ str);
			}
		}
	}

	public void sendMessage(View v) {

		EditText contactsWidget = (EditText) findViewById(R.id.contacts);
		EditText messageWidget = (EditText) findViewById(R.id.message);
		EditText subjectWidget = (EditText) findViewById(R.id.subject);

		if (contactsWidget.getText().toString().equalsIgnoreCase("")) {
			infoDialog("Enter the contacts !!", false);
			return;
		}
		if ("" == messageWidget.getText().toString()) {
			infoDialog("No Message Content !!", false);
			return;
		}

		OAuthToken token = new OAuthToken(Config.token,
				OAuthToken.NO_EXPIRATION, Config.refreshToken);
		IAMManager iamManager = new IAMManager(fqdn, token,
				new sendMessageListener());

		Boolean isGroup = false;
		
		String addresses[] = contactsWidget.getText().toString().split(",");
		if(addresses.length > Config.maxRecipients)
			infoDialog("Maximum recipients is + " + String.valueOf(Config.maxRecipients) + " !!",false);

		/*
		 * iamManager.SendMessage(addresses, messageWidget.getText().toString(),
		 * subjectWidget.getText().toString(), false, attachments);
		 */

		isGroup = (addresses.length > 1) ? true:false; 
		iamManager.SendMessage(addresses, messageWidget.getText().toString(),
				subjectWidget.getText().toString(), isGroup, null);

	}

	protected class sendMessageListener implements ATTIAMListener {

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
						"Message : " + msg.getId(), Toast.LENGTH_LONG);
				toast.show();

				sendMessageResponsetoParentActivity(msg.getId());
			}
		}
	}

	void sendMessageResponsetoParentActivity(String responseID) {

		Intent newMessageIntent = new Intent();//this.getIntent();
		newMessageIntent.putExtra("MessageResponse", responseID);
		setResult(RESULT_OK, newMessageIntent);
		finish();
	}

	public void addAttachment(View v) {

		openImageIntent();
	}

	private static final int SELECT_PICTURE = 1;
	private static final int SELECT_AUDIO = 2;
	private static final int SELECT_VIDEO = 3;

	void openImageIntent() {

		Intent pickIntent = new Intent();
		pickIntent.setType("image/*");
		pickIntent.setAction(Intent.ACTION_GET_CONTENT);

		Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		String pickTitle = "Select or take a new Picture"; // Or get from
															// strings.xml
		Intent chooserIntent = Intent.createChooser(pickIntent, pickTitle);
		chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
				new Intent[] { takePhotoIntent });

		startActivityForResult(chooserIntent, SELECT_PICTURE);
	}

	// Info Dialog
	protected void infoDialog(String message, final boolean doFinish) {
		doDialog("Info: ", message, doFinish);
	}

	private void doDialog(String prefix, String message, final boolean doFinish) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setTitle(prefix);

		// set dialog message
		alertDialogBuilder.setMessage(message).setCancelable(false)
				.setNeutralButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {

						if (doFinish == true)
							// if this button is clicked, Close the Application.
							finish();
						else
							// if this button is clicked, just close the dialog
							// box.
							dialog.cancel();
					}
				});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}
}
