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

public class APIGetContacts implements ATTIAMListener {
	
	private ATTIAMListener iamListener;
	private GetContactParams contactParams;
	private String xFields;
	AABService aabSrvc;
	protected Handler handler = new Handler();
	
	public APIGetContacts(String xFields, PageParams pageParams, SearchParams searchParams,
						  AABService aabService, ATTIAMListener iamListener) {

		this.xFields = xFields;
		this.contactParams = new GetContactParams(xFields, pageParams, searchParams);		
		this.iamListener = iamListener;
		this.aabSrvc = aabService;
	}
	
	
	public void GetContacts() {
		GetContactsTask getContactsTask = new GetContactsTask();
		getContactsTask.execute(contactParams);
	}
	
	public class  GetContactsTask extends AsyncTask<GetContactParams, Void, ContactResultSet> {

		@Override
		protected ContactResultSet doInBackground(GetContactParams... params) {
			// TODO Auto-generated method stub
			ContactResultSet contactResultSet = null;
			InAppMessagingError errorObj = new InAppMessagingError();

			try {
				contactResultSet = aabSrvc.getContacts(params[0].getxFields(), //xFields
													   params[0].getPageParams(), //PageParams
													   params[0].getSearchParams() //SearchParams 
													   );
			} catch (RESTException e) {
				errorObj = Utils.CreateErrorObjectFromException( e );
				onError( errorObj );
			} catch (ParseException e) {
				errorObj = new InAppMessagingError(e.getMessage());
				onError(errorObj);		
			}
			
			return contactResultSet;
		}

		@Override
		protected void onPostExecute(ContactResultSet contactResultSet) {
			// TODO Auto-generated method stub
			super.onPostExecute(contactResultSet);
			if( null != contactResultSet ) {
				onSuccess((ContactResultSet) contactResultSet);
			}
			
		}
		
	}

	@Override
	public void onSuccess(final Object contactResultSet) {
		// TODO Auto-generated method stub
		handler.post(new Runnable() {

			@Override
			public void run() {
				if (null != iamListener) {
					iamListener.onSuccess((ContactResultSet) contactResultSet);
				}
			}
			
		});
		
	}

	@Override
	public void onError(final InAppMessagingError error) {
/*		String serviceExceptionId = null;
		
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
		}	*/
					
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
