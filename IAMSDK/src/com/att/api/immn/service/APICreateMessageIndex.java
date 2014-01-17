package com.att.api.immn.service;

import android.os.AsyncTask;
import android.os.Handler;

import com.att.api.immn.listener.ATTIAMListener;
import com.att.api.rest.RESTException;

public class APICreateMessageIndex implements ATTIAMListener {
	
	Boolean isSuccessful = false;
	private ATTIAMListener iamListener;
	IMMNService immnSrvc;
	protected Handler handler = new Handler();
	
	public APICreateMessageIndex(IMMNService immnService, ATTIAMListener iamListener) {
		
		this.immnSrvc = immnService;
		this.iamListener = iamListener;
	}
	
	public void CreateMessageIndex() {
		
		CreateMessageIndexTask createMessageIndexTask = new CreateMessageIndexTask();
		createMessageIndexTask.execute();
	}
	
	public class CreateMessageIndexTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO Auto-generated method stub
			Boolean isSuccesful = false;
			try {
				immnSrvc.createMessageIndex();
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
			if(isSuccesful) {
				onSuccess((Boolean) isSuccesful);
			} else {
				onError((Boolean) isSuccesful);
			}
			
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
