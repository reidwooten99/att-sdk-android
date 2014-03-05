package com.att.api.immn.service;

import java.text.ParseException;

import org.json.JSONException;

import android.os.AsyncTask;
import android.os.Handler;

import com.att.api.error.InAppMessagingError;
import com.att.api.error.Utils;
import com.att.api.immn.listener.ATTIAMListener;
import com.att.api.rest.RESTException;

public class APIUpdateMessage  implements ATTIAMListener{
	
		public class APIUpdateMessageParams {
		
		String msgId;
		Boolean isUnread;
		Boolean isFavorite;	
	
		public APIUpdateMessageParams() {
			
			this.msgId = null;
			this.isUnread = false;
			this.isFavorite = false;
		}
		
		public APIUpdateMessageParams(String msgId, Boolean isUnread, Boolean isFavorite) {

			this.msgId = msgId;
			this.isUnread = isUnread;
			this.isFavorite = isFavorite;
		}

	}
	APIUpdateMessageParams params = null;
	private ATTIAMListener iamListener;
	IMMNService immnSrvc;
	protected Handler handler = new Handler();

	public APIUpdateMessage() {
		
		this.params = null;
		this.immnSrvc = null;
		this.iamListener = null;
		
	}
	public void set(APIUpdateMessageParams params, IMMNService immnSrvc, ATTIAMListener iamListener ) {
		
		this.params = params;
		this.immnSrvc = immnSrvc;
		this.iamListener = iamListener;
	}
	
	public void UpdateMessage() {
		UpdateMessageTask updateMessageTask = new UpdateMessageTask();
		updateMessageTask.execute(params);
		
	}
	
	public class UpdateMessageTask extends AsyncTask<APIUpdateMessageParams, Void, Boolean> {

		@Override
		protected Boolean doInBackground(APIUpdateMessageParams... params) {
			// TODO Auto-generated method stub
			Boolean isSuccesful = false;
			InAppMessagingError errorObj = new InAppMessagingError();

			try {
				immnSrvc.updateMessage(params[0].msgId,
									   params[0].isFavorite,
									   params[0].isUnread );
				isSuccesful = true;
			} catch (RESTException e) {
				errorObj = Utils.CreateErrorObjectFromException( e );
				onError( errorObj );
			} catch (JSONException e) {
				errorObj.setErrorMessage(e.getMessage());
				onError(errorObj);			
			}
			return isSuccesful;
		}

		@Override
		protected void onPostExecute(Boolean isSuccesful) {
			// TODO Auto-generated method stub
			super.onPostExecute(isSuccesful);
			if(isSuccesful) {
				onSuccess((Boolean) isSuccesful);
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
