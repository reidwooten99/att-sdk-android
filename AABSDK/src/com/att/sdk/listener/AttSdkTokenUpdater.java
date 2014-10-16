package com.att.sdk.listener;

import com.att.api.oauth.OAuthToken;

public interface AttSdkTokenUpdater {	
	public void onTokenUpdate(OAuthToken newToken);
	public void onTokenDelete();
}
