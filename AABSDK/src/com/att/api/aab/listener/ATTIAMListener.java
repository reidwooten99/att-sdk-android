package com.att.api.aab.listener;

import com.att.api.error.InAppMessagingError;

public interface ATTIAMListener {

	public void onSuccess(Object adViewResponse);
	
	public void onError(InAppMessagingError error);
}