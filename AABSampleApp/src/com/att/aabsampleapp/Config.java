package com.att.aabsampleapp;

import com.att.api.oauth.OAuthToken;

public class Config {

	public static long accessTokenExpiry = 50000000;
	public static String refreshToken;
	public static final String appScope = "AAB";
	public static OAuthToken authToken;
	public static String token;
	public static final String fqdn = "https://api.att.com";

	// Enter the following details from the application created in
	// http://developer.att.com
	public static final String clientID = APP_KEY;
	public static final String secretKey = APP_SECRET;
	public static final String redirectUri = REDIRECT_URI;
	
	// Testers can test the customParam functionality by setting this config value to following combinations
	// "" - default behavior - do not send any custom_param
	// "bypass_onnetwork_auth"
	// "suppress_landing_page"
	// "bypass_onnetwork_auth,suppress_landing_page"
	public static final String customParam = "";
}
