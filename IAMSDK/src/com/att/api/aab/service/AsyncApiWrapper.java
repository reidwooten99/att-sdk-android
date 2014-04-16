package com.att.api.aab.service;

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

public class AsyncApiWrapper {
	
	private ATTIAMListener iamListener;
	private AABService aabSrvc;
	
	public AsyncApiWrapper(AABService aabService, ATTIAMListener iamListener) {
		this.iamListener = iamListener;
		this.aabSrvc = aabService;
	}
	
	public void GetContacts(GetContactParams contactParams) {
		GetContactsTask getContactsTask = new GetContactsTask();
		getContactsTask.execute(contactParams);
	}
	
	public class  GetContactsTask extends AsyncTask<GetContactParams, Void, ContactResultSet> {
		@Override
		protected ContactResultSet doInBackground(GetContactParams... params) {
			ContactResultSet contactResultSet = null;
			InAppMessagingError errorObj = new InAppMessagingError();

			try {
				contactResultSet = aabSrvc.getContacts(
								params[0].getxFields(), //xFields
							    params[0].getPageParams(), //PageParams
							    params[0].getSearchParams() //SearchParams 
							    );
			} catch (RESTException e) {
				errorObj = Utils.CreateErrorObjectFromException( e );
				if (null != iamListener) {
					iamListener.onError(errorObj);
				}
			} catch (ParseException e) {
				errorObj = new InAppMessagingError(e.getMessage());
				if (null != iamListener) {
					iamListener.onError(errorObj);
				}
			}
			
			return contactResultSet;
		}

		@Override
		protected void onPostExecute(ContactResultSet contactResultSet) {
			super.onPostExecute(contactResultSet);
			if( null != contactResultSet ) {
				if (null != iamListener) {
					iamListener.onSuccess(contactResultSet);
				}
			}			
		}		
	}
}
