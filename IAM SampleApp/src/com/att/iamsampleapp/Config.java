package com.att.iamsampleapp;

public class Config {
	
	public static String token;
	public static String refreshToken;
	public static final int messageLimit 			= 	500;
	public static final int messageOffset 			= 	0;
	public static final int maxRecipients 			= 	10;
	public static final int	maxAttachments			= 	21;
	public static final String fqdn		 			= 	"https://api.att.com";
	public static final String appScope	 			= 	 "IMMN,MIM";
	
	public static final String clientID				= 	 "xnbifoyphspwtskgeokooagrsz7da7cu";
	public static final String secretKey 			= 	 "q2e6qcynry87vncnuv4tpsol05kblrjp";
	public static final String redirectUri  		= 	 "https://localhost";
	public static final String iamDownloadDirectory = "InAppMessagingDownloads";
	// Testers can test the customParam functionality by setting this config value to following combinations
	// "" - default behavior - do not send any custom_param
	// "bypass_onnetwork_auth"
	// "suppress_landing_page"
	// "bypass_onnetwork_auth,suppress_landing_page"
	public static final String customParam = "suppress_landing_page";
	// reduceTokenExpiryInSeconds_Debug is parameter that is used to test access token expiration logic
	public static final long reduceTokenExpiryInSeconds_Debug = 172750;
}