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
	public static final String clientID = API_KEY;
	public static final String secretKey = API_SECRET;
	public static final String redirectUri = "https://localhost";
	// Testers can test the customParam functionality by setting this config value to following combinations
	// "" - default behavior - do not send any custom_param
	// "bypass_onnetwork_auth"
	// "suppress_landing_page"
	// "bypass_onnetwork_auth,suppress_landing_page"
	public static final String customParam = "";
	// reduceTokenExpiryInSeconds_Debug is parameter that is used to test access token expiration logic
	public static final long reduceTokenExpiryInSeconds_Debug = 0; //172770;
	// Used to send the bad refresh token. Set this to blank for final version.
	public static final String appendToRefreshToken_Debug = "";
	
	// Static names of setting stored in Preferences
	public static final String accessTokenSettingName = "AccessToken";
	public static final String refreshTokenSettingName = "RefreshToken";
	public static final String tokenExpirySettingName = "AccessTokenExpiry";
	public static final String customParamSettingName = "CustomParam";
	public static final String reduceExpiryBySettingName = "ReduceExpiryBy";
}
