package com.att.api.immn.service;

import com.att.api.immn.listener.ATTIAMListener;
import com.att.api.oauth.OAuthToken;
/**
 * This class encapsulates the AT&T RESTfull APIs for In-App Messaging.
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
	 * @param fqdn - Specifies the fully qualified domain name that is used to send requests.
	 * @param token - Specifies the OAuth token that is used for authorization.
	 * @param iamListener - Specifies the Listener for callbacks.
	 */
	public IAMManager(String fqdn, OAuthToken token, ATTIAMListener iamListener) {
		
		immnSrvc = new IMMNService(fqdn, token);
		this.iamListener = iamListener;
	}

	/**
	 * The getMessages method gets a message based on its message Id. This method returns 
	 * a response of the type Message to the listener.
	 * 
	 * @param msgId - Specifies the message identifier of a subscriber message in the AT&T 
	 * Messages environment.
	 * 
	 */
	public void GetMessage(String msgId) {
		APIGetMessage getMessage = new APIGetMessage(msgId, immnSrvc, iamListener);
		getMessage.GetMessage(msgId);
	}
	
	/**
	 * 
	 *  The SendMesage method sends an MMS or SMS message. This method returns a response of the type 
	 *  SendResponse to the listener.

	 * @param address - Specifies the addresses were the message is sennt. The addresses must be specified 
	 * in one of the following formats (at least one of them must be specified): 
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
	 * If any of the email addresses are duplicated, the message is sent to that address only once.
	 * 
	 * @param message - The message to be sent.
	 * <ul>
	 * <li> If the request is detected to be an MMS message, then the following character sets are supported:
	 * 		<ul>
	 * 		<li> ASCII  
	 * 		<li> UTF-8  
	 * 		<li> UTF-16 
	 * 		<li> ISO-8859-1
	 * 		</ul>
	 * <li> If the request is detected to be an SMS mesage, then the following character set are supported:  ISO-8859-1
	 * The message parameter is required if no attachments are specified.
	 * </ul>
	 * 
	 * @param subject - Specifies the header for the message.
	 * @param group	- If True, indicates the message is sent to multiple recipients. If False, indicates that the message 
	 * is a broadcast mesage.
	 * @param attachments - Specifies the filenames of attachments associated wwith the message.
	 * */
	
	public void SendMessage(String[] addresses, String message, String subject, boolean group, String[] attachments) {
		APISendMessage sendMessage = new APISendMessage(addresses, message, subject, group, attachments, 
														immnSrvc, iamListener);
		sendMessage.SendMessage();
	}

	
	/**
	 * The GetMessageContent method gets a message attachment based on the attachment 
	 * and message identifier. This method returns a response of type MessageContent to the listener.
	 * 
	 * @param msgId - Specifies the identifier of a subscriber message in the AT&T Messages environment.
	 * @param partNumber - Specifies the content identifier of the attachment to be retrieved.
	 */
	public void GetMessageContent(String msgId, String partNumber) {
		APIGetMessageContent getMessageContent = new APIGetMessageContent(msgId, partNumber, immnSrvc, iamListener);
		getMessageContent.GetMessageContent();	
	}
	
	/**
	 * The GetMessageList method gets a block of messages based on an offset value and the number 
	 * of mesages to retrieve. The list of messages is returned in the order that they were recieved, starting 
	 * with the most recent. This method returns a response of the type MessageList to the listener.
	 * 
	 * @param limit - Specifies the number of messages to return. A maximum value of 500 is supported.
	 * @param offset - Specifies the offset from the beginning of the ordered set of messages.
	 */
	public void GetMessageList(int limit, int offset) {
		APIGetMessageList getMessageList = new APIGetMessageList(limit, offset, immnSrvc, iamListener);
		getMessageList.GetMessageList();
	}
	
	/**
	 * The GetDelta method checks to see if the client is in a specific state.
	 * This method returns a response of the type DeltaResponse to the listener.
	 * 
	 * @param state - Specifies the state of the client. This string is returned by either the 
	 * GetMessageIndex or GetMessageList method.
	 * 
	 */
	public void GetDelta(String state) {
		APIGetDelta getDelta = new APIGetDelta(state, immnSrvc,iamListener);
		getDelta.GetDelta();
	}
	
	/**
	 * The GetMessageIndexInfo method gets the state, status, and message count of the index cache for the 
	 * subscriber&#8217;s inbox. This method returns a response of the type MessageIndexInfo to the listener.
	 * 
	 */
	public void GetMessageIndexInfo() {
		APIGetMessageIndexInfo getMessageIndexInfo = new APIGetMessageIndexInfo(immnSrvc, iamListener);
		getMessageIndexInfo.GetMessageIndexInfo();
	}
	
	/**
	 * The GetNotificationDetails method gets details about the credentials, endpoint,
	 * and resource information that can be used to set up a notification connection. 
	 * This method returns a response of the type NotificationConnectionDetails to the listener.
	 * 
	 * @param queues - Specifies the name of the resource whoes notification detains are returned. 
	 * Supported resource include the following:
	 * <ul>
	 * 
	 * <li>TEXT : Subscription to this resource will provide notification related to messages stored as TEXT in the cloud inbox.
	 * <li>MMS  : Subscription to this resource will provide notification related to messages stored as MMS in the cloud inbox.
	 * </ul>
	 */
	public  void GetNotificationConnectionDetails(String queues) {
		APIGetNotificationConnectionDetails getNotificationConnectionDetails = 
						new APIGetNotificationConnectionDetails(queues,immnSrvc,iamListener); 
		getNotificationConnectionDetails.GetNotificationConnectionDetails();
	}
	
	/**
	 * The CreateMessageIndex method creats an index cache for the subscriber&#8217;s inbox.
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
	 * This method returns True for success or False for failure to the listener.
	 * 
	 * @param msgId - Specifes the Id of the message to be deleted.
	 */
	public void DeleteMessage(String msgId) {		
		APIDeleteMessage deleteMessage = new APIDeleteMessage(msgId, immnSrvc, iamListener);
		deleteMessage.DeleteMessage();		
	}
	/**
	 * The DeleteMessages method deletes multiple messages from an inbox. The messagee identifiers are 
	 * passed in the query string in the request. This method returns True for success or False for 
	 * failure to the listener.
	 * 
	 * @param msgIds - Specifies a comma delimited list of message identifiers.
	 */
	public void DeleteMessages(String[] msgIds) {
		APIDeleteMessages deleteMessages = new APIDeleteMessages(msgIds, immnSrvc, iamListener);
		deleteMessages.DeleteMessages();
	}
	
	/**
	 * The UpdateMessages method updates the flags that are associated with multiple messages. 
	 * Any number of messages can be updated. This method returns True for success or False 
 	 * for failure to the listener.
	 * 
	 * @param messages - Specifies the meesages to be updated and the flags to be updated.
	 */
	public void UpdateMessages(DeltaChange[] messages) {
		APIUpdateMessages updateMessages = new APIUpdateMessages(messages, immnSrvc, iamListener);
		updateMessages.UpdateMessages();
	}
	/**
	 * The UpdateMessage method updates the flags of a single message. This method returns True for 
	 * success and False for failure to the listener.
	 * 
	 * @param msgId - Specifies the identifier of the message to be updated.
	 * @param isUnread - (Optional) Indicates whether the message has (True) or has not (False) been read.
	 * @param isFavorite - (Optional) Indicates whetehr the messgae is (True) or is not (False) a favorite.
	 */
	public void UpdateMessage(String msgId, Boolean isUnread, Boolean isFavorite) {
		APIUpdateMessage updateMessage = new APIUpdateMessage();
		APIUpdateMessage.APIUpdateMessageParams params = 
					updateMessage.new APIUpdateMessageParams(msgId, isUnread, isFavorite );

		updateMessage.set(params, immnSrvc, iamListener);
		updateMessage.UpdateMessage();
	}
}
