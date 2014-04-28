package com.att.testaab;

import com.att.api.oauth.OAuthToken;

public class Config {	
	public static String token = "abcd";
	public static long accessTokenExpiry = 50000000;
	public static String refreshToken = "wxyz";
	public static final int messageLimit 			= 	500;
	public static final int messageOffset 			= 	0;
	public static final int maxRecipients 			= 	10;
	public static final int	maxAttachments			= 	21;
	public static final String fqdn		 			= 	"http://ldev.code-api-att.com:8888";
	public static final String iamFqdn				= 	"https://api.att.com";
	//public static OAuthToken authToken 				= 	new OAuthToken("abcd", 1, "xyz");
	
	/*public static final String clientID			= 	 APP_KEY;
	public static final String secretKey 			= 	 APP_SECRET;
	public static final String appScope	 			= 	 APP_SCOPE;
	public static final String redirectUri  		= 	 REDIRECT_URI;*/
	
	public static final String clientID				= 	 "7vroavot7vittuzg8zegqszjnymyf3lw";
	public static final String secretKey 			= 	 "tvygie2blq8gf1yhshylf9kqspi7gfvx";
	public static final String appScope	 			= 	 "IMMN,MIM";
	public static final String redirectUri  		= 	 "https://developer.att.com";
	public static final String iamDownloadDirectory = "InAppMessagingDownloads";
}