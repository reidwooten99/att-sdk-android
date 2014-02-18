
package com.att.api.immn.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.att.api.oauth.OAuthToken;
import com.att.api.rest.APIResponse;
import com.att.api.rest.RESTClient;
import com.att.api.rest.RESTException;
import com.att.api.service.APIService;
//import org.apache.commons.codec.binary.Base64;

public class IMMNService extends APIService {

    public IMMNService(String fqdn, OAuthToken token) {
        super(fqdn, token);
    }

    
    public SendResponse sendMessage(String address, String msg) throws RESTException, JSONException, ParseException {
        String[] addrs = {address};
        return this.sendMessage(addrs, msg);
    }

    
    public SendResponse sendMessage(String[] addresses, String msg) throws RESTException, JSONException, ParseException {
        return this.sendMessage(addresses, msg, null, false, null);     //SMS

    }

    public SendResponse sendMessage(String address, String subject, 
            boolean group) throws RESTException, JSONException, ParseException {
        return sendMessage(address, null, subject, group);     //Group Message
    }

    public SendResponse sendMessage(String address, String msg, String subject, 
            boolean group) throws RESTException, JSONException, ParseException {
        String[] addrs = {address};
        return sendMessage(addrs, null, subject, group); //Group Message
    }

    public SendResponse sendMessage(String[] addresses, String subject, 
            boolean group) throws RESTException, JSONException, ParseException {
        return sendMessage(addresses, null, subject, group);
    }

    public SendResponse sendMessage(String[] addresses, String msg, 
            String subject, boolean group) throws RESTException, JSONException, ParseException {
        return sendMessage(addresses, msg, subject, group, null);
    }

    public SendResponse sendMessage(String address, String msg, 
            String subject, boolean group, String[] attachments) throws RESTException, JSONException, ParseException {
        String[] addrs = {address};
        return sendMessage(addrs, msg, subject, group, attachments); // MMS
    }

    public SendResponse sendMessage(String[] addresses, String msg, 
            String subject, boolean group, String[] attachments) throws RESTException, JSONException,ParseException {

        final String endpoint = getFQDN() + "/myMessages/v2/messages";

        JSONObject jsonBody = new JSONObject();
        JSONObject body = new JSONObject();
        addresses = formatAddresses(addresses);

        if (msg != null ){
            body.put("text", msg);
        }

        if (subject != null)
            body.put("subject", subject);

        // group messages must specify multiple addresses
        if (addresses.length <= 1)
            group = false;
        body.put("isGroup", group);

        JSONArray jaddrs = new JSONArray();
        for (String addr : addresses)
        	if(addr != null)
        		jaddrs.put(addr);

        body.put("addresses", jaddrs);

        if ( attachments != null ) {
        	JSONArray jattach = new JSONArray();
        	int index = 0;
        	
        	for( String fattach : attachments) {
        		index++;
        		JSONObject attachBody = new JSONObject();
        		ByteArrayOutputStream baos = new ByteArrayOutputStream();  
        		String contentType = null;
        		String fileName = null;
        		if ( fattach.contains("jpeg") || fattach.contains("png") ) {
        			Bitmap bm = BitmapFactory.decodeFile(fattach);
        			boolean success = bm.compress(Bitmap.CompressFormat.JPEG, 70, baos); //bm is the bitmap object   
        			contentType = "image/png";
        			fileName = "image" + index + ".png";
        		} else if(fattach.contains("wav") || fattach.contains("mp4")) {
        				if(fattach.contains("wav")){
        					contentType = "audio/wav";
        					fileName = "audio.wav";
        				} else {
        					contentType = "video/mp4";
        					fileName = "video.mp4";
        				}
        				File inputFile = new File(fattach);
        				FileInputStream fis;					
        				try {
        					fis = new FileInputStream(inputFile);	
        					while ( true ) {
        						byte[] buf = new byte[4096];
        						int count = fis.read(buf, 0, 4096);
        						if ( count == -1 ) {
        							break;
        						}
        						baos.write(buf,0,count);
        					}
        				}					
        				catch (IOException ex) {
        					Log.e("AudioMMS", " exception", ex);
        				}
        			}
        			
				byte[] b = baos.toByteArray();

				String encodedBytes = Base64.encodeToString(b, Base64.URL_SAFE);
				encodedBytes = encodedBytes.replace('-', '+');
				encodedBytes = encodedBytes.replace('_', '/');
				encodedBytes = encodedBytes.replace("\n", "");
				attachBody.put("body", encodedBytes);
				attachBody.put("fileName", fileName);
				attachBody.put("content-type", contentType);
				attachBody.put("content-transfer-encoding", "BASE64");
				jattach.put(attachBody);
        	}
        	
        	if (msg != null) {
        		index++;
        		JSONObject attachBody = new JSONObject();
        		String contentType = "plain/text";
        		String fileName = "file" + index + ".txt";
        		
        		byte[] b = msg.getBytes();
				String encodedBytes = Base64.encodeToString(b, Base64.URL_SAFE);
				encodedBytes = encodedBytes.replace('-', '+');
				encodedBytes = encodedBytes.replace('_', '/');
				encodedBytes = encodedBytes.replace("\n", "");
				attachBody.put("body", encodedBytes);
				attachBody.put("fileName", fileName);
				attachBody.put("content-type", contentType);
				attachBody.put("content-transfer-encoding", "BASE64");
				jattach.put(attachBody);
        	}

        	body.put("messageContent", jattach);
        }
        
        jsonBody.put("messageRequest", body);
        
        final RESTClient rest = new RESTClient(endpoint)
            .setHeader("Accept", "application/json")
            .setHeader("Content-Type", "application/json")
            .addAuthorizationHeader(this.getToken());

        final APIResponse response = rest.httpPost(jsonBody.toString());


        JSONObject jobj = new JSONObject(response.getResponseBody());
		return SendResponse.valueOf(jobj);
    }

    public MessageList getMessageList(int limit, int offset) throws RESTException, JSONException, ParseException {
        return getMessageList(new MessageListArgs.Builder(limit, offset).build());
    }

    public MessageList getMessageList(MessageListArgs args) throws RESTException, JSONException, ParseException {
        final String endpoint = getFQDN() + "/myMessages/v2/messages";

        final RESTClient client = new RESTClient(endpoint)    
            .addAuthorizationHeader(getToken())
            .setHeader("Accept", "application/json")
            .setParameter("limit", "" + args.getLimit())
            .setParameter("offset", "" + args.getOffset());

        if (args.getMessageIds() != null) {
            String msgIds = StringUtils.join(args.getMessageIds(), ",");
            client.addParameter("messageIds", msgIds);
        }

        if (args.isFavorite() != null)
            client.addParameter("isFavorite", args.isFavorite() ? "true" : "false");

        if (args.isUnread() != null)
            client.addParameter("isUnread", args.isUnread() ? "true" : "false" );

        if (args.getType() != null)
            client.addParameter("type", args.getType().getString());

        if (args.getKeyword() != null)
            client.addParameter("keyword", args.getKeyword());

        if (args.isIncoming() != null)
            client.addParameter("isIncoming", args.isIncoming() ? "true" : "false" );

        APIResponse response = client.httpGet();
		JSONObject jobj = new JSONObject(response.getResponseBody());
		return MessageList.valueOf(jobj);
    }

    public Message getMessage(final String msgId) throws RESTException, JSONException, ParseException {
        final String endpoint = getFQDN() + "/myMessages/v2/messages/" + msgId;

        final APIResponse response = new RESTClient(endpoint)
            .addAuthorizationHeader(getToken())
            .setHeader("Accept", "application/json")
            .httpGet();

        JSONObject jobj = new JSONObject(response.getResponseBody());
		return Message.valueOf(jobj.getJSONObject("message"));
    }

    public MessageContent getMessageContent(String msgId, String partNumber)
            throws RESTException {

        final String endpoint = getFQDN() + "/myMessages/v2/messages/" + msgId
                + "/parts/" + partNumber;

        final APIResponse response = new RESTClient(endpoint)
            .addAuthorizationHeader(getToken())
            .setHeader("Accept", "application/json")
            .httpGet();

        String ctype = response.getHeader("Content-Type");
        String clength = response.getHeader("Content-Length");
        String content = response.getResponseBody();
        return new MessageContent(ctype, clength, content);
    }

    public DeltaResponseInternal getDelta(final String state) throws RESTException, JSONException, ParseException {
        final String endpoint = getFQDN() + "/myMessages/v2/delta";

        final APIResponse response = new RESTClient(endpoint)
            .addAuthorizationHeader(getToken())
            .setHeader("Accept", "application/json")
            .addParameter("state", state)
            .httpGet();

        JSONObject jobj = new JSONObject(response.getResponseBody());
		return DeltaResponseInternal.valueOf(jobj);
    }

    public void updateMessages(DeltaChange[] messages) throws RESTException, JSONException {
        final String endpoint = getFQDN() + "/myMessages/v2/messages";

        JSONArray jmsgs = new JSONArray();
        for (final DeltaChange change : messages) {
            JSONObject jchange = new JSONObject();
            jchange.put("messageId", change.getMessageId());
            
            if (change.isUnread() != null)
                jchange.put("isUnread", change.isUnread());

            if (change.isFavorite() != null)
                jchange.put("isFavorite", change.isFavorite());

            jmsgs.put(jchange);
        }

        JSONObject jobj = new JSONObject();
        jobj.put("messages", jmsgs);

        final APIResponse response = new RESTClient(endpoint)
            .addAuthorizationHeader(getToken())
            .setHeader("Accept", "application/json")
            .setHeader("Content-Type", "application/json")
            .httpPut(jobj.toString());

        if (response.getStatusCode() != 204) {
            final int code = response.getStatusCode();
            final String body = response.getResponseBody();
            throw new RESTException(code, body);
        }
    }

    public void updateMessage(String msgId, Boolean isUnread,
            Boolean isFavorite) throws RESTException, JSONException {

        final String endpoint = getFQDN() + "/myMessages/v2/messages/" + msgId;

        JSONObject jmsg = new JSONObject();
        if (isUnread != null) jmsg.put("isUnread", isUnread);
        if (isFavorite != null) jmsg.put("isFavorite", isFavorite);
        
        JSONObject jobj = new JSONObject();
        jobj.put("message", jmsg);

        final APIResponse response = new RESTClient(endpoint)
            .addAuthorizationHeader(getToken())
            .setHeader("Accept", "application/json")
            .setHeader("Content-Type", "application/json")
            .httpPut(jobj.toString());

        if (response.getStatusCode() != 204) {
            final int code = response.getStatusCode();
            final String body = response.getResponseBody();
            throw new RESTException(code, body);
        }
    }

    public void deleteMessages(String[] msgIds) throws RESTException {
        final String endpoint = getFQDN() + "/myMessages/v2/messages";

        String msgIdsStr = StringUtils.join(msgIds, ",");

        final APIResponse response = new RESTClient(endpoint)
            .setHeader("Accept", "application/json")
            .addAuthorizationHeader(getToken())
            .addParameter("messageIds", msgIdsStr)
            .httpDeleteMessages();

        if (response.getStatusCode() != 204) {
            final int code = response.getStatusCode();
            final String body = response.getResponseBody();
            throw new RESTException(code, body);
        }
    }

    public void deleteMessage(String msgId) throws RESTException {
        final String endpoint = getFQDN() + "/myMessages/v2/messages/" + msgId;

        final APIResponse response = new RESTClient(endpoint)
            .setHeader("Accept", "application/json")
            .addAuthorizationHeader(getToken())
            .httpDeleteMessage();

        if (response.getStatusCode() != 204) {
            final int code = response.getStatusCode();
            final String body = response.getResponseBody();
            throw new RESTException(code, body);
        }
    }

    public void createMessageIndex() throws RESTException {
        final String endpoint = getFQDN() + "/myMessages/v2/messages/index";

        final APIResponse response = new RESTClient(endpoint)
            .setHeader("Accept", "application/json")
            .addAuthorizationHeader(getToken())
            .httpPost();

        if (response.getStatusCode() != 202) {
            final int code = response.getStatusCode();
            final String body = response.getResponseBody();
            throw new RESTException(code, body);
        }
    }

    public MessageIndexInfo getMessageIndexInfo() throws RESTException, JSONException, ParseException {
        final String endpoint = getFQDN() + "/myMessages/v2/messages/index/info";

        final APIResponse response = new RESTClient(endpoint)
            .setHeader("Accept", "application/json")
            .addAuthorizationHeader(getToken())
            .httpGet();

        JSONObject jobj = new JSONObject(response.getResponseBody());

		return MessageIndexInfo.valueOf(jobj);
    }

    public NotificationConnectionDetails getNotificationConnectionDetails(
            String queues) throws RESTException, JSONException, ParseException {
            
        final String endpoint = getFQDN()
                + "/myMessages/v2/notificationConnectionDetails";

        final APIResponse response = new RESTClient(endpoint)
            .setHeader("Accept", "application/json")
            .addAuthorizationHeader(getToken())
            .setParameter("queues", queues)
            .httpGet();

        JSONObject jobj = new JSONObject(response.getResponseBody());
		return NotificationConnectionDetails.valueOf(jobj);
    }

}
