package com.att.iamsampleapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.att.api.immn.service.MessageList;

public class ListCustomAdapter extends BaseAdapter {
	private static MessageList messageList;

	private LayoutInflater mInflater;

	public ListCustomAdapter(Context context, MessageList results) {
		messageList = results;
		mInflater = LayoutInflater.from(context);
	}

	public int getCount() {
		return messageList.getLimit();
	}

	public Object getItem(int position) {
		return messageList.getMessages()[position];
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
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

		holder.txtName.setText(messageList.getMessages()[position].getFrom());
		holder.txtMessage.setText(messageList.getMessages()[position].getText());
		holder.txtTime.setText(messageList.getMessages()[position].getTimeStamp());

		return convertView;
	}

	static class ViewHolder {
		TextView txtName;
		TextView txtMessage;
		TextView txtTime;
	}
}
