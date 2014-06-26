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
	
	private static Contact[] quickContacts;
	private LayoutInflater cInflater;
	
	
	public ContactsAdapter(Context context, Contact[] contactsList) {
		quickContacts = contactsList;
		cInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return quickContacts.length;
	}

	@Override
	public Object getItem(int position) {
		return quickContacts[position];
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder;
		
		if(convertView == null) {
			convertView = cInflater.inflate(R.layout.activity_contact_list_row, null);
			holder = new ViewHolder();
			holder.txtFormattedName = (TextView)convertView.findViewById(R.id.formattedname);
			holder.txtPhoneNumber = (TextView)convertView.findViewById(R.id.PhoneNumber);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		 holder.txtFormattedName.setText(quickContacts[position].getFirstName());
		 if (quickContacts[position].getPhones() != null) {
			 holder.txtPhoneNumber.setText(quickContacts[position].getPhones()[0].getNumber());
		 }
		
		return convertView;
	}
	
	static class ViewHolder {	
		TextView txtFormattedName;
		TextView txtPhoneNumber;
	}

}
