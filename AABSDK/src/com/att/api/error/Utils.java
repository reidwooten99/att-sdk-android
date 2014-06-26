package com.att.api.error;

import com.att.api.rest.RESTException;

public class Utils {
	
	public static AttSdkError 
	CreateErrorObjectFromException(RESTException exception) {
    	AttSdkError errorResponse = null;
    		if(exception == null) {
    			errorResponse = new AttSdkError("The size exceeded the limit");     
    		} else {
    			errorResponse = new AttSdkError(exception.getStatusCode(), exception.getErrorMessage() );
    		}
    	return errorResponse;
    }

}
