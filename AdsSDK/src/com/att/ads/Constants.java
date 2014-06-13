package com.att.ads;



/**
 * Class encapsulating all of the constants and some are default values. 
 * 
 * @author ATT
 *
 */
public class Constants {

	public static final String SDK_VERSION = "2.0.6";
	
	public static final long AD_RELOAD_PERIOD = 120000; //in milliseconds
	public static final int DEFAULT_REQUEST_TIMEOUT = 20000; //in seconds
	
	public static final int DEFAULT_AD_SERVER_TIMEOUT = 3000; // server side timeout, in milliseconds; 

	public static final int DEFAULT_AD_TYPE = -1; // text (1) || image (2) || text or image (3)
	
	//public static final String OAUTH_URL = "https://api-uat.bf.pacer.sl.attcompute.com/oauth/access_token";
	//public static final String OAUTH_URL = "https://api-uat.pacer.bf.sl.attcompute.com/oauth/access_token";
	public static final String OAUTH_URL = "https://api.att.com/oauth/access_token";
	//public static final String ADS_URL = "https://api-uat.bf.pacer.sl.attcompute.com/rest/1/ads";
	//public static final String ADS_URL = "https://api-uat.pacer.bf.sl.attcompute.com/rest/1/ads";
	public static final String ADS_URL = "https://api.att.com/rest/1/ads";
	
	public static final String FAILURE = "failure";
	public static final String SUCCESS = "success";
	//set connection timeout 
	public static final int CONNECTION_TIMEOUT = 60000;
	//set socket connection timeout 
	public static final int SOCKET_TIMEOUT = 60000;
	
	public static final String STR_INVALID_PARAM = "invalid param";
	// message returned in error callback when no ads found
	public static final String STR_NULL_SERVER_RESPONSE = "null server response";
	public static final String STR_EMPTY_SERVER_RESPONSE = "empty server response (no ads)"; 
	// message returned in error callback when no ads found
	public static final String STR_JSON_PARSE_PROBLEM = "JSON parsing problem";
	public static final String STR_NETWORK_PROBLEM = "Network not reachable at this moment";
	public static final String STR_UDID_PROBLEM = "UDID is mandatory parameter and it should not be null or empty";
	public static final String STR_APP_KEY_PROBLEM = "App Key is mandatory parameter and it should not be null or empty";
	public static final String STR_SECRET_PROBLEM = "Secret Key is mandatory parameter and it should not be null or empty";
	public static final String STR_CATEGORY_PROBLEM = "Category is mandatory parameter and it should not be null or empty";

	
	//OATH parameters 
	public static final String GRANT_TYPE  = "client_credentials";
	public static final String REFRESH_TOKEN  = "refresh_token";
	public static final String SCOPE = "ADS";
	//Types of ads
	public static final String ADS_THIRDPARTY_TYPE  = "thirdparty";
	public static final String ADS_IMAGE_TYPE  = "image";
	public static final String ADS_TEXT_TYPE = "text";
	public static final String ADS_IMAGE_URL  = "ImageUrl";
	public static final String ADS_CLICK_URL  = "ClickUrl";
	
	//Ads headers 
	public static final String AUTHORIZATION  = "Authorization";
	public static final String XARG  = "X-Arg";
	public static final String UDID  = "UDID";
	public static final String PSEDO_ZONE  = "psedo_zone";
	
	
	
}
