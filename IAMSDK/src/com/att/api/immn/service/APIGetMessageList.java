package com.att.api.immn.service;

import java.text.ParseException;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Handler;

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
				if (null != iamListener) {
					iamListener.onSuccess((MessageList) messageList);
				}
			}
			
		});
		
	}

	@Override
	public void onError(final InAppMessagingError error) {
		String serviceExceptionId = null;
		
		if(null  != error) {
			if(error.getHttpResponse().contains("ServiceException")) {
				JSONObject jobj;
				MessageIndexInfo messageIndexInfo;
				try {
					jobj = new JSONObject( error.getHttpResponse());
					JSONObject jobj1 = jobj.getJSONObject("RequestError");
					JSONObject jobj2 = jobj1.getJSONObject("ServiceException");
					serviceExceptionId = jobj2.getString("MessageId");
					if (serviceExceptionId.equalsIgnoreCase("SVC0001") ) {
						messageIndexInfo = immnSrvc.getMessageIndexInfo();
						if(messageIndexInfo.getState().equalsIgnoreCase("NOT_INITIALIZED") || 
						   messageIndexInfo.getState().equalsIgnoreCase("ERROR")) {
							immnSrvc.createMessageIndex();
							GetMessageList();
						}
					}
				} catch (JSONException e1) {
					e1.printStackTrace();
				} catch (RESTException e) {
					e.printStackTrace();
				}  catch (ParseException e) {
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
