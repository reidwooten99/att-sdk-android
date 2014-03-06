package com.att.api.error;

import org.json.JSONException;
import org.json.JSONObject;

import com.att.api.rest.RESTException;

public class Utils {
	
	public static InAppMessagingError 
	CreateErrorObjectFromException(RESTException exception) {
    	InAppMessagingError errorResponse = null;
		String errorMessage = "";
		String getTokenError = "";

    	try {
			JSONObject jobj = new JSONObject( exception.getErrorMessage());
			if( null !=  jobj) {
				if(jobj.toString().contains("error")) {
					JSONObject jobjGrant = new JSONObject(exception.getMessage());
					if(null != jobjGrant) {
						errorMessage = jobjGrant.getString("error");
					}
				}else if(jobj.toString().contains("RequestError")) {
					JSONObject jobjReqErr = jobj.getJSONObject("RequestError");
					if(null != jobjReqErr) {
						JSONObject jobjServcEx = jobjReqErr.getJSONObject("ServiceException");
						errorMessage = jobjServcEx.getString("Text");
					}
				}
			} 	
			 
			} 
    		catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
    	
    	errorResponse = new InAppMessagingError(errorMessage, exception.getStatusCode(), exception.getErrorMessage() );
    	return errorResponse;
    }

}
