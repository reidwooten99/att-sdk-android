package com.att.api.immn.service;

import org.json.JSONException;

import android.os.AsyncTask;
import android.os.Handler;

import com.att.api.immn.listener.ATTIAMListener;
import com.att.api.rest.RESTException;

public class APIUpdateMessages implements ATTIAMListener {
	
	DeltaChange[] messages = null;
	Boolean isSuccesful = false;
	private ATTIAMListener iamListener;
	IMMNService immnSrvc;
	protected Handler handler = new Handler();
	
	public APIUpdateMessages(DeltaChange[] messages, IMMNService immnSrvc, ATTIAMListener iamListener) {
		this.messages = messages;
		this.isSuccesful = isSuccesful;
		this.immnSrvc = immnSrvc;
		this.iamListener = iamListener;	
	}
	
	public void UpdateMessages() {
		APIUpdateMessagesTask updateMessagestask = new APIUpdateMessagesTask();
		updateMessagestask.execute(messages);
	}

	public class APIUpdateMessagesTask extends AsyncTask<DeltaChange, Void, Boolean> {

		@Override
		protected Boolean doInBackground(DeltaChange... messages) {
			// TODO Auto-generated method stub
			try {
				immnSrvc.updateMessages(messages);
				isSuccesful = true;
			} catch (RESTException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
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
