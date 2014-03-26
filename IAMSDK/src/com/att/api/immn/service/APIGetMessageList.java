package com.att.api.immn.service;

import java.text.ParseException;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.att.api.error.InAppMessagingError;
import com.att.api.error.Utils;
import com.att.api.immn.listener.ATTIAMListener;
import com.att.api.rest.RESTException;

public class APIGetMessageList implements ATTIAMListener {
	int limit = 0;
	int offset = 0;
	private ATTIAMListener iamListener;
	IMMNService immnSrvc;
	protected Handler handler = new Handler();
	
	public APIGetMessageList(int limit, int offset,
			IMMNService immnService, ATTIAMListener iamListener) {

		this.limit = limit;
		this.offset = offset;
		this.iamListener = iamListener;
		this.immnSrvc = immnService;
	}
	
	public void GetMessageList() {
		GetMessageListTask getMessageListTask = new GetMessageListTask();
		getMessageListTask.execute(limit,offset);
	}
	
	public class  GetMessageListTask extends AsyncTask<Integer, Void, MessageList> {

		@Override
		protected MessageList doInBackground(Integer... params) {
			// TODO Auto-generated method stub
			MessageList messageList = null;
			InAppMessagingError errorObj = new InAppMessagingError();

			try {
				messageList = immnSrvc.getMessageList(params[0],params[1]);
			} catch (RESTException e) {
				errorObj = Utils.CreateErrorObjectFromException( e );
				onError( errorObj );
			} catch (JSONException e) {
				errorObj = new InAppMessagingError(e.getMessage());
				onError(errorObj);			
			} catch (ParseException e) {
				errorObj = new InAppMessagingError(e.getMessage());
				onError(errorObj);		
			}
			return messageList;
		}

		@Override
		protected void onPostExecute(MessageList messageList) {
			// TODO Auto-generated method stub
			super.onPostExecute(messageList);
			if( null != messageList ) {
				onSuccess((MessageList) messageList);
			}
			
		}
		
	}

	@Override
	public void onSuccess(final Object messageList) {
		// TODO Auto-generated method stub
		handler.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (null != iamListener) {
					iamListener.onSuccess((MessageList) messageList);
				}
			}
			
		});
		
	}

	@Override
	public void onError(final InAppMessagingError error) {
		// TODO Auto-generated method stub
		String serviceExceptionId = null;
		
		if(null  != error) {
			if(error.getErrorMessage().contains("ServiceException")) {
				JSONObject jobj;
				MessageIndexInfo messageIndexInfo;
				try {
					jobj = new JSONObject( error.getErrorMessage());
					JSONObject jobj1 = jobj.getJSONObject("RequestError");
					JSONObject jobj2 = jobj1.getJSONObject("ServiceException");
					serviceExceptionId = jobj2.getString("MessageId");
					if (serviceExceptionId == "SVC0001") {
						messageIndexInfo = immnSrvc.getMessageIndexInfo();	
						if(messageIndexInfo.getState() == "NOT_INITIALIZED" || 
						   messageIndexInfo.getState() == "ERROR") {
							immnSrvc.createMessageIndex();
						}
					}
				} catch (JSONException e1) {
				// TODO Auto-generated catch block
				//e1.printStackTrace();
					Log.i("JSONException", "ONException");
				} catch (RESTException e) {
				// TODO Auto-generated catch block
					e.printStackTrace();
				}  catch (ParseException e) {
				// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}	
					
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
