package com.att.sdk.listener;

import com.att.api.error.AttSdkError;

public interface AttSdkListener {

	public void onSuccess(Object adViewResponse);
	
	public void onError(AttSdkError error);
}