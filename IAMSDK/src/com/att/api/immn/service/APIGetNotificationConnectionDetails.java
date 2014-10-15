package com.att.api.immn.service;

import java.text.ParseException;

import org.json.JSONException;

import android.os.AsyncTask;
import android.os.Handler;

import com.att.api.error.InAppMessagingError;
import com.att.api.error.Utils;
import com.att.api.immn.listener.ATTIAMListener;
import com.att.api.rest.RESTException;

public class APIGetNotificationConnectionDetails implements ATTIAMListener {
	
	String queues = null;
	private ATTIAMListener iamListener;
	//IMMNService immnSrvc;
	protected Handler handler = new Handler();

	public APIGetNotificationConnectionDetails(String queues, IMMNService immnService, ATTIAMListener iamListener) {
		
		this.queues = queues;
		//this.immnSrvc = immnService;
		this.iamListener = iamListener;
	}
	
	public void GetNotificationConnectionDetails () {
		
		GetNotificationConnectionDetailsTask getNotificationConnectionDetailsTask = 
				new GetNotificationConnectionDetailsTask();
		getNotificationConnectionDetailsTask.execute(queues);		
	}
	
	public class GetNotificationConnectionDetailsTask extends AsyncTask<String, Void, NotificationConnectionDetails> {

		@Override
		protected NotificationConnectionDetails doInBackground(String... params) {
			// TODO Auto-generated method stub
			NotificationConnectionDetails notificationDetails = null;
			InAppMessagingError errorObj = new InAppMessagingError();

			try {
				if (!IAMManager.CheckAndRefreshExpiredTokenAsync()) return null;
				notificationDetails = IAMManager.immnSrvc.getNotificationConnectionDetails(params[0]);
			} catch (RESTException e) {
				errorObj = Utils.CreateErrorObjectFromException( e );
				onError( errorObj );
			} catch (JSONException e) {
				//errorObj.setErrorMessage(e.getMessage());
				errorObj = new InAppMessagingError(e.getMessage());
				onError(errorObj);			
			} catch (ParseException e) {
				//errorObj.setErrorMessage(e.getMessage());
				errorObj = new InAppMessagingError(e.getMessage());
				onError(errorObj);		
			}
			
			return notificationDetails;
		}

		@Override
		protected void onPostExecute(NotificationConnectionDetails notificationDetails) {
			// TODO Auto-generated method stub
			super.onPostExecute(notificationDetails);
			if(null != notificationDetails) {
				onSuccess((NotificationConnectionDetails) notificationDetails);
			}
			
		}
		
	}

	@Override
	public void onSuccess(final Object notificationDetails) {
		// TODO Auto-generated method stub
		handler.post(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
		    	 if(null != iamListener) { 
					iamListener.onSuccess((NotificationConnectionDetails) notificationDetails);
				}
				
			}
		});
		
	}

	@Override
	public void onError(final InAppMessagingError error) {
		// TODO Auto-generated method stub
		handler.post(new Runnable() {
			@Override
			public void run() {
				if (null != iamListener) {
					iamListener.onError(error);
				}
			}
		});
		
	}
	

}
