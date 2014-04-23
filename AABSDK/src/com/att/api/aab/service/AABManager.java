package com.att.api.aab.service;

import com.att.api.aab.service.AsyncAabWrapper;
import com.att.api.aab.listener.ATTIAMListener;
import com.att.api.oauth.OAuthToken;

public class AABManager {	
	public static AABService aabService = null;
	private ATTIAMListener iamListener = null;
	
	public AABManager(String fqdn, OAuthToken token, ATTIAMListener iamListener) {
		
		aabService = new AABService(fqdn, token);
		this.iamListener = iamListener;
	}
	
	public void CreateContact(Contact contact) {
		AsyncAabWrapper asyncApiWrapper = new AsyncAabWrapper(aabService, iamListener);
		asyncApiWrapper.CreateContact(contact);
	}
	
	public void GetContacts(String xFields, PageParams pParams, SearchParams sParams) {
		GetContactParams contactParams;
		contactParams = new GetContactParams(xFields, pParams, sParams);
		AsyncAabWrapper asyncApiWrapper = new AsyncAabWrapper(aabService, iamListener);
		asyncApiWrapper.GetContacts(contactParams);
	}
	
	public void GetContact(String contactId, String xFields) {
		AsyncAabWrapper asyncApiWrapper = new AsyncAabWrapper(aabService, iamListener);
		asyncApiWrapper.GetContact(contactId, xFields);
	}
	
	public void GetContactGroups(String contactId, PageParams params) {
		AsyncAabWrapper asyncApiWrapper = new AsyncAabWrapper(aabService, iamListener);
		asyncApiWrapper.GetContactGroups(contactId, params.getOrder(), params.getOrderBy(),
											params.getLimit(), params.getOffset());
	}
		
	public void UpdateContact(Contact contact) {
		AsyncAabWrapper asyncApiWrapper = new AsyncAabWrapper(aabService, iamListener);
		asyncApiWrapper.UpdateContact(contact);
	}
	
	public void DeleteContact(String contactId) {
		AsyncAabWrapper asyncApiWrapper = new AsyncAabWrapper(aabService, iamListener);
		asyncApiWrapper.DeleteContact(contactId);
	}
	
	public void CreateGroup(Group group) {
		AsyncAabWrapper asyncApiWrapper = new AsyncAabWrapper(aabService, iamListener);
		asyncApiWrapper.CreateGroup(group);
	}
}
