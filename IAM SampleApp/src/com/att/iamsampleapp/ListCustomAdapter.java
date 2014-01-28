package com.att.iamsampleapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.att.api.immn.service.Message;

public class ListCustomAdapter extends BaseAdapter {
	
	private static Message[] messageList;
	private LayoutInflater mInflater;
	private Context ctx;

	public ListCustomAdapter(Context context, Message[] results) {
		messageList = results;
		this.ctx = context;  
		mInflater = LayoutInflater.from(context);
	}

	public int getCount() {
		return messageList.length;
	}

	public Object getItem(int position) {
		return messageList[position];
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder;
		String contactName;
		
		if (convertView == null) {
			
			convertView = mInflater.inflate(R.layout.custom_row_view, null);
			
			holder = new ViewHolder();
			holder.txtName = (TextView) convertView.findViewById(R.id.name);
			holder.txtMessage = (TextView) convertView
					.findViewById(R.id.message);
			holder.txtTime = (TextView) convertView.findViewById(R.id.time);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		contactName = Utils.getContactName(ctx, messageList[position].getFrom());
		if(null == contactName)
			holder.txtName.setText(messageList[position].getFrom());
		else
			holder.txtName.setText(contactName);
		
		if(null == messageList[position].getText())
			holder.txtMessage.setText("< Empty Message >");
		else if(messageList[position].getText().equalsIgnoreCase(""))
			holder.txtMessage.setText("< Empty Message >");
		else
			holder.txtMessage.setText(messageList[position].getText());

		holder.txtTime.setText(messageList[position].getTimeStamp().replace('T', ' '));
		
		return convertView;
	}

	static class ViewHolder {
		TextView txtName;
		TextView txtMessage;
		TextView txtTime;
	}
}
