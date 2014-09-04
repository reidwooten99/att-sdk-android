package com.att.iamsampleapp;

public class Config {
	
	public static String token;
	public static String refreshToken;
	public static final int messageLimit 			= 	500;
	public static final int messageOffset 			= 	0;
	public static final int maxRecipients 			= 	10;
	public static final int	maxAttachments			= 	21;
	public static final String fqdn                 =   "https://api.att.com";
	public static final String fqdn_extend          =   "/oauth/v4/authorize";
	
	public static final String appScope	 			= 	"IMMN,MIM,SMS,MMS";
	public static final String clientID				= 	"8lwozmbmp9yup54gdrypph39mys5utbu";
	public static final String secretKey 			= 	"8cluptbe186cjydsh261x99yaepbyza8";
	public static final String redirectUri  		=   "https://developer.att.com";
	public static final String byPassOnNetwork      =   "&custom_param=bypass_onnetwork_auth";
	public static final String byPassOnNetStr       =   "bypass_onnetwork_auth";
	public static final String suppressLandingPage  =   "&custom_param=suppress_landing_page";
	public static final String suppressLndgPageStr  =   "suppress_landing_page";
	public static final String byPassOnNetANDsuppressLandingPage =   
			                                            "&custom_param=bypass_onnetwork_auth,suppress_landing_page";
	public static String byPassANDsuppress          =   "";
	public static final String iamDownloadDirectory =   "InAppMessagingDownloads";
	public static final String backUpPreseted       =   "backUpPreseted";
	

}