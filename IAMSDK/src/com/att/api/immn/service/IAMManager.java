package com.att.api.immn.service;

import com.att.api.immn.listener.ATTIAMListener;
import com.att.api.oauth.OAuthToken;

public class IAMManager {

	public static IMMNService immnSrvc;
	private ATTIAMListener iamListener;
	
	public IAMManager(String fqdn, OAuthToken token, ATTIAMListener iamListener) {		
		immnSrvc = new IMMNService(fqdn, token);
		this.iamListener = iamListener;
	}

	public void GetMessage(String msgId) {
		APIGetMessage getMessageViewObj = new APIGetMessage(msgId, immnSrvc, iamListener);
		getMessageViewObj.GetMessage(msgId);
	}
	
	public void SendMessage(String address, String message) {
		APISendMessage sendMessage = new APISendMessage(address, message, immnSrvc, iamListener);
		sendMessage.SendMessage();
	}
}
