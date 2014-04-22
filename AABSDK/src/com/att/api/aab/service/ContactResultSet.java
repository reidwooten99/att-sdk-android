package com.att.api.aab.service;

/* vim: set expandtab tabstop=4 shiftwidth=4 softtabstop=4: */

/*
 * ====================================================================
 * LICENSE: Licensed by AT&T under the 'Software Development Kit Tools
 * Agreement.' 2014.
 * TERMS AND CONDITIONS FOR USE, REPRODUCTION, AND DISTRIBUTIONS:
 * http://developer.att.com/sdk_agreement/
 *
 * Copyright 2014 AT&T Intellectual Property. All rights reserved.
 * For more information contact developer.support@att.com
 * ====================================================================
 */

//package com.att.api.aab.service;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public final class ContactResultSet {
    private  int totalRecords;
    private  int totalPages;
    private  int currentPageIndex;
    private  int previousPage;
    private  int nextPage;
    private Contact[] contacts;
    private QuickContact[] quickContacts;

    private ContactResultSet(JSONObject jobj) {
        try {
			this.totalRecords = jobj.getInt("totalRecords");
			this.totalPages = jobj.getInt("totalPages");
	        this.currentPageIndex = jobj.getInt("currentPageIndex");
	        this.previousPage = jobj.getInt("previousPage");
	        this.nextPage = jobj.getInt("nextPage");
	        if (jobj.has("contacts")) {
	            JSONArray jarr = jobj.getJSONObject("contacts").getJSONArray("quickContact");
	            Contact[] contacts = new Contact[jarr.length()];
	            for (int i = 0; i < jarr.length(); ++i) {
	                contacts[i] = Contact.valueOf(jarr.getJSONObject(i));
	            }
	            this.contacts = contacts;
	        }
	        if (jobj.has("quickContacts")) {
	            JSONArray jarr = jobj.getJSONObject("quickContacts").getJSONArray("quickContact");
	            QuickContact[] quickContacts = new QuickContact[jarr.length()];
	            for (int i = 0; i < jarr.length(); ++i) {
	                quickContacts[i] = QuickContact.valueOf(jarr.getJSONObject(i));
	            }
	            this.quickContacts = quickContacts;
	        }
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    }

    public int getTotalRecords() {
        return totalRecords;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getCurrentPageIndex() {
        return currentPageIndex;
    }

    public int getPreviousPage() {
        return previousPage;
    }

    public int getNextPage() {
        return nextPage;
    }

    public Contact[] getContacts() {
        return contacts;
    }

    public QuickContact[] getQuickContacts() {
        return quickContacts;
    }

    public static ContactResultSet valueOf(JSONObject jobj) {
        return new ContactResultSet(jobj);
    }

}
