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

public class AsyncApiWrapper implements ATTIAMListener {
	
	private ATTIAMListener iamListener;
	//private GetContactParams contactParams;
	//private String xFields;
	AABService aabSrvc;
	protected Handler handler = new Handler();
	
	public AsyncApiWrapper(AABService aabService, ATTIAMListener iamListener) {

		//this.xFields = xFields;
		//this.contactParams = new GetContactParams(xFields, pageParams, searchParams);		
		this.iamListener = iamListener;
		this.aabSrvc = aabService;
	}
	
	
	public void GetContacts(GetContactParams contactParams) {
		GetContactsTask getContactsTask = new GetContactsTask();
		getContactsTask.execute(contactParams);
	}
	
	public class  GetContactsTask extends AsyncTask<GetContactParams, Void, String> {

		@Override
		//protected ContactResultSet doInBackground(GetContactParams... params) {
		protected String doInBackground(GetContactParams... params) {
			// TODO Auto-generated method stub
			//ContactResultSet contactResultSet = null;
			String strResult = null;
			InAppMessagingError errorObj = new InAppMessagingError();

			try {
				//contactResultSet = aabSrvc.getContacts(
				strResult = aabSrvc.getContactsResponse(
								params[0].getxFields(), //xFields
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
			
			return strResult; // contactResultSet;
		}

		@Override
		protected void onPostExecute(String contactResultSet) {
			// TODO Auto-generated method stub
			super.onPostExecute(contactResultSet);
			if( null != contactResultSet ) {
				// onSuccess(contactResultSet); // SM Note: This is not working.
				if (null != iamListener) {
			        JSONObject jrs = null;
					try {
						jrs = new JSONObject(contactResultSet).getJSONObject("resultSet");
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					iamListener.onSuccess(ContactResultSet.valueOf(jrs));
				}
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
			        JSONObject jrs = null;
					try {
						jrs = new JSONObject(contactResultSet).getJSONObject("resultSet");
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					iamListener.onSuccess(ContactResultSet.valueOf(jrs));
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
