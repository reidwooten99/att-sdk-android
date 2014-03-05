package com.att.api.error;

import org.json.JSONException;
import org.json.JSONObject;

import com.att.api.rest.RESTException;

public class Utils {
	
	public static InAppMessagingError 
	CreateErrorObjectFromException(RESTException exception) {
    	InAppMessagingError errorResponse = null;
		String errorMessage = "";

    	try {
			JSONObject jobj = new JSONObject( exception.getErrorMessage());
			if( null !=  jobj) {
				JSONObject jobj1 = jobj.getJSONObject("RequestError");
				JSONObject jobj2 = jobj1.getJSONObject("ServiceException");
				errorMessage = jobj2.getString("Text");
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	errorResponse = new InAppMessagingError(errorMessage, exception.getStatusCode(), exception.getErrorMessage() );
    	return errorResponse;
    }

}
