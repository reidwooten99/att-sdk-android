package com.att.api.immn.service;

import java.text.ParseException;

import org.json.JSONException;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.att.api.immn.listener.ATTIAMListener;
import com.att.api.rest.RESTException;

public class APIGetMessage implements ATTIAMListener {

	String msgId = null;
	private ATTIAMListener iamListener;
	IMMNService immnSrvc;
	protected Handler handler = new Handler();

	public APIGetMessage(String msgId, IMMNService immnService, ATTIAMListener iamListener) {
		
		this.msgId = msgId;		
		this.iamListener = iamListener;
		this.immnSrvc = immnService;
	}

	public void GetMessage(String msgId) {
		
		GetMessageTask getMessageTask = new GetMessageTask();
		getMessageTask.execute(msgId);
	}
	
	public class  GetMessageTask extends AsyncTask<String,Void,Message> {

		@Override
		protected Message doInBackground(String... msgId) {
			Message m = null ;
			try {
				Log.d("IAMSDK", "Async Task : " +  msgId[0]);
				 m = immnSrvc.getMessage(msgId[0]);
			} catch (RESTException e) {
				onError(e);
			} catch (JSONException e) {
				onError(e);
			} catch (ParseException e) {
				onError(e);
			}
			if(null != m)
				Log.d("IAMSDK", "M not null");
			else
				Log.d("IAMSDK", "M is null");
			return m;
		}

		@Override
		protected void onPostExecute(Message result) {
			
			super.onPostExecute(result);
			onSuccess((Message)result);
		}
	}

	@Override
	public void onSuccess(final Object response) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				if (null != iamListener) {
					iamListener.onSuccess((Message)response);
				}
			}
		});
	}

	@Override
	public void onError(final Object error) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				if (null != iamListener) {
					iamListener.onError((Exception)error);
				}
			}
		});
	}
}