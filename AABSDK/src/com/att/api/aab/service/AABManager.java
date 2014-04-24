package com.att.api.aab.service;

import java.text.ParseException;

import android.os.AsyncTask;

import com.att.api.aab.listener.ATTIAMListener;
import com.att.api.error.InAppMessagingError;
import com.att.api.error.Utils;
import com.att.api.oauth.OAuthToken;
import com.att.api.rest.RESTException;

public class AABManager {	
	public static AABService aabService = null;
	private ATTIAMListener aabListener = null;
	
	public AABManager(String fqdn, OAuthToken token, ATTIAMListener listener) {		
		aabService = new AABService(fqdn, token);
		aabListener = listener;
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
				result = aabService.createContact(
								params[0] //contact
							    );
			} catch (RESTException e) {
				errorObj = Utils.CreateErrorObjectFromException( e );
				if (null != aabListener) {
					aabListener.onError(errorObj);
				}
			}
			
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if( null != result ) {
				if (null != aabListener) {
					aabListener.onSuccess(result);
				}
			}			
		}		
	}

	public void GetContacts(String xFields, PageParams pParams, SearchParams sParams) {
		GetContactParams contactParams;
		contactParams = new GetContactParams(xFields, pParams, sParams);
		GetContactsTask task = new GetContactsTask();
		task.execute(contactParams);
	}
	
	public class  GetContactsTask extends AsyncTask<GetContactParams, Void, ContactResultSet> {
		@Override
		protected ContactResultSet doInBackground(GetContactParams... params) {
			ContactResultSet contactResultSet = null;
			InAppMessagingError errorObj = new InAppMessagingError();

			try {
				contactResultSet = aabService.getContacts(
								params[0].getxFields(), //xFields
							    params[0].getPageParams(), //PageParams
							    params[0].getSearchParams() //SearchParams 
							    );
			} catch (RESTException e) {
				errorObj = Utils.CreateErrorObjectFromException( e );
				if (null != aabListener) {
					aabListener.onError(errorObj);
				}
			} catch (ParseException e) {
				errorObj = new InAppMessagingError(e.getMessage());
				if (null != aabListener) {
					aabListener.onError(errorObj);
				}
			}
			
			return contactResultSet;
		}

		@Override
		protected void onPostExecute(ContactResultSet contactResultSet) {
			super.onPostExecute(contactResultSet);
			if( null != contactResultSet ) {
				if (null != aabListener) {
					aabListener.onSuccess(contactResultSet);
				}
			}			
		}		
	}
	
	public void GetContact(String contactId, String xFields) {
		GetContactTask task = new GetContactTask();
		task.execute(contactId, xFields);
	}
	
	public class  GetContactTask extends AsyncTask<String, Void, ContactWrapper> {
		@Override
		protected ContactWrapper doInBackground(String... params) {
			ContactWrapper result = null;
			InAppMessagingError errorObj = new InAppMessagingError();

			try {
				result = aabService.getContact(
								params[0], //contactId
							    params[1] //xFields 
							    );
			} catch (RESTException e) {
				errorObj = Utils.CreateErrorObjectFromException( e );
				if (null != aabListener) {
					aabListener.onError(errorObj);
				}
			} catch (ParseException e) {
				errorObj = new InAppMessagingError(e.getMessage());
				if (null != aabListener) {
					aabListener.onError(errorObj);
				}
			}
			
			return result;
		}

		@Override
		protected void onPostExecute(ContactWrapper result) {
			super.onPostExecute(result);
			if( null != result ) {
				if (null != aabListener) {
					aabListener.onSuccess(result);
				}
			}			
		}		
	}
	
	public void GetContactGroups(String contactId, PageParams params) {
		GetContactGroupsTask getContactGroupsTask = new GetContactGroupsTask();
		getContactGroupsTask.execute(contactId, params.getOrder(), params.getOrderBy(),
				params.getLimit(), params.getOffset());
	}
	
	public class  GetContactGroupsTask extends AsyncTask<String, Void, GroupResultSet> {
		@Override
		protected GroupResultSet doInBackground(String... params) {
			GroupResultSet result = null;
			InAppMessagingError errorObj = new InAppMessagingError();

			try {
				PageParams pageParams = new PageParams(params[1], params[2], params[3], params[4]);
				result = aabService.getContactGroups(
								params[0], //contactId
								pageParams //pageParams 
							    );
			} catch (RESTException e) {
				errorObj = Utils.CreateErrorObjectFromException( e );
				if (null != aabListener) {
					aabListener.onError(errorObj);
				}
			} catch (ParseException e) {
				errorObj = new InAppMessagingError(e.getMessage());
				if (null != aabListener) {
					aabListener.onError(errorObj);
				}
			}
			
			return result;
		}
		
		@Override
		protected void onPostExecute(GroupResultSet result) {
			super.onPostExecute(result);
			if( null != result ) {
				if (null != aabListener) {
					aabListener.onSuccess(result);
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
				aabService.updateContact(
								params[0], //contact
								params[0].getContactId() //contactId
							    );
			} catch (RESTException e) {
				errorObj = Utils.CreateErrorObjectFromException( e );
				if (null != aabListener) {
					aabListener.onError(errorObj);
				}
			}
			
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if( null != result ) {
				if (null != aabListener) {
					aabListener.onSuccess(result);
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
				aabService.deleteContact(
								params[0] //contactId
							    );
			} catch (RESTException e) {
				errorObj = Utils.CreateErrorObjectFromException( e );
				if (null != aabListener) {
					aabListener.onError(errorObj);
				}
			}
			
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if( null != result ) {
				if (null != aabListener) {
					aabListener.onSuccess(result);
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
				result = aabService.createGroup(
								params[0] //group
							    );
			} catch (RESTException e) {
				errorObj = Utils.CreateErrorObjectFromException( e );
				if (null != aabListener) {
					aabListener.onError(errorObj);
				}
			}
			
			return result;
		}
	
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if( null != result ) {
				if (null != aabListener) {
					aabListener.onSuccess(result);
				}
			}			
		}		
	}
	
	public void GetGroups(PageParams params, String groupName) {
		GetGroupsTask task = new GetGroupsTask();
		task.execute(groupName, params.getOrder(), params.getOrderBy(),
				params.getLimit(), params.getOffset());
	}
	
	public class  GetGroupsTask extends AsyncTask<String, Void, GroupResultSet> {
		@Override
		protected GroupResultSet doInBackground(String... params) {
			GroupResultSet result = null;
			InAppMessagingError errorObj = new InAppMessagingError();

			try {
				PageParams pageParams = new PageParams(params[1], params[2], params[3], params[4]);
				result = aabService.getGroups(
								pageParams, //pageParams 
								params[0] //groupName
							    );
			} catch (RESTException e) {
				errorObj = Utils.CreateErrorObjectFromException( e );
				if (null != aabListener) {
					aabListener.onError(errorObj);
				}
			} catch (ParseException e) {
				errorObj = new InAppMessagingError(e.getMessage());
				if (null != aabListener) {
					aabListener.onError(errorObj);
				}
			}
			
			return result;
		}
		
		@Override
		protected void onPostExecute(GroupResultSet result) {
			super.onPostExecute(result);
			if( null != result ) {
				if (null != aabListener) {
					aabListener.onSuccess(result);
				}
			}			
		}		
	}

	public void DeleteGroup(String groupId) {
		DeleteGroupTask task = new DeleteGroupTask();
		task.execute(groupId);
	}
	
	public class  DeleteGroupTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... params) {
			InAppMessagingError errorObj = new InAppMessagingError();
			String result = null;

			try {
				aabService.deleteGroup(
								params[0] //groupId
							    );
			} catch (RESTException e) {
				errorObj = Utils.CreateErrorObjectFromException( e );
				if (null != aabListener) {
					aabListener.onError(errorObj);
				}
			}
			
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if( null != result ) {
				if (null != aabListener) {
					aabListener.onSuccess(result);
				}
			}			
		}		
	}
	
	public void UpdateGroup(Group group) {
		UpdateGroupTask task = new UpdateGroupTask();
		task.execute(group);
	}
	
	public class  UpdateGroupTask extends AsyncTask<Group, Void, String> {
		@Override
		protected String doInBackground(Group... params) {
			String result = null;
			InAppMessagingError errorObj = new InAppMessagingError();
	
			try {
				aabService.updateGroup(
								params[0], //group
								params[0].getGroupId() //groupId
							    );
			} catch (RESTException e) {
				errorObj = Utils.CreateErrorObjectFromException( e );
				if (null != aabListener) {
					aabListener.onError(errorObj);
				}
			}
			
			return result;
		}
	
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if( null != result ) {
				if (null != aabListener) {
					aabListener.onSuccess(result);
				}
			}			
		}		
	}

	public void AddContactsToGroup(String groupId, String contactIds) {
		AddContactsToGroupTask task = new AddContactsToGroupTask();
		task.execute(groupId, contactIds);
	}
	
	public class  AddContactsToGroupTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... params) {
			InAppMessagingError errorObj = new InAppMessagingError();
			String result = null;

			try {
				aabService.addContactsToGroup(
								params[0], //groupId
								params[1]  //contactIds
							    );
			} catch (RESTException e) {
				errorObj = Utils.CreateErrorObjectFromException( e );
				if (null != aabListener) {
					aabListener.onError(errorObj);
				}
			}
			
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if( null != result ) {
				if (null != aabListener) {
					aabListener.onSuccess(result);
				}
			}			
		}		
	}

	public void RemoveContactsFromGroup(String groupId, String contactIds) {
		RemoveContactsFromGroupTask task = new RemoveContactsFromGroupTask();
		task.execute(groupId, contactIds);
	}
	
	public class  RemoveContactsFromGroupTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... params) {
			InAppMessagingError errorObj = new InAppMessagingError();
			String result = null;

			try {
				aabService.removeContactsFromGroup(
								params[0], //groupId
								params[1]  //contactIds
							    );
			} catch (RESTException e) {
				errorObj = Utils.CreateErrorObjectFromException( e );
				if (null != aabListener) {
					aabListener.onError(errorObj);
				}
			}
			
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if( null != result ) {
				if (null != aabListener) {
					aabListener.onSuccess(result);
				}
			}			
		}		
	}
	
	
	public void GetGroupContacts(String groupId, PageParams params) {
		GetGroupContactsTask task = new GetGroupContactsTask();
		task.execute(groupId, params.getOrder(), params.getOrderBy(),
				params.getLimit(), params.getOffset());
	}
	
	public class  GetGroupContactsTask extends AsyncTask<String, Void, String[]> {
		@Override
		protected String[] doInBackground(String... params) {
			String[] result = null;
			InAppMessagingError errorObj = new InAppMessagingError();

			try {
				PageParams pageParams = new PageParams(params[1], params[2], params[3], params[4]);
				result = aabService.getGroupContacts(
								params[0], //groupId
								pageParams //pageParams 
							    );
			} catch (RESTException e) {
				errorObj = Utils.CreateErrorObjectFromException( e );
				if (null != aabListener) {
					aabListener.onError(errorObj);
				}
			} catch (ParseException e) {
				errorObj = new InAppMessagingError(e.getMessage());
				if (null != aabListener) {
					aabListener.onError(errorObj);
				}
			}
			
			return result;
		}
		
		@Override
		protected void onPostExecute(String[] result) {
			super.onPostExecute(result);
			if( null != result ) {
				if (null != aabListener) {
					aabListener.onSuccess(result);
				}
			}			
		}		
	}
	
	public void GetMyInfo() {
		GetMyInfoTask task = new GetMyInfoTask();
		task.execute();
	}
	
	public class  GetMyInfoTask extends AsyncTask<Void, Void, Contact> {
		@Override
		protected Contact doInBackground(Void... params) {
			Contact result = null;
			InAppMessagingError errorObj = new InAppMessagingError();

			try {
				result = aabService.getMyInfo();
			} catch (RESTException e) {
				errorObj = Utils.CreateErrorObjectFromException( e );
				if (null != aabListener) {
					aabListener.onError(errorObj);
				}
			} catch (ParseException e) {
				errorObj = new InAppMessagingError(e.getMessage());
				if (null != aabListener) {
					aabListener.onError(errorObj);
				}
			}
			
			return result;
		}

		@Override
		protected void onPostExecute(Contact result) {
			super.onPostExecute(result);
			if( null != result ) {
				if (null != aabListener) {
					aabListener.onSuccess(result);
				}
			}			
		}		
	}

	public void UpdateMyInfo(Contact contact) {
		UpdateMyInfoTask task = new UpdateMyInfoTask();
		task.execute(contact);
	}
	
	public class  UpdateMyInfoTask extends AsyncTask<Contact, Void, String> {
		@Override
		protected String doInBackground(Contact... params) {
			InAppMessagingError errorObj = new InAppMessagingError();
			String result = null;

			try {
				aabService.updateMyInfo(
								params[0] //contact
							    );
			} catch (RESTException e) {
				errorObj = Utils.CreateErrorObjectFromException( e );
				if (null != aabListener) {
					aabListener.onError(errorObj);
				}
			}
			
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if( null != result ) {
				if (null != aabListener) {
					aabListener.onSuccess(result);
				}
			}			
		}		
	}
}
