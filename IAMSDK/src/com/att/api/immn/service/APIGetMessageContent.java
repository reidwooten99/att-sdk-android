package com.att.api.immn.service;

import android.os.AsyncTask;
import android.os.Handler;

import com.att.api.immn.listener.ATTIAMListener;
import com.att.api.rest.RESTException;

public class APIGetMessageContent implements ATTIAMListener {
	
	String messageId = null;
	String partNumber = null;
	private ATTIAMListener iamListener;
	IMMNService immnSrvc;
	protected Handler handler = new Handler();

	public APIGetMessageContent (String msgId, String partNumber, IMMNService immnService, 
								 ATTIAMListener iamListener) {
		this.messageId = msgId;
		this.partNumber = partNumber;
		this.immnSrvc = immnService;
		this.iamListener = iamListener;		
	}
	
	public void GetMessageContent() {
		GetMessageContentTask getMessageContentTask = new GetMessageContentTask();
		getMessageContentTask.execute(messageId, partNumber);
	}
	
	public class GetMessageContentTask extends AsyncTask<String, Void, MessageContent> {

		@Override
		protected MessageContent doInBackground(String... params) {
			// TODO Auto-generated method stub
			MessageContent msgContent = null;
			try {
				msgContent = immnSrvc.getMessageContent(params[0], params[1]);
			} catch (RESTException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return msgContent;
		}

		@Override
		protected void onPostExecute(MessageContent result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			onSuccess(result);
		}
		
	}
	

	@Override
	public void onSuccess(Object adViewResponse) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onError(Object error) {
		// TODO Auto-generated method stub
		
	}

}
