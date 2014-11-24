package com.att.aabsampleapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.att.api.aab.manager.AabManager;
import com.att.api.aab.service.Address;
import com.att.api.aab.service.Contact;
import com.att.api.aab.service.ContactWrapper;
import com.att.api.aab.service.Email;
import com.att.api.aab.service.Phone;
import com.att.api.error.AttSdkError;
import com.att.sdk.listener.AttSdkListener;

@SuppressWarnings("unused")
public class ContactDetails extends Activity {

	private AabManager aabManager;
	private String contactId;
	private EditText editFirstName;
	private String selectedContactId;
	private EditText editLastName;
	private EditText editPhone1;
	private EditText editEmailAddress;
	private EditText editAddress;
	private EditText editAddress2;
	private EditText editCity;
	private EditText editState;
	private EditText editZipCode;
	private EditText editCountry;
	private Button btnCreateContact;
	private Button btnUpdateContactInfo;

	public static Contact currentContact; // Contact object used to display and
											// update contact.
	// public static Contact newContact; //Contact object used to create new
	// contact.
	// public static Contact updateContact;
	private ContactWrapper contactWrapper;
	private boolean isUpdateMyInfo;
	private boolean isUpdateContact;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact_details);
		Button btnShowGroups;

		editFirstName = (EditText) findViewById(R.id.editfirstName);
		editLastName = (EditText) findViewById(R.id.editlastName);
		editPhone1 = (EditText) findViewById(R.id.editPhoneType1);
		editEmailAddress = (EditText) findViewById(R.id.editEmailAdddress);
		editAddress = (EditText) findViewById(R.id.editAddress);
		editAddress2 = (EditText) findViewById(R.id.editAddress2);
		editCity = (EditText) findViewById(R.id.editCity);
		editState = (EditText) findViewById(R.id.editState);
		editZipCode = (EditText) findViewById(R.id.editzipCode);
		editCountry = (EditText) findViewById(R.id.editCountry);
		btnShowGroups = (Button) findViewById(R.id.btnshowgroups);
		btnCreateContact = (Button) findViewById(R.id.createContact);
		btnUpdateContactInfo = (Button) findViewById(R.id.update);

		Intent intent = getIntent();
		contactId = intent.getExtras().getString("contactId");
		isUpdateMyInfo = intent.getBooleanExtra("isUpdateMyInfo", false);

		if (contactId.equalsIgnoreCase("MY_INFO")) {

			if (isUpdateMyInfo) {
				btnCreateContact.setEnabled(false);
				aabManager = new AabManager(new getMyInfoListener());
				aabManager.GetMyInfo();
				btnUpdateContactInfo.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						aabManager = new AabManager(new updateMyInfoListener());
						aabManager
								.UpdateMyInfo(getUpdatedContactFromContactDetails());
					}

				});

			} else {
				editFirstName.setEnabled(false);
				editFirstName.setTextColor(Color.BLACK);
				editLastName.setEnabled(false);
				editLastName.setTextColor(Color.BLACK);
				editPhone1.setEnabled(false);
				editPhone1.setTextColor(Color.BLACK);
				editEmailAddress.setEnabled(false);
				editEmailAddress.setTextColor(Color.BLACK);
				editAddress.setEnabled(false);
				editAddress.setTextColor(Color.BLACK);
				editAddress2.setEnabled(false);
				editAddress2.setTextColor(Color.BLACK);
				editCity.setEnabled(false);
				editCity.setTextColor(Color.BLACK);
				editState.setEnabled(false);
				editState.setTextColor(Color.BLACK);
				editZipCode.setEnabled(false);
				editZipCode.setTextColor(Color.BLACK);
				editCountry.setEnabled(false);
				editCountry.setTextColor(Color.BLACK);
				btnCreateContact.setEnabled(false);
				btnUpdateContactInfo.setEnabled(false);

				aabManager = new AabManager(new getMyInfoListener());
				aabManager.GetMyInfo();
			}

		} else {
			btnCreateContact.setEnabled(false);
			aabManager = new AabManager(new getContactListener());
			aabManager.GetContact(contactId, " ");
			btnUpdateContactInfo.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					aabManager = new AabManager(new updateContactListener());
					aabManager
							.UpdateContact(getUpdatedContactFromContactDetails());
				}
			});
		}

		btnShowGroups.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(ContactDetails.this,
						ContactGroupList.class);
				i.putExtra("contactId", contactId);
				startActivity(i);
			}
		});

		if (contactId.equalsIgnoreCase("NEW_CONTACT")) {
			btnCreateContact.setEnabled(true);
			btnUpdateContactInfo.setEnabled(false);

		}

		btnCreateContact.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				aabManager = new AabManager(new createContactListener());
				aabManager.CreateContact(createContactFromContactDetails());
			}

		});

	}

	/*
	 * @Override public boolean onCreateOptionsMenu(Menu menu) {
	 * getMenuInflater().inflate(R.menu.contact_details, menu); return true; }
	 */

	private class getMyInfoListener extends AttSdkSampleListener {

		public getMyInfoListener() {
			super("getMyInfoAPI");
		}

		private String strText;

		@Override
		public void onSuccess(Object response) {
			ContactDetails.currentContact = (Contact) response;
			Contact c = ContactDetails.currentContact;
			if (null != c) {
				strText = "\n" + c.getContactId() + ", " + c.getFormattedName();
				Log.i("getMyInfoAPI", "OnSuccess : ContactID :  " + strText);

				editFirstName.setText(c.getFirstName());
				editLastName.setText(c.getLastName());
				if (c.getPhones() != null) {
					editPhone1.setText(c.getPhones()[0].getNumber());
				}
				if (c.getEmails() != null) {
					editEmailAddress
							.setText(c.getEmails()[0].getEmailAddress());
				}

				if (c.getAddresses() != null) {
					editAddress.setText(c.getAddresses()[0].getAddrLineOne());
					editAddress2.setText(c.getAddresses()[0].getAddrLineTwo());
					editCity.setText(c.getAddresses()[0].getCity());
					editState.setText(c.getAddresses()[0].getState());
					editZipCode.setText(c.getAddresses()[0].getZipcode());
					editCountry.setText(c.getAddresses()[0].getCountry());
				}

				selectedContactId = c.getContactId();
			}
			return;
		}

		@Override
		public void onError(AttSdkError error) {
			super.onError(error);
		}		
	}

	private class getContactListener extends AttSdkSampleListener {

		public getContactListener() {
			super("getContactAPI");
		}

		private String strText;

		@Override
		public void onSuccess(Object response) {
			contactWrapper = (ContactWrapper) response;
			if (null != contactWrapper) {
				ContactDetails.currentContact = contactWrapper.getContact();
				Contact c = ContactDetails.currentContact;
				if (null != c) {
					strText = "\n" + c.getContactId() + ", "
							+ c.getFormattedName();
					Log.i("getContactAPI", "OnSuccess : ContactID :  "
							+ strText);

					createContactDetailsFromContact(c);
				}
			}
		}

		@Override
		public void onError(AttSdkError error) {
			super.onError(error);
		}		
	}

	public void createContactDetailsFromContact(Contact contact) {
		editFirstName.setText(contact.getFirstName());
		editLastName.setText(contact.getLastName());

		selectedContactId = contact.getContactId();

		if (contact.getPhones() != null) {

			int numPhoneContacts = contact.getPhones().length;
			if (contact.getPhones()[0] != null) {
				editPhone1.setText(contact.getPhones()[0].getNumber());
			}
		}
		if (contact.getEmails() != null) {
			editEmailAddress.setText(contact.getEmails()[0].getEmailAddress());
		}

		if (contact.getAddresses() != null) {
			editAddress.setText(contact.getAddresses()[0].getAddrLineOne());
			editAddress2.setText(contact.getAddresses()[0].getAddrLineTwo());
			editCity.setText(contact.getAddresses()[0].getCity());
			editState.setText(contact.getAddresses()[0].getState());
			editZipCode.setText(contact.getAddresses()[0].getZipcode());
			editCountry.setText(contact.getAddresses()[0].getCountry());
		}

	}

	public Contact createContactFromContactDetails() {

		Contact.Builder builder = new Contact.Builder();
		builder.setFirstName(editFirstName.getText().toString());
		builder.setLastName(editLastName.getText().toString());

		long time = System.currentTimeMillis();
		builder.setContactId(String.valueOf(time));
		Phone[] phones = new Phone[1];
		phones[0] = new Phone("WORK,CELL", editPhone1.getText().toString(),
				true);
		builder.setPhones(phones);

		Email[] emails = new Email[1];
		emails[0] = new Email("INTERNET,HOME", editEmailAddress.getText()
				.toString(), true);
		builder.setEmails(emails);

		Address.Builder addressBuilder = new Address.Builder();
		addressBuilder.setType("HOME");
		addressBuilder.setPreferred(true);
		addressBuilder.setPoBox("34567");
		addressBuilder.setAddrLineOne(editAddress.getText().toString());
		addressBuilder.setAddrLineTwo(editAddress2.getText().toString());
		addressBuilder.setCity(editCity.getText().toString());
		addressBuilder.setState(editState.getText().toString());
		addressBuilder.setCountry(editCountry.getText().toString());
		addressBuilder.setZipcode(editZipCode.getText().toString());

		Address[] addresses = new Address[1];
		addresses[0] = addressBuilder.build();
		builder.setAddresses(addresses);

		return builder.build();
	}

	public Contact getUpdatedContactFromContactDetails() {
		Contact.Builder builder = new Contact.Builder();
		if (editFirstName.getText().toString() != currentContact.getFirstName()) {
			builder.setFirstName(editFirstName.getText().toString());
		}
		if (editLastName.getText().toString() != currentContact.getLastName()) {
			builder.setLastName(editLastName.getText().toString());
		}

		Phone[] phones = new Phone[1];
		if (currentContact.getPhones() == null
				|| (currentContact.getPhones()[0] != null && editPhone1
						.getText().toString() != currentContact.getPhones()[0]
						.getNumber())) {
			phones[0] = new Phone("WORK,CELL", editPhone1.getText().toString(),
					true);
		}

		builder.setPhones(phones);

		Email[] emails = new Email[1];
		if (currentContact.getEmails() == null
				|| (currentContact.getEmails()[0] != null && editEmailAddress
						.getText().toString() != currentContact.getEmails()[0]
						.getEmailAddress())) {
			emails[0] = new Email("INTERNET,HOME", editEmailAddress.getText()
					.toString(), true);
		}
		builder.setEmails(emails);

		Address[] addresses = new Address[1];
		Address.Builder addressBuilder = new Address.Builder();
		if (currentContact.getAddresses() == null
				|| (currentContact.getAddresses()[0] != null && editAddress
						.getText().toString() != currentContact.getAddresses()[0]
						.getAddrLineOne())) {
			addressBuilder.setAddrLineOne(editAddress.getText().toString());
		}
		if (currentContact.getAddresses() == null
				|| (currentContact.getAddresses()[0] != null && editAddress2
						.getText().toString() != currentContact.getAddresses()[0]
						.getAddrLineTwo())) {
			addressBuilder.setAddrLineTwo(editAddress2.getText().toString());
		}
		if (currentContact.getAddresses() == null
				|| (currentContact.getAddresses()[0] != null && editCity
						.getText().toString() != currentContact.getAddresses()[0]
						.getCity())) {
			addressBuilder.setCity(editCity.getText().toString());
		}
		if (currentContact.getAddresses() == null
				|| (currentContact.getAddresses()[0] != null && editState
						.getText().toString() != currentContact.getAddresses()[0]
						.getState())) {
			addressBuilder.setState(editState.getText().toString());
		}
		if (currentContact.getAddresses() == null
				|| (currentContact.getAddresses()[0] != null && editZipCode
						.getText().toString() != currentContact.getAddresses()[0]
						.getZipcode())) {
			addressBuilder.setZipcode(editZipCode.getText().toString());
		}
		if (currentContact.getAddresses() == null
				|| (currentContact.getAddresses()[0] != null && editCountry
						.getText().toString() != currentContact.getAddresses()[0]
						.getCountry())) {
			addressBuilder.setCountry(editCountry.getText().toString());
		}

		addresses[0] = addressBuilder.build();
		builder.setAddresses(addresses);

		builder.setContactId(selectedContactId);

		return builder.build();

	}

	/*
	 * @Override public boolean onMenuItemSelected(int featureId, MenuItem item)
	 * { // Contact c = getContactFromFields(); switch (item.getItemId()) { case
	 * R.id.action_create:// createContact
	 * 
	 * aabManager = new AabManager(Config.fqdn, Config.authToken, new
	 * createContactListener());
	 * aabManager.CreateContact(createContactFromContactDetails()); break;
	 * 
	 * case R.id.action_update: // UpdateMyInfo or UpdateContact
	 * 
	 * if (isUpdateMyInfo) { // ContactDetails.currentContact = //
	 * getUpdatedContactFromContactDetails()
	 * 
	 * } else { aabManager = new AabManager(Config.fqdn, Config.authToken, new
	 * updateContactListener());
	 * aabManager.UpdateContact(getUpdatedContactFromContactDetails()); } break;
	 * 
	 * } return super.onMenuItemSelected(featureId, item); }
	 */
	public void updateContact(String firstName, String contactId) {
		aabManager = new AabManager(new updateContactListener());

		Contact.Builder builder = new Contact.Builder();
		builder.setFirstName(firstName);
		builder.setContactId(contactId);
		Contact contact = builder.build();
		aabManager.UpdateContact(contact);
	}

	private class createContactListener extends AttSdkSampleListener {

		public createContactListener() {
			super("createContactAPI");
		}

		@Override
		public void onSuccess(Object response) {
			String strText;
			String newContactId;
			String result = (String) response;
			if (null != result) {
				strText = "\n" + result;
				String[] locationUrl = result.split("contacts/");
				newContactId = locationUrl[1];
				Log.i("createContactAPI", "OnSuccess : ContactID :  " + result);
				finish();
			} else {
				strText = "Unknown: " + "test.\nNo data returned.";
			}

		}

		@Override
		public void onError(AttSdkError error) {
			super.onError(error);
		}		
	}

	private class updateContactListener extends AttSdkSampleListener {

		public updateContactListener() {
			super("updateContactAPI");
		}

		@Override
		public void onSuccess(Object response) {
			String result = (String) response;
			if (null != result) {
				Log.i("updateContactAPI", "OnSuccess : ContactID :  " + result);
				// finish();
				Intent intent = new Intent(ContactDetails.this,
						SampleAppLauncher.class);
				startActivity(intent);
			}

		}

		@Override
		public void onError(AttSdkError error) {
			super.onError(error);
		}		
	}

	private class updateMyInfoListener extends AttSdkSampleListener {

		public updateMyInfoListener() {
			super("updateMyInfoAPI");
		}

		@Override
		public void onSuccess(Object response) {
			String result = (String) response;
			if (null != result) {
				Log.i("updateMyInfoAPI", "OnSuccess : ContactID :  " + result);
				// finish();
				Intent intent = new Intent(ContactDetails.this,
						SampleAppLauncher.class);
				startActivity(intent);
			} else {
				result = "Unknown: " + "test.\nNo data returned.";
			}

		}

		@Override
		public void onError(AttSdkError error) {
			super.onError(error);
		}		
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (contactId.equalsIgnoreCase("MY_INFO")) {
			aabManager = new AabManager(new getMyInfoListener());
			aabManager.GetMyInfo();
		}

	}

}
