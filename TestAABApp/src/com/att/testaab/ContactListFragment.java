package com.att.testaab;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.att.api.aab.listener.ATTIAMListener;
import com.att.api.aab.manager.AABManager;
import com.att.api.aab.service.ContactWrapper;
import com.att.api.error.InAppMessagingError;
import com.att.api.oauth.OAuthToken;


public class ContactListFragment extends Fragment {
	
	private String contactId;
	//private AABManager aabManager;
	private ContactWrapper contactWrapper;	
	private EditText editFirstName;
	private EditText editLastName;
	private EditText editOrganization;
	private EditText editPhone1;
	private EditText editEmailAddress;
	private EditText editAddress;
	private EditText editAddress2;
	private EditText editCity;
	private EditText editState;
	private EditText editZipCode;
	private String strText;
	private Button btnGroups;
	private Button btnUpdateContact;
	private Button btnDeleteContact;
	private Button btnSettings;
	

	RelativeLayout rl;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view =  inflater.inflate(R.layout.activity_contact_details, container, false);
		 OAuthToken authToken = new OAuthToken(Config.token, Config.accessTokenExpiry, Config.refreshToken);
		 
		 	editFirstName = (EditText) view.findViewById(R.id.editfirstName);
			editLastName = (EditText) view.findViewById(R.id.editlastName);
			editOrganization = (EditText) view.findViewById(R.id.editorgName);
			editPhone1 = (EditText) view.findViewById(R.id.editPhoneType1);
			editEmailAddress = (EditText) view.findViewById(R.id.editEmailAdddress);
			editAddress = (EditText) view.findViewById(R.id.editAddress);
			editAddress2 = (EditText) view.findViewById(R.id.editAddress2);
			editCity = (EditText) view.findViewById(R.id.editCity);
			editState =(EditText) view.findViewById(R.id.editState);
			editZipCode =(EditText) view.findViewById(R.id.editzipCode);
			
			btnUpdateContact = (Button) view.findViewById(R.id.Update);
			//btnUpdateContact.setOnClickListener(this);
			
			btnDeleteContact =(Button) view.findViewById(R.id.Delete);
			//btnDeleteContact.setOnClickListener(this);
			
			btnGroups = (Button) view.findViewById(R.id.Groups);
			//btnGroups.setOnClickListener(this);
			
			btnSettings = (Button) view.findViewById(R.id.settings);
			//btnSettings.setOnClickListener(this);
			
				
			//int id = Integer.valueOf(contactId);
			
			AABManager aabManager = new AABManager(Config.fqdn, authToken, new getContactListener());
			aabManager.GetContact("0987654432123", "shallow");
			
			return view;

	}
	
	private class getContactListener implements ATTIAMListener {

		@Override
		public void onSuccess(Object response) {
			
			contactWrapper = (ContactWrapper) response;
			if (null != contactWrapper) { 
				strText = null;
				com.att.api.aab.service.QuickContact qc = contactWrapper.getQuickContact();
				if (null != qc) {
					strText = "\n" + qc.getContactId() + ", " + 
								qc.getFormattedName() + ", " + qc.getPhone().getNumber();
					Log.i("getContactsAPI","OnSuccess : ContactID :  " + strText);
					
					editFirstName.setText(qc.getFirstName());
					editLastName.setText(qc.getLastName());
					editOrganization.setText(/*qc.getOrganization()*/"ATT");
					editPhone1.setText(qc.getPhone().getNumber());
					editEmailAddress.setText(qc.getEmail().getEmailAddress());
					editAddress.setText(qc.getAddress().getAddrLineOne());
					editAddress2.setText(qc.getAddress().getAddrLineTwo());
					editCity.setText(qc.getAddress().getCity());
					editState.setText(qc.getAddress().getState());
					editZipCode.setText(qc.getAddress().getZipcode());
				}
				return;
			}
		}

		@Override
		public void onError(InAppMessagingError error) {
			Log.i("getContactAPI on error", "onError");

		}
	}

}
