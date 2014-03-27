package com.att.api.immn.service;

import com.att.api.immn.listener.ATTIAMListener;
import com.att.api.oauth.OAuthToken;
/**
 * This class encapsulates the AT&T RESTful APIs for the In-App Messaging API.
 * 
 * @author dg185p
 * @author ps350r
 * 
 */
public class IAMManager {

	public static IMMNService immnSrvc;
	private ATTIAMListener iamListener;
	
	/**
	 * The IAMManager method creates an IAMManager object.
	 * @param fqdn - Specifies the fully qualified domain name that is used for sending requests.
	 * @param token - Specifies the OAuth token to use for authorization.
	 * @param iamListener - Listener for callback.
	 */
	public IAMManager(String fqdn, OAuthToken token, ATTIAMListener iamListener) {
		
		immnSrvc = new IMMNService(fqdn, token);
		this.iamListener = iamListener;
	}

	/**
	 * The GetMessage method gets the specified message from the background task. 
	 * 
	 * This method returns a response of type Message to the listener.
	 * 
	 * @param msgId - Specifies the message identifier of a subscriber message
	 * in the AT&T Messages environment.
	 * 
	 */
	public void GetMessage(String msgId) {
		APIGetMessage getMessage = new APIGetMessage(msgId, immnSrvc, iamListener);
		getMessage.GetMessage(msgId);
	}
	
	/**
	 * 
	 * The SendMessage method sends an SMS or MMS message to one or more recipients. 
	 * 
	 * This method returns a response of type SendResponse to the listener.
	 * 
	 * @param addresses - Specifies the recipient addresses. At least one address is required. Addresses 
	 * can be in the following formats: 
	 * <ul>
	 * <li> MSISDN: This format is the mobile number based on North American Numbering Plan with a maximum 
	 * length of 11 digits. It must be preceded by the following prefix: tel: 
	 * <li>Valid formats are: 
	 * 		<ul>
	 * 		<li> tel:+12012345678
	 * 		<li> tel:12012345678 
	 * 		<li> tel:2012345678
	 * 		</ul>
	 * International numbers are not be supported.
	 * <li> Short code: This format is a special number between 3-8 digits long. It must be preceded by the 
	 * following prefix: short: 
	 * Valid formats are:  
	 * 		<ul>
	 * 		<li> short:123 
	 * 		<li> short:12345678
	 * 		</ul>
	 * <li> Email address: This format is the standard email address format. Validation of the address must be 
	 * performed. A maximum of 10 addresses is supported. However, this limit can be configurable at a System level.
	 * If any of the addresses are duplicated, the message is sent to that address only once.
	 * 
	 * @param message - Specifies the message to be sent.
	 * <ul>
	 * <li> If the message is an MMS message, the following character sets are supported:
	 * 		<ul>
	 * 		<li> ASCII  
	 * 		<li> UTF-8  
	 * 		<li> UTF-16 
	 * 		<li> ISO-8859-1
	 * 		</ul>
	 * <li> If the message is an SMS message, the following character set is supported:  ISO-8859-1
	 * The message parameter is required if attachments are not provided in the request.
	 * </ul>
	 * 
	 * @param subject - Specifies the header for the message.
	 * @param group - If set to True, implies that there are multiple recipients. If set to False, the message 
	 * is treated as a broadcast message.
	 * @param attachments - Specifies the filename of the media content attached to the message.
	 * */
	
	public void SendMessage(String[] addresses, String message, String subject, boolean group, String[] attachments) {
		APISendMessage sendMessage = new APISendMessage(addresses, message, subject, group, attachments, 
														immnSrvc, iamListener);
		sendMessage.SendMessage();
	}

	
	/**
	 * The GetMessageContent method returns specific content from a message. The app requests 
	 * content from the AT&T Systems by providing the identifier of the message and the idetifier of 
	 * the content to be returned. 
	 * 
	 * This method returns a response of type MessageContent to the listener.
	 * 
	 * @param msgId - Specifies the identifier of the message associated with the content.
	 * @param partNumber - Specifies the identifier of an attachment to the message.
	 */
	public void GetMessageContent(String msgId, String partNumber) {
		APIGetMessageContent getMessageContent = new APIGetMessageContent(msgId, partNumber, immnSrvc, iamListener);
		getMessageContent.GetMessageContent();	
	}
	
	/**
	 * The GetMessageList method gets a block of messages. The app requests a block of messages by an offset 
	 * that indicates where the block starts and the number of messages to be retrieved. The messages are 
	 * returned in the order that they were received, starting with the most recent message.
	 * 
	 * This method returns the response of type MessageList to the listener.
	 * 
	 * @param limit - Specifies the upper limit of the block of messages. A maximum of 500 messages can be 
	 * returned.
	 * @param offset - Specifies the lower limit of the block of messages.
	 */
	public void GetMessageList(int limit, int offset) {
		APIGetMessageList getMessageList = new APIGetMessageList(limit, offset, immnSrvc, iamListener);
		getMessageList.GetMessageList();
	}
	
	/**
	 * The GetDelta method checks for updates by passing in a client state.
	 * 
	 * This method returns a response of type DeltaResponse to the listener.
	 * 
	 * @param state - Specifies the client state. This string is returned by the GetMessageIndex 
	 * or GetMessageList methods.
	 * 
	 */
	public void GetDelta(String state) {
		APIGetDelta getDelta = new APIGetDelta(state, immnSrvc,iamListener);
		getDelta.GetDelta();
	}
	
	/**
	 * The GetMessageIndexInfo method gets the state, status, and message count of the index cache for the 
	 * subscriber's inbox.
	 * 
	 * This method returns a response of the type MessageIndexInfo to the listener.
	 * 
	 */
	public void GetMessageIndexInfo() {
		APIGetMessageIndexInfo getMessageIndexInfo = new APIGetMessageIndexInfo(immnSrvc, iamListener);
		getMessageIndexInfo.GetMessageIndexInfo();
	}
	
	/**
	 * The GetNotificationConnectionDetails method retrieves details about the credentials, 
	 * endpoint, and resource information that is used to set up a notification connection.
	 * 
	 * This method task returns a response of the type NotificationConnectionDetails to the listener.
	 * 
	 * @param queues - The name of the resource the client is interested in subscribing for notifications. 
	 * Currently supported resource is -
	 * 
	 * 1)TEXT : Subscription to this resource will provide notification related to messages stored as TEXT in the cloud inbox.
	 * 2)MMS  : Subscription to this resource will provide notification related to messages stored as MMS in the cloud inbox.
	 */
	public  void GetNotificationConnectionDetails(String queues) {
		APIGetNotificationConnectionDetails getNotificationConnectionDetails = 
						new APIGetNotificationConnectionDetails(queues,immnSrvc,iamListener); 
		getNotificationConnectionDetails.GetNotificationConnectionDetails();
	}
	
	/**
	 * The CreateMessageIndex method creats an index cache for the subscriber's inbox.
	 * This method must be called before any of the other operations is used. 
	 * In addition, if a message index is inactive for 30 or more days, then the developer 
	 * will need to create an index cache again.
	 * 
	 * This method returns True for success or False for failure to the listener.
	 */
	public void CreateMessageIndex() {		
		APICreateMessageIndex createMessageIndex = new APICreateMessageIndex(immnSrvc, iamListener);
		createMessageIndex.CreateMessageIndex();	
	}
	
	/**
	 * The DeleteMessage method deletes a specific message from an inbox.
	 * 
	 * This method returns True for success and False for failure to the listener
	 * 
	 * @param msgId - Specifies the identifier of the message to be deleted.
	 */
	public void DeleteMessage(String msgId) {		
		APIDeleteMessage deleteMessage = new APIDeleteMessage(msgId, immnSrvc, iamListener);
		deleteMessage.DeleteMessage();		
	}
	/**
	 * The DeleteMessages method deletes multiple messages from an inbox. The message Ids are passed 
	 * in the query string in the request.
	 * 
	 * This method returns the response with either true or false for success or failure respectively to the listener
	 * 
	 * @param msgIds - Comma delimited message ids.
	 */
	public void DeleteMessages(String[] msgIds) {
		APIDeleteMessages deleteMessages = new APIDeleteMessages(msgIds, immnSrvc, iamListener);
		deleteMessages.DeleteMessages();
	}
	
	/**
	 * The UpdateMessages method updates the flags of multiple messages messages. Any number of messages 
	 * can be passed in.
	 * 
	 * This method returns True if sucessfull or False if a failure occured to the listener.
	 * 
	 * @param messages - Specifies the messages and the flags to be updated.
	 */
	public void UpdateMessages(DeltaChange[] messages) {
		APIUpdateMessages updateMessages = new APIUpdateMessages(messages, immnSrvc, iamListener);
		updateMessages.UpdateMessages();
	}
	/**
	 * The UpdateMessage method updates the flags of a specific message.
	 * 
	 * This method returns True if sucessfull or False if a failure occured to the listener.
	 * 
	 * @param msgId - Specifies the Id of the message to be updated.
	 * @param isUnread - Indicates whether the message has or has not been read.
	 * @param isFavorite - Indicates whether the message is or is not a favorite.
	 */
	public void UpdateMessage(String msgId, Boolean isUnread, Boolean isFavorite) {
		APIUpdateMessage updateMessage = new APIUpdateMessage();
		APIUpdateMessage.APIUpdateMessageParams params = 
					updateMessage.new APIUpdateMessageParams(msgId, isUnread, isFavorite );

		updateMessage.set(params, immnSrvc, iamListener);
		updateMessage.UpdateMessage();
	}
}
