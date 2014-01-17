package com.att.api.immn.service;

import android.os.AsyncTask;
import android.os.Handler;

import com.att.api.immn.listener.ATTIAMListener;
import com.att.api.rest.RESTException;

public class APIDeleteMessage implements ATTIAMListener {
	
	Boolean isSuccessful = false;
	String msgId = null;
	private ATTIAMListener iamListener;
	IMMNService immnSrvc;
	protected Handler handler = new Handler();
	
	public  APIDeleteMessage(String msgId, IMMNService immnService, ATTIAMListener iamListener) {
		
		this.msgId = msgId;
		this.immnSrvc = immnService;
		this.iamListener = iamListener;
	}
	public void DeleteMessage() {
		
		DeleteMessageTask deleteMessageTask = new DeleteMessageTask();
		deleteMessageTask.execute(msgId);		
	}
	
	public class DeleteMessageTask extends AsyncTask<String, Void, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {
			// TODO Auto-generated method stub
			Boolean isSuccessful = false;
			try {
				immnSrvc.deleteMessage(msgId);
				isSuccessful = true;
			} catch (RESTException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return isSuccessful;
		}

		@Override
		protected void onPostExecute(Boolean isSuccessful) {
			// TODO Auto-generated method stub
			super.onPostExecute(isSuccessful);
			if(isSuccessful) {
				onSuccess((Boolean) isSuccessful);
			} else {
				onError((Boolean) isSuccessful);
			}
			
		}
		
	}



	@Override
	public void onSuccess(final Object isSuccessful) {
		// TODO Auto-generated method stub
		handler.post(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
		    	 if(null != iamListener) { 
					iamListener.onSuccess((Boolean) isSuccessful);
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


