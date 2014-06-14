package com.att.aabsampleapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.att.api.aab.service.Contact;
import com.att.api.aab.service.QuickContact;

public class ContactsAdapter extends BaseAdapter {
	
	//private static QuickContact[] quickContacts;
	private static Contact[] quickContacts;
	//private Context ctx;
	private LayoutInflater cInflater;
	
	
	public ContactsAdapter(Context context, Contact[] contactsList) {
		quickContacts = contactsList;
		//this.ctx = context;
		cInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return quickContacts.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return quickContacts[position];
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		ViewHolder holder;
		
		if(convertView == null) {
			convertView = cInflater.inflate(R.layout.activity_contact_list_row, null);
			holder = new ViewHolder();
			holder.txtFormattedName = (TextView)convertView.findViewById(R.id.formattedname);
			holder.txtContactId = (TextView)convertView.findViewById(R.id.contactId);
			holder.txtZipcode = (TextView)convertView.findViewById(R.id.ZipCode);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		// holder.txtFormattedName.setText(quickContacts[position].getFormattedName());
		 holder.txtFormattedName.setText(quickContacts[position].getFirstName());
		 holder.txtContactId.setText(quickContacts[position].getContactId());
		// holder.txtZipcode.setText(quickContacts[position].getEmail().getEmailAddress().toString());
		
		return convertView;
	}
	
	static class ViewHolder {	
		TextView txtFormattedName;
		TextView txtContactId;
		TextView txtZipcode;
	}

}
