package com.att.iamsampleapp;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.att.api.immn.service.Message;

public class MessageListAdapter extends BaseAdapter {

	private static Message[] messageList;
	private LayoutInflater mInflater;
	private Context ctx;

	public MessageListAdapter(Context context, Message[] results) {
		messageList = results;
		this.ctx = context;
		mInflater = LayoutInflater.from(context);
	}
	
	public Message[] deleteItem(int nIndex){
		
		System.arraycopy(messageList,nIndex+1,messageList,nIndex,messageList.length-1-nIndex);
		return messageList;
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

			convertView = mInflater.inflate(R.layout.conversationlist_row_view, null);
			// convertView = mInflater.inflate(R.layout., null);

			holder = new ViewHolder();
			holder.txtName = (TextView) convertView.findViewById(R.id.name);
			holder.txtMessage = (TextView) convertView
					.findViewById(R.id.message);
			holder.txtTime = (TextView) convertView.findViewById(R.id.time);
			holder.imgFavorite = (ImageButton) convertView
					.findViewById(R.id.favorite);
			holder.imgAttachment = (ImageView) convertView
					.findViewById(R.id.attachment);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// Update message From
		contactName = Utils
				.getContactName(ctx, messageList[position].getFrom());
		if (null == contactName)
			holder.txtName.setText(messageList[position].getFrom());
		else
			holder.txtName.setText(contactName);

		// Update message
		if (null == messageList[position].getText())
			holder.txtMessage.setText("< Empty Message >");
		else if (messageList[position].getText().equalsIgnoreCase(""))
			holder.txtMessage.setText("< Empty Message >");
		else {
			holder.txtMessage.setText(messageList[position].getText());
		}
		// Update message time
		holder.txtTime.setText(messageList[position].getTimeStamp().replace(
				'T', ' '));

		// Update favorite message
		if (messageList[position].isFavorite())
			holder.imgFavorite.setBackgroundResource(R.drawable.btn_favorite);
		else
			holder.imgFavorite
					.setBackgroundResource(R.drawable.btn_notfavorite);

		// Update Attachment
		if (messageList[position].getMmsContents() != null)
			holder.imgAttachment
					.setBackgroundResource(R.drawable.ic_attachment);
		else
			holder.imgAttachment
					.setBackgroundResource(R.drawable.ic_transparent);

		// Message Read
		int typeFace;
		if (messageList[position].isUnread()){
			typeFace = Typeface.BOLD_ITALIC;
			convertView.setBackgroundColor(Utils.UnreadBG);
		}
		else{
			typeFace = Typeface.NORMAL;
			convertView.setBackgroundColor(Utils.ReadBG);
		}
		holder.txtName.setTypeface(null, typeFace);
		holder.txtMessage.setTypeface(null, typeFace);
		holder.txtTime.setTypeface(null, typeFace);

		return convertView;
	}

	static class ViewHolder {
		TextView txtName;
		TextView txtMessage;
		TextView txtTime;
		ImageButton imgFavorite;
		ImageView imgAttachment;
	}
	
	
}
