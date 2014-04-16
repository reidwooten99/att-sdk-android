package com.att.ads.listeners;

import com.att.ads.ATTAdViewError;

/**
 * Listener to handle the call backs from the ATTAdView.
 * One must confirm to this protocol to receive the notifications
 * when the ad is received successfully or failed to receive.
 *  
 * @author ATT 
 */
public interface ATTAdViewListener {
	
	/**
	 * Success call back method.
	 * Method triggers when the ad is received successfully and rendered.
	 * SDK passes the actual ad view response to the client.
	 * Generally this is the place holder for application developer to 
	 * do the logging or debugging purposes. 
	 */
	public void onSuccess(String adViewResponse);
	
	/**
	 * Error call back method.
	 * Method triggers when the ad is failed to receive valid response.
	 * SDK passes {@link ATTAdViewError} object to the client to analyze the problem.
	 * Generally this is the place holder for application developer to 
	 * do the error handling is own way. 
	 */
	public void onError(ATTAdViewError error);
}
