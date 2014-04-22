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

package com.att.api.aab.service;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public final class GroupResultSet {
    private int totalRecords = 0;
    private  int totalPages = 0 ;
    private  int currentPageIndex = 0;
    private  int previousPage = 0;
    private  int nextPage = 0;
    private Group[] groups = null;

    private GroupResultSet(JSONObject jobj) {
        try {
			this.totalRecords = jobj.getInt("totalRecords");
			this.totalPages = jobj.getInt("totalPages");
	        this.currentPageIndex = jobj.getInt("currentPageIndex");
	        this.previousPage = jobj.getInt("previousPage");
	        this.nextPage = jobj.getInt("nextPage");
	        if (jobj.has("groups")) {
	            JSONArray jarr = jobj.getJSONObject("groups").getJSONArray("group");
	            Group[] groups = new Group[jarr.length()];
	            for (int i = 0; i < jarr.length(); ++i) {
	                groups[i] = Group.valueOf(jarr.getJSONObject(i));
	            }
	            this.groups = groups;
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

    public Group[] getGroups() {
        // TODO: return copy
        return this.groups;
    }

    public static GroupResultSet valueOf(JSONObject jobj) {
        return new GroupResultSet(jobj);
    }

}
