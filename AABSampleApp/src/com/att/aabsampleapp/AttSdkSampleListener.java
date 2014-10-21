package com.att.aabsampleapp;

import android.util.Log;
import android.widget.Toast;

import com.att.api.error.AttSdkError;
import com.att.api.util.TokenUpdatedListener;
import com.att.sdk.listener.AttSdkListener;

public class AttSdkSampleListener implements AttSdkListener {
	protected String operationName = "Unknown";
	
	public AttSdkSampleListener(String name) {
        this.operationName = name;
    }
	
	@Override
	public void onSuccess(Object response) {
		Log.i("Failed: Please override " + operationName + " onSuccess method.",  "Response=\n" + response.toString());
	}

	@Override
	public void onError(AttSdkError error) {
		Log.i(operationName + " on error", "Error:" + error.getErrorMessage() + 
				".\nHttpResponseCode:" + error.getHttpResponseCode() + 
				". HttpResponse: " + error.getHttpResponse());
		switch (error.getHttpResponseCode()) {
		case 400: // invalid_grant
		case 401: // UnAuthorized Request
			TokenUpdatedListener.DeleteSavedToken();
			break;
		}
	}

}
