package com.att.api.immn.service;

import java.text.ParseException;

import org.json.JSONException;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.att.api.immn.listener.ATTIAMListener;
import com.att.api.rest.RESTException;

public class APISendMessage implements ATTIAMListener {

	String address = null;
	String message = null;
	private ATTIAMListener iamListener;
	IMMNService immnSrvc;
	protected Handler handler = new Handler();

	public APISendMessage(String address, String message,
			IMMNService immnService, ATTIAMListener iamListener) {

		this.address = address;
		this.message = message;
		this.iamListener = iamListener;
		this.immnSrvc = immnService;
	}

	public void SendMessage() {

		SendMessageTask sendMessageTask = new SendMessageTask();
		sendMessageTask.execute(address, message);
	}

	public class SendMessageTask extends AsyncTask<String, Void, SendResponse> {

		@Override
		protected SendResponse doInBackground(String... params) {
			SendResponse resp = null;
			try {
				resp = immnSrvc.sendMessage(params[0], params[1]);
			} catch (RESTException e) {
				onError(e);
			} catch (JSONException e) {
				onError(e);
			} catch (ParseException e) {
				onError(e);
			}
			return resp;
		}

		@Override
		protected void onPostExecute(SendResponse response) {

			super.onPostExecute(response);
			onSuccess((SendResponse) response);
		}
	}

	@Override
	public void onSuccess(final Object response) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				if (null != iamListener) {
					iamListener.onSuccess((SendResponse) response);
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
					iamListener.onError((Exception) error);
				}
			}
		});
	}
}
