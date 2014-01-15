package com.att.api.immn.service;

import android.os.AsyncTask;
import android.widget.TextView;

public class IAMManager {
	
	public Message GetMessage(String msgId) {
		GetMessageViewObject getMessageViewObj = new GetMessageViewObject(msgId) ;
		return getMessageViewObj.GetMessage(msgId);
		
	}
	
}
