package com.att.api.error;

import org.json.JSONException;
import org.json.JSONObject;

import com.att.api.rest.RESTException;

public class Utils {
	
	public static InAppMessagingError 
	CreateErrorObjectFromException(RESTException exception) {
    	InAppMessagingError errorResponse = null;
		/*try {
			JSONObject jobj = new JSONObject( exception.getErrorMessage());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
    	errorResponse = new InAppMessagingError(exception.getStatusCode(), exception.getErrorMessage() );
    	return errorResponse;
    }

}
