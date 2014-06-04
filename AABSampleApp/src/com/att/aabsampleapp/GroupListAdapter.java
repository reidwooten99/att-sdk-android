package com.att.aabsampleapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.att.api.aab.service.Group;

public class GroupListAdapter extends BaseAdapter {
	
	private static Group[] groupList;
    private LayoutInflater giInflater;
    
    public GroupListAdapter(Context context, Group[] groupList_array) {
        groupList = groupList_array;
        giInflater = LayoutInflater.from(context);
    }

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return groupList.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return groupList[position];
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
	            convertView = giInflater.inflate(R.layout.activity_group_list_row, null);
	            holder = new ViewHolder();
	            holder.txtGroupName = (TextView)convertView.findViewById(R.id.grpName);
	            holder.txtGroupType = (TextView)convertView.findViewById(R.id.grpType);
	            convertView.setTag(holder);
	        } else {
	            holder = (ViewHolder) convertView.getTag();
	        }
	        
	         holder.txtGroupName.setText(groupList[position].getGroupName());
	         holder.txtGroupType.setText(groupList[position].getGroupType());
	        
	        return convertView;	        
	}

		static class ViewHolder {   
			TextView txtGroupName;
			TextView txtGroupType;
    }

}
