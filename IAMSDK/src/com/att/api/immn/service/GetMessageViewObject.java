package com.att.api.immn.service;

import java.text.ParseException;

import org.json.JSONException;
import org.json.JSONObject;

import com.att.api.rest.APIResponse;
import com.att.api.rest.RESTClient;
import com.att.api.rest.RESTException;
import com.att.api.service.APIService;

import android.os.AsyncTask;
import android.widget.TextView;

public class GetMessageViewObject implements GetMessageResponse {
	String msgId = null;
	IMMNService immnSrvc;
	TextView messageTextView;

	
	public GetMessageViewObject(String msgId) {
	// TODO Auto-generated constructor stub
		this.msgId = msgId;		
	}

	public TextView GetMessage(String msgId) {
		
		GetMessageTask getMessageTask = new GetMessageTask();
		getMessageTask.execute(msgId);
		return messageTextView;

	}
	public class  GetMessageTask extends AsyncTask<String,Void,Message> {

		@Override
		protected Message doInBackground(String... msgId) {
			// TODO Auto-generated method stub
			Message m = null ;
			try {
				 m = immnSrvc.getMessage(msgId[0]);
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
			return m;
		}

		@Override
		protected void onPostExecute(Message result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			messageTextView.setText(result.getText());
		}
		
	}

	
}
