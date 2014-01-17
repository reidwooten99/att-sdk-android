package com.att.api.immn.service;

import java.text.ParseException;

import org.json.JSONException;

import android.os.AsyncTask;
import android.os.Handler;

import com.att.api.immn.listener.ATTIAMListener;
import com.att.api.rest.RESTException;

public class APIGetDelta implements ATTIAMListener {
	
	String state = null;
	private ATTIAMListener iamListener;
	IMMNService immnSrvc;
	protected Handler handler = new Handler();

	public APIGetDelta(String state, IMMNService immnService, ATTIAMListener iamListener) {
		this.state = state;
		this.immnSrvc = immnService;
		this.iamListener = iamListener;
	}

	public void GetDelta() {
		GetDeltaTask getDelta =  new GetDeltaTask();
		getDelta.execute(state);		
	}
	public class GetDeltaTask extends AsyncTask<String, Void, DeltaResponse> {

		@Override
		protected DeltaResponse doInBackground(String... params) {
			// TODO Auto-generated method stub
			DeltaResponse deltaResponse = null;
			try {
				deltaResponse = immnSrvc.getDelta(params[0]);
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
			return deltaResponse;
		}

		@Override
		protected void onPostExecute(DeltaResponse deltaResponse) {
			// TODO Auto-generated method stub
			super.onPostExecute(deltaResponse);
			if(null != deltaResponse) {
				onSuccess((DeltaResponse) deltaResponse);
			} else {
				onError((DeltaResponse) deltaResponse);
			}
			
		}
		
	}

	@Override
	public void onSuccess(final Object deltaResponse) {
		// TODO Auto-generated method stub
		handler.post(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(null != iamListener) {
					iamListener.onSuccess((DeltaResponse) deltaResponse);
				}
			}
		});
	}

	@Override
	public void onError(final Object error) {
		// TODO Auto-generated method stub
		handler.post(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(null != iamListener) {
					iamListener.onError((Exception) error);
				}
			}
		});
		
	}

}
