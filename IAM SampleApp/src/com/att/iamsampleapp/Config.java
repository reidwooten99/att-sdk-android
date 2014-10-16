package com.att.iamsampleapp;

public class Config {
	
	
	final static String APP_KEY              =   "8lwozmbmp9yup54gdrypph39mys5utbu";
	final static String APP_SECRET           =   "8cluptbe186cjydsh261x99yaepbyza8";
	public static final String UPDATE_REDIRECT_URI 	=   "https://developer.att.com";
	public static final int messageLimit 			= 	500;
	public static final int messageOffset 			= 	0;
	public static final int maxRecipients 			= 	10;
	public static final int	maxAttachments			= 	21;
	public static final String appScope	 			= 	"IMMN,MIM";
	public static final String clientID             =   APP_KEY;
	public static final String secretKey            =   APP_SECRET;
	public static final String redirectUri  		=   UPDATE_REDIRECT_URI;
	public static final String iamDownloadDirectory =   "InAppMessagingDownloads";
	
}