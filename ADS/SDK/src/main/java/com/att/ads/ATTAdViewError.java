package com.att.ads;


import java.io.Serializable;

/**
 * Class encapsulates the error type, message and exception information. 
 * While any error occurs in the processing or downstream operations, 
 * object will be constructed and passed to the Client.
 * 
 * @author ATT
 *
 */
public class ATTAdViewError implements Serializable {

	private static final long serialVersionUID = 5607352650665167278L;
	
	// Error types	
	public static final int ERROR_OAUTH_ERROR = -10;
	public static final int ERROR_ADSERVER_ERROR = -20;
	public static final int ERROR_NETWORK_ERROR = -30;
	public static final int ERROR_PARAMETER_ERROR = -40;
	public static final int ERROR_OTHER_ERROR = -50;

	//public static final int ERROR_PARSE_RESPONSE_FAILED = -60;

	private String mMessage = null;
	private int mType = 0;
	private Exception mEx = null;
	
	public ATTAdViewError(int type, String message, Exception ex) {
		this.mMessage = message;
		this.mType = type;
		this.mEx = ex;
	}
	
	public ATTAdViewError(int type, String message) {
		this.mMessage = message;
		this.mType = type;
	}
	
	/** Get the error message associated with this error in human-readable format. **/
	public String getMessage() {
		return this.mMessage;
	}

	/** Get the type of error we encountered in computer-friendly format (see above) **/
	public int getType() {
		return this.mType;
	}
	
	/** Get the exception that caused this error, or null if there is no exception associated with this error. **/
	public Exception getException() {
		return this.mEx;
	}

}
