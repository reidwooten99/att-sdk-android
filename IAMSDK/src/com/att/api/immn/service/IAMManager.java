package com.att.api.immn.service;

import android.util.Log;

import com.att.api.error.InAppMessagingError;
import com.att.api.error.Utils;
import com.att.api.immn.listener.ATTIAMListener;
import com.att.api.immn.listener.AttSdkTokenUpdater;
import com.att.api.oauth.OAuthService;
import com.att.api.oauth.OAuthToken;
import com.att.api.rest.RESTException;
/**
 * This class encapsulates the AT&T RESTfull APIs for In-App Messaging.
 * 
 * @author dg185p
 * @author ps350r
 * 
 */
public class IAMManager {

	public static IMMNService immnSrvc = null;
	public static OAuthService osrvc = null;
	private ATTIAMListener iamListener;
	private static AttSdkTokenUpdater tokenListener = null;
	private static OAuthToken currentToken = null; // NOTE: This variable may not be required. Just immnSrvc can be used.
	private static long reduceTokenExpiryInSeconds_Debug = 0;
	private final static Object lockRefreshToken = new Object();
	private static String apiFqdn = "https://api.att.com";
	
	/**
	 * The IAMManager method creates an IAMManager object.
	 * @param fqdn - Specifies the fully qualified domain name that is used to send requests.
	 * @param token - Specifies the OAuth token that is used for authorization.
	 * @param iamListener - Specifies the Listener for callbacks.
	 */
	public IAMManager(String fqdn, OAuthToken token, ATTIAMListener iamListener) {
		
		// TODO: Update currentToken, if token is not null
		if (immnSrvc == null) {
			immnSrvc = new IMMNService(fqdn, token);
		}
		this.iamListener = iamListener;
	}

	/**
	 * The getMessages method gets a message based on its message Id. 
	 * 
	 * @param msgId - Specifies the message identifier of a subscriber message in the AT&T 
	 * Messages environment.
	 * 
	 * @return Returns a response of type Message to the listener.
	 * 
	 */
	public void GetMessage(String msgId) {
		APIGetMessage getMessage = new APIGetMessage(msgId, this, iamListener);
		getMessage.GetMessage(msgId);
	}
	
	/**
	 * 
	 *  The SendMesage method sends an MMS or SMS message. 
	 * 
	 * @param addresses - Specifies the addresses where the message is sent. The addresses must be specified 
	 * in one of the following formats (at least one message must be specified): 
	 * <ul>
	 * <li> MSISDN: This format is the mobile number based on North American Numbering Plan with a maximum 
	 * length of 11 digits. It must be preceded by the following prefix: tel: 
	 * <li>Valid formats are: 
	 * 		<ul>
	 * 		<li> tel:+12012345678
	 * 		<li> tel:12012345678 
	 * 		<li> tel:2012345678
	 * 		</ul>
	 * International numbers are not supported.
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
	 * <li> If the request is detected to be an SMS message, then the following character set is supported:  ISO-8859-1
	 * The message parameter is required if no attachments are specified.
	 * </ul>
	 * 
	 * @param subject - Specifies the header for the message.
	 * @param group	- If True, indicates the message is sent to multiple recipients. If False, indicates that the message 
	 * is a broadcast mesage.
	 * @param attachments - Specifies the filenames of attachments associated with the message.
	 * 
	 * 
	 * @return Returns a response of type SendResponse to the listener.
	 * 
	 */
	
	public void SendMessage(String[] addresses, String message, String subject, boolean group, String[] attachments) {
		APISendMessage sendMessage = new APISendMessage(addresses, message, subject, group, attachments, 
														this, iamListener);
		sendMessage.SendMessage();
	}

	
	/**
	 * The GetMessageContent method gets a message attachment based on the attachment 
	 * and message identifier. 
	 * 
	 * @param msgId - Specifies the identifier of a subscriber message in the AT&T Messages environment.
	 * @param partNumber - Specifies the content identifier of the attachment to be retrieved.
	 * 
	 * @return Returns a response of type MessageContent to the listener.
	 * 
	 */
	public void GetMessageContent(String msgId, String partNumber) {
		APIGetMessageContent getMessageContent = new APIGetMessageContent(msgId, partNumber, this, iamListener);
		getMessageContent.GetMessageContent();	
	}
	
	/**
	 * The GetMessageList method gets a block of messages based on an offset value and the number 
	 * of messages to retrieve. The list of messages is returned in the order that they were received, starting 
	 * with the most recent. 
	 * 
	 * @param limit - Specifies the number of messages to return. A maximum value of 500 is supported.
	 * @param offset - Specifies the offset from the beginning of the ordered set of messages.
	 * 
	 * @return Returns a response of type MessageList to the listener.
	 * 
	 */
	public void GetMessageList(int limit, int offset) {
		APIGetMessageList getMessageList = new APIGetMessageList(limit, offset, this, iamListener);
		getMessageList.GetMessageList();
	}
	
	/**
	 * The GetDelta method checks to see if the client is in a specific state.
	 * 
	 * 
	 * @param state - Specifies the state of the client. This string is returned by either the 
	 * GetMessageIndex or GetMessageList method.
	 * 
	 * @return Returns a response of type DeltaResponse to the listener.
	 * 
	 */
	public void GetDelta(String state) {
		APIGetDelta getDelta = new APIGetDelta(state, this,iamListener);
		getDelta.GetDelta();
	}
	
	/**
	 * The GetMessageIndexInfo method gets the state, status, and message count of the index cache for the 
	 * inbox of the subscriber. 
	 * 
	 * @return Returns a response of type MessageIndexInfo to the listener.
	 * 
	 */
	public void GetMessageIndexInfo() {
		APIGetMessageIndexInfo getMessageIndexInfo = new APIGetMessageIndexInfo(this, iamListener);
		getMessageIndexInfo.GetMessageIndexInfo();
	}
	
	/**
	 * The GetNotificationDetails method gets details about the credentials, endpoint,
	 * and resource information that can be used to set up a notification connection. 
	 * 
	 * @param queues - Specifies the name of the resource for which the notification details are returned. 
	 * Supported resources include the following:
	 * <ul>
	 * 
	 * <li>TEXT : Subscription to this resource will provide notification related to messages stored as TEXT in the cloud inbox.
	 * <li>MMS  : Subscription to this resource will provide notification related to messages stored as MMS in the cloud inbox.
	 * </ul>
	 * 
	 * @return Returns a response of type NotificationConnectionDetails to the listener.
	 * 
	 */
	public  void GetNotificationConnectionDetails(String queues) {
		APIGetNotificationConnectionDetails getNotificationConnectionDetails = 
						new APIGetNotificationConnectionDetails(queues,this,iamListener); 
		getNotificationConnectionDetails.GetNotificationConnectionDetails();
	}
	
	/**
	 * The CreateMessageIndex method creates an index cache for the inbox of the subscriber.
	 * This method must be called before any of the other operations are used. 
	 * In addition, if a message index is inactive for 30 or more days, then the index cache
	 * must be recreated.
	 * 
	 * @return Returns True for success or False for failure to the listener.
	 */
	public void CreateMessageIndex() {		
		APICreateMessageIndex createMessageIndex = new APICreateMessageIndex(this, iamListener);
		createMessageIndex.CreateMessageIndex();	
	}
	
	/**
	 * The DeleteMessage method deletes a specific message from an inbox.
	 * 
	 * @param msgId - Specifies the Id of the message to be deleted.
	 * 
	 * @return Returns True for success or False for failure to the listener.
	 * 
	 */
	public void DeleteMessage(String msgId) {		
		APIDeleteMessage deleteMessage = new APIDeleteMessage(msgId, this, iamListener);
		deleteMessage.DeleteMessage();		
	}
	/**
	 * The DeleteMessages method deletes multiple messages from an inbox. The messagee identifiers are 
	 * specified in the query string in the request. 
	 * 
	 * @param msgIds - Specifies a comma delimited list of message identifiers.
	 *  
	 * @return Returns True for success or False for failure to the listener.
	 * 
	 */
	public void DeleteMessages(String[] msgIds) {
		APIDeleteMessages deleteMessages = new APIDeleteMessages(msgIds, this, iamListener);
		deleteMessages.DeleteMessages();
	}
	
	/**
	 * The UpdateMessages method updates the flags that are associated with multiple messages. 
	 * Any number of messages can be updated. 
	 * 
	 * @param messages - Specifies the messages to be updated and the flags to be updated.
	 * 
	 * @return Returns True for success or False for failure to the listener.
	 * 
	 */
	public void UpdateMessages(DeltaChange[] messages) {
		APIUpdateMessages updateMessages = new APIUpdateMessages(messages, this, iamListener);
		updateMessages.UpdateMessages();
	}
	/**
	 * The UpdateMessage method updates the flags of a single message. 
	 * 
	 * @param msgId - Specifies the identifier of the message to be updated.
	 * @param isUnread - (Optional) Indicates whether the message has (True) or has not (False) been read.
	 * @param isFavorite - (Optional) Indicates whether the message is (True) or is not (False) a favorite.
	 * 
	 * @return Returns True for success or False for failure to the listener.
	 * 
	 */
	public void UpdateMessage(String msgId, Boolean isUnread, Boolean isFavorite) {
		APIUpdateMessage updateMessage = new APIUpdateMessage();
		APIUpdateMessage.APIUpdateMessageParams params = 
					updateMessage.new APIUpdateMessageParams(msgId, isUnread, isFavorite );

		updateMessage.set(params, this, iamListener);
		updateMessage.UpdateMessage();
	}
	
	public static void SetCurrentToken(OAuthToken token) {
		currentToken = token;
		immnSrvc = new IMMNService(apiFqdn, token);
	}
	
	public static void SetReduceTokenExpiryInSeconds_Debug (long value) {
		reduceTokenExpiryInSeconds_Debug = value;
	}
	
	public static long GetReduceTokenExpiryInSeconds_Debug () {
		return reduceTokenExpiryInSeconds_Debug;
	}
	
	public static void SetTokenUpdatedListener(AttSdkTokenUpdater listener) {
		tokenListener = listener;
	}
	
	public static Boolean isCurrentTokenExpired() {
		return (currentToken.getAccessTokenExpiry() - (System.currentTimeMillis() / 1000) < reduceTokenExpiryInSeconds_Debug);		
	}
	
	public static void SetApiFqdn(String fqdn) {
		apiFqdn = fqdn;
	}
	
	public Boolean CheckAndRefreshExpiredTokenAsync() {
		try {
			synchronized (lockRefreshToken) {
				if (isCurrentTokenExpired()) {
					String refreshTokenValue = currentToken.getRefreshToken();
					currentToken = null;
					InAppMessagingError errorObj = new InAppMessagingError();
					try {
						if (osrvc == null) throw new Exception("Failed during token refresh. osrvc not initiazed.");
						OAuthToken authToken = osrvc.refreshToken(refreshTokenValue);
						if (authToken != null) {
							SetCurrentToken(authToken);
							Log.i("getRefreshTokenListener",
									"onSuccess Message : " + authToken.getAccessToken());
							if (tokenListener != null) {
								tokenListener.onTokenUpdate(authToken);
							}
						} else {
							throw new Exception("Failed during token refresh.");
						}
					} catch (RESTException e) {
						Log.i("getRefreshTokenListener", "REST Error:" + e.getMessage());
						errorObj = Utils.CreateErrorObjectFromException( e );
					} catch (Exception e) {
						Log.i("getRefreshTokenListener", "Error:" + e.getMessage());
					}
					if (currentToken == null) {
						if (iamListener != null) {
							iamListener.onError(errorObj);
						}						
						if (tokenListener != null) {
							tokenListener.onTokenDelete();
						}			
					}
				}
			}
		} catch (Exception /*InterruptedException*/ e) {
			currentToken = null;
		}	
		return (currentToken != null);
	}
}
