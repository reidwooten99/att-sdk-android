package com.att.aabsampleapp;

public class Config {
	public static final String appScope = "AAB";
	public static final String fqdn = "https://api.att.com";

	// Enter the following details from the application created in
	// http://developer.att.com
	public static final String clientID = UPDATE_APP_KEY;
	public static final String secretKey = UPDATE_APP_SECRET;
	public static final String redirectUri = UPDATE_REDIRECT_URI;
	// Testers can test the customParam functionality by setting this config value to following combinations
	// "" - default behavior - do not send any custom_param
	// "bypass_onnetwork_auth"
	// "suppress_landing_page"
	// "bypass_onnetwork_auth,suppress_landing_page"
	public static final String customParam = "";
	// reduceTokenExpiryInSeconds_Debug is parameter that is used to test access token expiration logic
	public static final long reduceTokenExpiryInSeconds_Debug = 0;
}
