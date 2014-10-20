/* vim: set expandtab tabstop=4 shiftwidth=4 softtabstop=4 */

/*
 * ====================================================================
 * LICENSE: Licensed by AT&T under the 'Software Development Kit Tools
 * Agreement.' 2013.
 * TERMS AND CONDITIONS FOR USE, REPRODUCTION, AND DISTRIBUTIONS:
 * http://developer.att.com/sdk_agreement/
 *
 * Copyright 2013 AT&T Intellectual Property. All rights reserved.
 * For more information contact developer.support@att.com
 * ====================================================================
 */

package com.att.api.oauth;

import android.util.Log;

/**
 * An immutable OAuthToken object that encapsulates an OAuth 2.0 token, which
 * can be used for accessing protected resources.
 *
 * <p>
 * This class also offers convenience methods for checking whether the token is
 * expired, and saving/loading token from file in an asynchronous-safe manner.
 * </p>
 *
 * An example of usage can be found below:
 * <pre>
 * <code>
 * // declare variables
 * final long expiry = OAuthToken.NO_EXPIRATION;
 * final String accessToken = "12345";
 * final String refreshToken = "12345";
 * OAuthToken token = new OAuthToken(accessToken, expiry, refreshToken);
 *
 * // check if access token is expired
 * if (token.isAccessTokenExpired()) {
 *     System.out.println("Access token is expired!");
 * }
 *
 * // save token
 * token.saveToken("/tmp/token.properties");
 *
 * // load token
 * token = OAuthToken.loadToken("/tmp/token.properties");
 *
 * </code>
 * </pre>
 *
 * @author dg185p,ps350r
 */
public class OAuthToken {

    /* Access token. */
    private String accessToken;

    /* Unix timestamp, in seconds, to denote access token expiry. */
    private  long accessTokenExpiry;

    /* Refresh token. */
    private String refreshToken;

    /* Used to indicate access token does not expire. */
    public static final long NO_EXPIRATION = -1;
    
    final String TAG = "OAuthToken";
   
    /**
     * Gets the current time as a Unix timestamp.
     *
     * @return seconds since Unix epoch
     */
    public static long xtimestamp() {
        return System.currentTimeMillis() / 1000;
    }

    /**
     * Creates an OAuthToken object with the specified parameters.
     *
     * <p>
     * <strong>NOTE:</strong> To make an access token never expire, set the
     * <code>expiresIn</code> parameter to <code>OAuthToken.NO_EXPIRATION</code>
     * </p>
     *
     * @param accessToken access token
     * @param expiresIn time in seconds token expires since
     *                  <code>creationTime</code>
     * @param refreshToken refresh token
     * @param creationTime access token creation time as a Unix timestamp
     */
    
    
    public OAuthToken(String accessToken, long expiresIn, String refreshToken,
            long creationTime) {

        if (expiresIn == OAuthToken.NO_EXPIRATION) {
            this.accessTokenExpiry = OAuthToken.NO_EXPIRATION;
        } 
        else {
             this.accessTokenExpiry = expiresIn + creationTime;
        }
        
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    
    }
    
    /**
     * Creates an OAuthToken object with the <code>creationTime</code> set to
     * the current time.
     *
     * @param accessToken access token
     * @param expiresIn time in seconds token expires from current time
     * @param refreshToken refresh token
     * @see #OAuthToken(String, long, String, long)
     */
    public OAuthToken(String accessToken, long expiresIn, String refreshToken) {
        this(accessToken, expiresIn, refreshToken, xtimestamp());
    }

	/**
     * Gets whether the access token is expired.
     *
     * @return <tt>true</tt> if access token is expired, <tt>false</tt>
     *         otherwise
     */
    public boolean isAccessTokenExpired() {
    	
    	Log.i(TAG, "accessTokenExpiry: " + String.valueOf(accessTokenExpiry));
    	Log.i(TAG, "xtimestamp(): " + String.valueOf(xtimestamp()));
        return accessTokenExpiry != NO_EXPIRATION
            && xtimestamp() >= accessTokenExpiry;
    }
    
    /**
     * Gets access token expiry.
     *
     * @return access token
     */
    public long getAccessTokenExpiry() {
        return accessTokenExpiry;
    }
    
    /**
     * Gets access token.
     *
     * @return access token
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * Gets refresh token.
     *
     * @return refresh token
     */
    public String getRefreshToken() {
        return refreshToken;
    }
    
    /**
     * Sets refresh token.
     *
     */
    public void setRefreshToken(String AC_freshToken) {
        refreshToken = AC_freshToken;
    }
    
    /**
     * Sets access token.
     *
     */
    
    public void setAccessToken(String AC_token) {
        accessToken = AC_token;
    }
    
    /**
     * Sets access token expired time.
     *
     */
    public void setAccessTokenExpiry(long AC_expiry) {
        accessTokenExpiry = AC_expiry;
    }

    /*
     * Not yet implemented.
     *
     * @param useCaching not yet implemented
     */
    public static void useTokenCaching(boolean useCaching) {
        // TODO (pk9069): Implement
        throw new UnsupportedOperationException();
    }
}
