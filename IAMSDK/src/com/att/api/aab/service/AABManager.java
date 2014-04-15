package com.att.api.aab.service;

import com.att.api.immn.listener.ATTIAMListener;
import com.att.api.oauth.OAuthToken;

public class AABManager {
	
	public static AABService aabService;
	private ATTIAMListener iamListener;
	
	public AABManager(String fqdn, OAuthToken token, ATTIAMListener iamListener) {
		
		aabService = new AABService(fqdn, token);
		this.iamListener = iamListener;
	}
	
	public void GetContacts(String xFields, PageParams pParams,SearchParams sParams) {		
		APIGetContacts getContacts = new APIGetContacts(xFields, pParams, sParams, aabService, iamListener);
		getContacts.GetContacts();
	}

}
