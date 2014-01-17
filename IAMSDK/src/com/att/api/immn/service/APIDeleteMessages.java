package com.att.api.immn.service;

import android.os.AsyncTask;
import android.os.Handler;

import com.att.api.immn.listener.ATTIAMListener;
import com.att.api.rest.RESTException;

public class APIDeleteMessages implements ATTIAMListener{
	
	String[] msgIds = null;
	Boolean isSuccesful = false;
	private ATTIAMListener iamListener;
	IMMNService immnSrvc;
	protected Handler handler = new Handler();


	public APIDeleteMessages(String msgIds[], IMMNService immnService, ATTIAMListener iamListener) {
		
		this.msgIds = msgIds;
		this.immnSrvc = immnService;
		this.iamListener = iamListener;			
	}
	
	public void DeleteMessages() {
		DeleteMessagestask deleteMessagesTask = new DeleteMessagestask();
		deleteMessagesTask.execute(msgIds);	
	}
	
	public class DeleteMessagestask extends AsyncTask<String[], Void, Boolean> {

		@Override
		protected Boolean doInBackground(String[]... msgIds) {
			// TODO Auto-generated method stub
			Boolean isSuccesful = false;
			try {
				immnSrvc.deleteMessages(msgIds[0]);
				isSuccesful = true;
			} catch (RESTException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return isSuccesful;
		}

		@Override
		protected void onPostExecute(Boolean isSuccesful) {
			// TODO Auto-generated method stub
			super.onPostExecute(isSuccesful);
			if(isSuccesful)
				onSuccess((Boolean) isSuccesful);
			else
				onError((Boolean) isSuccesful);
		}
		
	}
	@Override
	public void onSuccess(final Object isSuccesful) {
		// TODO Auto-generated method stub
		handler.post(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
		    	 if(null != iamListener) { 
					iamListener.onSuccess((Boolean) isSuccesful);
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
