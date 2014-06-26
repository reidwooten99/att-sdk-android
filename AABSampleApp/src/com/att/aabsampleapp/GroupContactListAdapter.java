package com.att.aabsampleapp;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.att.api.aab.service.Contact;
import com.att.api.aab.service.QuickContact;

public class GroupContactListAdapter extends BaseAdapter {
	
	private static ArrayList<Contact> groupContacts;
	
	//private Context ctx;
	private LayoutInflater cInflater;
	
	
	public GroupContactListAdapter(Context context, ArrayList<Contact> contactsList) {
		groupContacts = contactsList;
		//this.ctx = context;
		cInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return groupContacts.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return groupContacts.get(position);
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
	
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		 holder.txtFormattedName.setText(groupContacts.get(position).getFirstName());
	
		return convertView;
	}
	
	static class ViewHolder {	
		TextView txtFormattedName;
		TextView txtContactId;
		TextView txtZipcode;
	}


}
