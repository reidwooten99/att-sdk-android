package com.att.testaab;


public class Config {	

	public static String token = "ERVYgpBqYbFJMnWvCAnhqJVOUXShDOQD";

	public static long accessTokenExpiry = 50000000;
	public static String refreshToken ;
	public static final int messageLimit 			= 	500;
	public static final int messageOffset 			= 	0;
	public static final int maxRecipients 			= 	10;
	public static final int	maxAttachments			= 	21;
	public static final String fqdn		 			= 	"https://api-stage.mars.bf.sl.attcompute.com";
	public static final String ldevFqdn				=	"http://ldev.code-api-att.com:8888";
	public static final String iamFqdn				= 	"https://api.att.com";
	
	//public static final String f6Fqdn		 		= 	"https://api-stage.mars.bf.sl.attcompute.com";
	//public static String f6token 					= 	"ERVYgpBqYbFJMnWvCAnhqJVOUXShDOQD";

	
	/*public static final String clientID			= 	 APP_KEY;
	public static final String secretKey 			= 	 APP_SECRET;
	public static final String appScope	 			= 	 APP_SCOPE;
	public static final String redirectUri  		= 	 REDIRECT_URI;*/
	
	public static final String clientID				= 	 "qyptay8tyizxqvp2teeiipqeofetdogz";//"7vroavot7vittuzg8zegqszjnymyf3lw";
	public static final String secretKey 			= 	 "fi7vwipukqnlhgxpjchi1kmwbiwa4kwj";//"tvygie2blq8gf1yhshylf9kqspi7gfvx";
	public static final String appScope	 			= 	 "AAB";
	public static final String redirectUri  		= 	"https://www.something.com";
	public static final String iamDownloadDirectory = "InAppMessagingDownloads";
}