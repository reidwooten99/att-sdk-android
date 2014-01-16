package com.att.api.immn.listener;

public interface ATTIAMListener {

	public void onSuccess(Object adViewResponse);
	
	public void onError(Object error);
}