package com.att.api.immn.service;

import android.os.AsyncTask;
import android.widget.TextView;

public class IAMManager {
	
	public TextView GetMessage(String msgId) {
		GetMessageViewObject getMessageViewObj = new GetMessageViewObject(msgId) ;
		return getMessageViewObj.GetMessage(msgId);
		
	}
	
}
