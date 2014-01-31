package com.att.api.immn.service;

import com.att.api.immn.listener.ATTIAMListener;
import com.att.api.immn.service.APIUpdateMessage.APIUpdateMessageParams;
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
	 * The Message with the given identifier is retrieved
	 * @param msgId - A message identifier representing a Subscriber Message in the AT&T Messages environment.
	 */
	public void GetMessage(String msgId) {
		APIGetMessage getMessage = new APIGetMessage(msgId, immnSrvc, iamListener);
		getMessage.GetMessage(msgId);
	}
	
	/**
	 * @param address - Addresses can be in the following forms and at least one of them must be provided: 
	 * 1) MSISDN – It is the mobile number based on North American Numbering Plan with max length of 11 digits. 
	 * It must be preceded by ‘tel:’ scheme. 
	 * Valid representation formats are: a) tel:+12012345678 b) tel:12012345678 c) tel:2012345678
	 * International numbers shall not be supported.
	 * 2) Short code – It is a special number between 3-8 digits long. 
	 * It must be preceded by ‘short:’ scheme. Example of valid values are: a) short:123 b) short:12345678
	 * 3) Email address – Standard email address format validation must be performed.
	 * Max 10 Addresses will be supported. However, this limit will be configurable at a System level.
	 * If any of the addresses is duplicated, the request will be sent only ONCE.
	 * 
	 * @param message - The Message to be sent.
	 * 1) If the request is detected to be MMS then the following character sets will be supported :
	 * 		a) ASCII  b) UTF-8  c) UTF-16 d) ISO-8859-1
	 * 2) If the request is detected to be SMS then the following character set will be supported:
	 * 		a) ISO-8859-1
	 * It becomes a mandatory field if Attachment(s) is NOT provided in the request.
	 * 
	 * */
	/*public void SendMessage(String address, String message) {
		APISendMessage sendMessage = new APISendMessage(address, message, immnSrvc, iamListener);
		sendMessage.SendMessage();
	}
*/	public void SendMessage(String[] addresses, String message, String subject, boolean group, String[] attachments) {
		APISendMessage sendMessage = new APISendMessage(addresses, message, subject, group, attachments, 
														immnSrvc, iamListener);
		sendMessage.SendMessage();
	}

	
	/**
	 * The Application will request message content from the AT&T Systems by providing a content 
	 * identifier and the associated message identifier.
	 * The content associated with the identifier provided in the request is returned.
	 * 
	 * @param msgId - A message identifier representing a Subscriber Message in the AT&T Messages environment.
	 * @param partNumber - A content identifier representing an attachment in the referenced subscriber message.
	 */
	public void GetMessageContent(String msgId, String partNumber) {
		APIGetMessageContent getMessageContent = new APIGetMessageContent(msgId, partNumber, immnSrvc, iamListener);
		getMessageContent.GetMessageContent();	
	}
	
	/**
	 * The Application will request a block of messages by providing count and an offset value.
	 * A list of messages is returned in received order starting with the most recent.
	 * 
	 * @param limit - This parameter defines the upper limit of the number of returned messages.
	 *  A maximum value of 500 is supported.
	 * @param offset - This parameter defines the offset from the beginning of the ordered set of messages.
	 */
	public void GetMessageList(int limit, int offset) {
		APIGetMessageList getMessageList = new APIGetMessageList(limit, offset, immnSrvc, iamListener);
		getMessageList.GetMessageList();
	}
	
	/**
	 * This provides capability to check for updates by passing in a client state.
	 *  
	 * @param state - This string is from a either the Get Message Index request, or from the Get Message List request.
	 * 
	 */
	public void GetDelta(String state) {
		APIGetDelta getDelta = new APIGetDelta(state, immnSrvc,iamListener);
		getDelta.GetDelta();
	}
	
	/**
	 * This gets the state, status and message count of the index cache for the subscriber’s inbox.
	 */
	public void GetMessageIndexInfo() {
		APIGetMessageIndexInfo getMessageIndexInfo = new APIGetMessageIndexInfo(immnSrvc, iamListener);
		getMessageIndexInfo.GetMessageIndexInfo();
	}
	
	/**
	 * This  provides capability to retrieve details about the credentials, 
	 * endpoint and resource information to setup a notification connection. 
	 * 
	 * @param queues - The name of the resource the client is interested in subscribing for notifications. 
	 * Currently supported resource is -
	 * 1)TEXT – Subscription to this resource will provide notification related to messages stored as TEXT in the cloud inbox.
	 * 2)MMS – Subscription to this resource will provide notification related to messages stored as MMS in the cloud inbox.
	 */
	public  void GetNotificationConnectionDetails(String queues) {
		APIGetNotificationConnectionDetails getNotificationConnectionDetails = 
						new APIGetNotificationConnectionDetails(queues,immnSrvc,iamListener); 
		getNotificationConnectionDetails.GetNotificationConnectionDetails();
	}
	
	/**
	 * This operation allows creating an index cache for the subscriber’s inbox.
	 * The developer will need to initiate a Create Message Index operation before any of the other operations are used. 
	 * In addition, if a message index is inactive for 30 or more days,
	 * then the developer will need to execute the Create Message Index operation again.
	 */
	public void CreateMessageIndex() {		
		APICreateMessageIndex createMessageIndex = new APICreateMessageIndex(immnSrvc, iamListener);
		createMessageIndex.CreateMessageIndex();	
	}
	
	/**
	 * This operation gives the the ability to delete a specific message in an inbox.
	 * @param msgId - Id of the message that is intended to get deleted
	 */
	public void DeleteMessage(String msgId) {		
		APIDeleteMessage deleteMessage = new APIDeleteMessage(msgId, immnSrvc, iamListener);
		deleteMessage.DeleteMessage();		
	}
	/**
	 * This operation gives the ability to delete messages in an inbox. 
	 * The messageIds are passed in the query string in the request.
	 * 
	 * @param msgIds - Comma delimited message ids.
	 */
	public void DeleteMessages(String[] msgIds) {
		APIDeleteMessages deleteMessages = new APIDeleteMessages(msgIds, immnSrvc, iamListener);
		deleteMessages.DeleteMessages();
	}
	
	/**
	 * This  allows to update the flags that are associated with a collection of messages. 
	 * Any number of messages can  be passed in.
	 * 
	 * @param messages - Container for the messages and the flags that need updating
	 */
	public void UpdateMessages(DeltaChange[] messages) {
		APIUpdateMessages updateMessages = new APIUpdateMessages(messages, immnSrvc, iamListener);
		updateMessages.UpdateMessages();
	}
	/**
	 * This  allows to update the flags that are associated with a specific message. 
	 * The developer passes in the messageId.
	 * @param msgId - Id of the message that is intended to get updated
	 * @param isUnread - optional - This flag provides capability to set a message unread or read status
	 * @param isFavorite - optional - Sets the message to favorite or to unset the favorite.
	 */
	public void UpdateMessage(String msgId, Boolean isUnread, Boolean isFavorite) {
		APIUpdateMessage updateMessage = new APIUpdateMessage();
		APIUpdateMessage.APIUpdateMessageParams params = 
					updateMessage.new APIUpdateMessageParams(msgId, isUnread, isFavorite );

		updateMessage.set(params, immnSrvc, iamListener);
		updateMessage.UpdateMessage();
	}
}
