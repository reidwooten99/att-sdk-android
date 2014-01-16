package com.att.api.immn.service;

import com.att.api.immn.listener.ATTIAMListener;
import com.att.api.oauth.OAuthToken;

/**
 * @author dg185p
 * @author ps350r
 * 
 */
public class IAMManager {

	public static IMMNService immnSrvc;
	private ATTIAMListener iamListener;
	
	/**
	 * Creates an IAMManager object.
	 * @param fqdn fully qualified domain name to use for sending requests
	 * @param token OAuth token to use for authorization
	 * @param iamListener listener for callback
	 */
	public IAMManager(String fqdn, OAuthToken token, ATTIAMListener iamListener) {
		
		immnSrvc = new IMMNService(fqdn, token);
		this.iamListener = iamListener;
	}

	/**
	 * @param msgId
	 */
	public void GetMessage(String msgId) {
		APIGetMessage getMessageViewObj = new APIGetMessage(msgId, immnSrvc, iamListener);
		getMessageViewObj.GetMessage(msgId);
	}
	
	/**
	 * @param address
	 * @param message
	 */
	public void SendMessage(String address, String message) {
		APISendMessage sendMessage = new APISendMessage(address, message, immnSrvc, iamListener);
		sendMessage.SendMessage();
	}
	/**
	 * @param msgId
	 * @param partNumber
	 */
	public void GetMessageContent(String msgId, String partNumber) {
		APIGetMessageContent getMessageContent = new APIGetMessageContent(msgId, partNumber, immnSrvc, iamListener);
		getMessageContent.GetMessageContent();	
	}
	/**
	 * @param limit
	 * @param offset
	 */
	public void GetMessageList(int limit, int offset) {
		APIGetMessageList getMessageList = new APIGetMessageList(limit, offset, immnSrvc, iamListener);
		getMessageList.GetMessageList();
	}
	/**
	 * @param state
	 */
	public void GetDelta(String state) {
		APIGetDelta getDelta = new APIGetDelta(state, immnSrvc,iamListener);
		getDelta.GetDelta();
	}
	/**
	 * 
	 */
	public void GetMessageIndexInfo() {
		APIGetMessageIndexInfo getMessageIndexInfo = new APIGetMessageIndexInfo(immnSrvc, iamListener);
		getMessageIndexInfo.GetMessageIndexInfo();
	}
		
	
}
