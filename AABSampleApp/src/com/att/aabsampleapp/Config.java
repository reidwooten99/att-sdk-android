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
	// reduceTokenExpiryInSeconds_Debug is parameter that is used to test access token expiration logic
	public static final long reduceTokenExpiryInSeconds_Debug = 172790;
	// Used to send the bad refresh token. Set this to blank for final version.
	public static final String appendToRefreshToken_Debug = "junk";

	// Delete these one by one
	public static final String byPassOnNetwork      =   "&custom_param=bypass_onnetwork_auth";
	public static final String byPassOnNetStr       =   "bypass_onnetwork_auth";
	public static final String suppressLandingPage  =   "&custom_param=suppress_landing_page";
	public static final String suppressLndgPageStr  =   "suppress_landing_page";
	public static final String none                 =   "none";
	public static final String byPassOnNetANDsuppressLandingPage =   
			                                            "&custom_param=bypass_onnetwork_auth,suppress_landing_page";
	public static String byPassANDsuppress          =   "";
	public static final String iamDownloadDirectory =   "InAppMessagingDownloads";
	public static final String backUpPreseted       =   "backUpPreseted";
	public static final String preset               =   "PRESET";
	public static final String expiredTime          =   "EXPIRED_TIME";
	public static final String oAuthCodeStr         =   "oAuthCodeStr";
	public static long tokenExpiredTime             =   0;
	public static final long noTokenExpiredTime     =   -1;

}
