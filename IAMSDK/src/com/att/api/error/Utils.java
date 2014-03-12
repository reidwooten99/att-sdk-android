package com.att.api.error;

import com.att.api.rest.RESTException;

public class Utils {
	
	public static InAppMessagingError 
	CreateErrorObjectFromException(RESTException exception) {
    	InAppMessagingError errorResponse = null;
	    	errorResponse = new InAppMessagingError(exception.getStatusCode(), exception.getErrorMessage() );
    	return errorResponse;
    }

}
