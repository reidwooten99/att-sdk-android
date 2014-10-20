package com.att.iamsampleapp;

public class Config {
	
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