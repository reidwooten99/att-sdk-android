package com.att.aabsampleapp;

import com.att.api.oauth.OAuthToken;

public class Config {

		public static long accessTokenExpiry = 50000000;
		public static String refreshToken ;

		//FINAL CREDENTIALS
		
		/*public static final String clientID			= 	 APP_KEY;
		public static final String secretKey 			= 	 APP_SECRET;
		public static final String appScope	 			= 	 APP_SCOPE;
		public static final String redirectUri  		= 	 REDIRECT_URI;*/
		
		//IAM Credentials
		
		/*public static final String clientID				= 	 "7vroavot7vittuzg8zegqszjnymyf3lw";
		public static final String secretKey 			= 	"tvygie2blq8gf1yhshylf9kqspi7gfvx";
		public static final String redirectUri  		= 	"https://developer.att.com";
		public static final String 		fqdn			= 	"https://api.att.com";
		public static final String appScope	 			= 	 "IMMN,MIM";*/

		
		//F6 Credentials - NOT WORKING 
		
		/*public static final String clientID			= 	 "qyptay8tyizxqvp2teeiipqeofetdogz";
		public static final String secretKey 			= 	 "fi7vwipukqnlhgxpjchi1kmwbiwa4kwj";
		public static final String redirectUri  		= 	"https://www.something.com";
		public static final String appScope	 			= 	 "AAB";
		*/
		
		//SIMULATOR ON HEROKU 
		
		/*public static String token 					= "PAhHNHrmuLNYQnot7ERJa2hEW9ClFpT0";
		
		public static final String clientID				= 	 "hjelfbo7ioni1xhegmbp51dyi3ylgirn";
		public static final String secretKey 			= 	"opfahnjscrfra3b1swab1nziam97xb0v";
		public static final String redirectUri  		= 	"https://developer.att.com";
		public static final String appScope	 			= 	 "AAB";
		public static final String 		fqdn			= 	"http://api-simulator.herokuapp.com";*/
		
		
		//F3 Credentials 
		public static String token 					= "PAhHNHrmuLNYQnot7ERJa2hEW9ClFpT0";
		
		public static final String clientID				= 	 "hjelfbo7ioni1xhegmbp51dyi3ylgirn";
		public static final String secretKey 			= 	"opfahnjscrfra3b1swab1nziam97xb0v";
		public static final String redirectUri  		= 	"https://developer.att.com";
		public static final String appScope	 			= 	 "AAB";
		public static final String 		fqdn			= 	"https://api-uat.mars.bf.sl.attcompute.com";
	}
