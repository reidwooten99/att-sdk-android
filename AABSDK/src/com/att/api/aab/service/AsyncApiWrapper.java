package com.att.api.aab.service;

import java.text.ParseException;

import android.os.AsyncTask;

import com.att.api.error.InAppMessagingError;
import com.att.api.error.Utils;
import com.att.api.aab.listener.ATTIAMListener;
import com.att.api.rest.RESTException;

public class AsyncApiWrapper {
	
	private ATTIAMListener iamListener;
	private AABService aabSrvc;
	
	public AsyncApiWrapper(AABService aabService, ATTIAMListener iamListener) {
		this.iamListener = iamListener;
		this.aabSrvc = aabService;
	}
		
	public void CreateContact(Contact contact) {
		CreateContactTask createContactTask = new CreateContactTask();
		createContactTask.execute(contact);
	}
	
	public class  CreateContactTask extends AsyncTask<Contact, Void, String> {
		@Override
		protected String doInBackground(Contact... params) {
			String result = null;
			InAppMessagingError errorObj = new InAppMessagingError();

			try {
				result = aabSrvc.createContact(
								params[0] //contact
							    );
			} catch (RESTException e) {
				errorObj = Utils.CreateErrorObjectFromException( e );
				if (null != iamListener) {
					iamListener.onError(errorObj);
				}
			}
			
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if( null != result ) {
				if (null != iamListener) {
					iamListener.onSuccess(result);
				}
			}			
		}		
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
	
	public void GetContact(String contactId, String xFields) {
		GetContactTask getContactTask = new GetContactTask();
		getContactTask.execute(contactId, xFields);
	}
	
	public class  GetContactTask extends AsyncTask<String, Void, ContactWrapper> {
		@Override
		protected ContactWrapper doInBackground(String... params) {
			ContactWrapper result = null;
			InAppMessagingError errorObj = new InAppMessagingError();

			try {
				result = aabSrvc.getContact(
								params[0], //contactId
							    params[1] //xFields 
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
			
			return result;
		}

		@Override
		protected void onPostExecute(ContactWrapper result) {
			super.onPostExecute(result);
			if( null != result ) {
				if (null != iamListener) {
					iamListener.onSuccess(result);
				}
			}			
		}		
	}
	
	public void GetContactGroups(String contactId, String pageOrder, String pageOrderBy, String pageLimit, String pageOffset) {
		GetContactGroupsTask getContactGroupsTask = new GetContactGroupsTask();
		getContactGroupsTask.execute(contactId, pageOrder, pageOrderBy, pageLimit, pageOffset);
	}
	
	public class  GetContactGroupsTask extends AsyncTask<String, Void, GroupResultSet> {
		@Override
		protected GroupResultSet doInBackground(String... params) {
			GroupResultSet result = null;
			InAppMessagingError errorObj = new InAppMessagingError();

			try {
				PageParams pageParams = new PageParams(params[1], params[2], params[3], params[4]);
				result = aabSrvc.getContactGroups(
								params[0], //contactId
								pageParams //pageParams 
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
			
			return result;
		}
		
		@Override
		protected void onPostExecute(GroupResultSet result) {
			super.onPostExecute(result);
			if( null != result ) {
				if (null != iamListener) {
					iamListener.onSuccess(result);
				}
			}			
		}		
	}

	public void UpdateContact(Contact contact) {
		UpdateContactTask task = new UpdateContactTask();
		task.execute(contact);
	}
	
	public class  UpdateContactTask extends AsyncTask<Contact, Void, String> {
		@Override
		protected String doInBackground(Contact... params) {
			InAppMessagingError errorObj = new InAppMessagingError();
			String result = null;

			try {
				aabSrvc.updateContact(
								params[0], //contact
								params[0].getContactId() //contactId
							    );
			} catch (RESTException e) {
				errorObj = Utils.CreateErrorObjectFromException( e );
				if (null != iamListener) {
					iamListener.onError(errorObj);
				}
			}
			
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if( null != result ) {
				if (null != iamListener) {
					iamListener.onSuccess(result);
				}
			}			
		}		
	}

	public void DeleteContact(String contactId) {
		DeleteContactTask task = new DeleteContactTask();
		task.execute(contactId);
	}
	
	public class  DeleteContactTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... params) {
			InAppMessagingError errorObj = new InAppMessagingError();
			String result = null;

			try {
				aabSrvc.deleteContact(
								params[0] //contactId
							    );
			} catch (RESTException e) {
				errorObj = Utils.CreateErrorObjectFromException( e );
				if (null != iamListener) {
					iamListener.onError(errorObj);
				}
			}
			
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if( null != result ) {
				if (null != iamListener) {
					iamListener.onSuccess(result);
				}
			}			
		}		
	}
	
	public void CreateGroup(Group group) {
		CreateGroupTask task = new CreateGroupTask();
		task.execute(group);
	}
	
	public class  CreateGroupTask extends AsyncTask<Group, Void, String> {
		@Override
		protected String doInBackground(Group... params) {
			String result = null;
			InAppMessagingError errorObj = new InAppMessagingError();
	
			try {
				result = aabSrvc.createGroup(
								params[0] //group
							    );
			} catch (RESTException e) {
				errorObj = Utils.CreateErrorObjectFromException( e );
				if (null != iamListener) {
					iamListener.onError(errorObj);
				}
			}
			
			return result;
		}
	
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if( null != result ) {
				if (null != iamListener) {
					iamListener.onSuccess(result);
				}
			}			
		}		
	}
}
