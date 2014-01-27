package com.att.iamsampleapp;

import com.att.api.immn.listener.ATTIAMListener;
import com.att.api.immn.service.IAMManager;
import com.att.api.immn.service.SendResponse;
import com.att.api.oauth.OAuthToken;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class NewMessage extends Activity {

	private static final int PICK_CONTACT_REQUEST = 0;
	private static final String TAG = "IAM_NewMessage";
	final String fqdn = "https://api-stage.mars.bf.sl.attcompute.com";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_message);
		// pickContact();
	}

	@SuppressWarnings("unused")
	private void pickContact() {
		Intent pickContactIntent = new Intent(Intent.ACTION_PICK,
				ContactsContract.Contacts.CONTENT_URI);
		pickContactIntent
				.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
		startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST);
	}

	public void toastHere(String message) {
		Toast toast = Toast.makeText(getApplicationContext(), "Message : "
				+ message, Toast.LENGTH_SHORT);
		toast.show();
	}

	protected void onActivityResult(int requestCode, int resultCode,
			Intent intentContact) {
		super.onActivityResult(requestCode, resultCode, intentContact);
		if (requestCode == PICK_CONTACT_REQUEST) {

			if (resultCode == RESULT_OK) {
				Uri pickedContact = intentContact.getData();
				// handle the picked phone number in here.
				toastHere(pickedContact.toString());
				retrieveContactName(pickedContact);
				retrieveContactNumber(pickedContact);
			}
		}
	}

	private void retrieveContactNumber(Uri uriContact) {

		String contactNumber = null;

		// getting contacts ID
		Cursor cursorID = getContentResolver().query(uriContact,
				new String[] { ContactsContract.Contacts._ID }, null, null,
				null);

		String contactID = null;
		if (cursorID.moveToFirst()) {

			contactID = cursorID.getString(cursorID
					.getColumnIndex(ContactsContract.Contacts._ID));
		}

		cursorID.close();

		Log.d(TAG, "Contact ID: " + contactID);

		// Using the contact ID now we will get contact phone number
		Cursor cursorPhone = getContentResolver().query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
				new String[] { ContactsContract.CommonDataKinds.Phone.NUMBER },

				ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND "
						+ ContactsContract.CommonDataKinds.Phone.TYPE + " = "
						+ ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,

				new String[] { contactID }, null);

		if (cursorPhone.moveToFirst()) {
			contactNumber = cursorPhone
					.getString(cursorPhone
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
		}

		cursorPhone.close();

		toastHere(contactNumber);
		Log.d(TAG, "Contact Phone Number: " + contactNumber);
	}

	private void retrieveContactName(Uri uriContact) {

		String contactName = null;

		// querying contact data store
		Cursor cursor = getContentResolver().query(uriContact, null, null,
				null, null);

		if (cursor.moveToFirst()) {

			int idx = cursor.getColumnIndex(ContactsContract.Contacts._ID);
			// id = cursor.getString(idx);

			idx = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
			contactName = cursor.getString(idx);

			toastHere(contactName);
		}

		cursor.close();
	}

	@SuppressWarnings("unused")
	private void getDelta() {

	}

	public void sendMessage(View v) {

		EditText contactsWidget = (EditText) findViewById(R.id.contacts);
		EditText messageWidget = (EditText) findViewById(R.id.message);

		if (contactsWidget.getText().toString().equalsIgnoreCase("")) {
			infoDialog("Enter the contacts !!", false);
			return;
		}
		if ("" == messageWidget.getText().toString()) {
			infoDialog("No Message Content !!", false);
			return;
		}

		OAuthToken token = new OAuthToken("bY02hSSp3BCxD9dGqi0W38NS7F0WDXZY",
				0, null);
		IAMManager iamManager = new IAMManager(fqdn, token,
				new sendMessageListener());
		iamManager.SendMessage(contactsWidget.getText().toString(),
				messageWidget.getText().toString());

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

		Intent newMessageIntent = this.getIntent();
		newMessageIntent.putExtra("MessageResponse", responseID);
		this.setResult(RESULT_OK, newMessageIntent);
		finish();
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
