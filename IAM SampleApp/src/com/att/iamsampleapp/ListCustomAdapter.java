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

	//public ListCustomAdapter(Context context, MessageList results) {
	public ListCustomAdapter(Context context, Message[] results) {
		//messageList = results;
		messageList = results;
		mInflater = LayoutInflater.from(context);
	}

	public int getCount() {
		//return messageList.getLimit();
		return messageList.length;
	}

	public Object getItem(int position) {
		//return messageList.getMessages()[position];
		return messageList[position];
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		
		//if (convertView == null && messageList.getTotal() < position) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.custom_row_view, null);
			holder = new ViewHolder();
			
			holder.txtName = (TextView) convertView.findViewById(R.id.name);
			//holder.txtName.setText(messageList.getMessages()[position].getFrom());
			holder.txtName.setText(messageList[position].getFrom());
			
			holder.txtMessage = (TextView) convertView
					.findViewById(R.id.message);
			//holder.txtMessage.setText(messageList.getMessages()[position].getText());
			holder.txtMessage.setText(messageList[position].getText());
			
			holder.txtTime = (TextView) convertView.findViewById(R.id.time);
			//holder.txtTime.setText(messageList.getMessages()[position].getTimeStamp());
			holder.txtTime.setText(messageList[position].getTimeStamp());

			convertView.setTag(holder);
		} else {
			
			holder = (ViewHolder) convertView.getTag();
		}

		return convertView;
	}

	static class ViewHolder {
		TextView txtName;
		TextView txtMessage;
		TextView txtTime;
	}
}
