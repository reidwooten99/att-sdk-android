package com.att.api.immn.service;

import java.text.ParseException;

import org.json.JSONException;

import android.os.AsyncTask;
import android.os.Handler;

import com.att.api.immn.listener.ATTIAMListener;
import com.att.api.rest.RESTException;

public class APIGetNotificationConnectionDetails implements ATTIAMListener {
	
	String queues = null;
	private ATTIAMListener iamListener;
	IMMNService immnSrvc;
	protected Handler handler = new Handler();

	public APIGetNotificationConnectionDetails(String queues, IMMNService immnService, ATTIAMListener iamListener) {
		
		this.queues = queues;
		this.immnSrvc = immnService;
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
			try {
				notificationDetails = immnSrvc.getNotificationConnectionDetails(params[0]);
			} catch (RESTException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return notificationDetails;
		}

		@Override
		protected void onPostExecute(NotificationConnectionDetails notificationDetails) {
			// TODO Auto-generated method stub
			super.onPostExecute(notificationDetails);
			if(null != notificationDetails) {
				onSuccess((NotificationConnectionDetails) notificationDetails);
			} else {
				onError((NotificationConnectionDetails) notificationDetails);
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
	public void onError(final Object error) {
		// TODO Auto-generated method stub
		handler.post(new Runnable() {
			public void run() {
				if(null != iamListener) {
					iamListener.onError((Exception) error);
				}
				
			}
		});
		
	}
	

}
