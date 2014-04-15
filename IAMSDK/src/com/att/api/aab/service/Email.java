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

import org.json.JSONException;
import org.json.JSONObject;

public final class Email {
    private  String type;
    private  String emailAddress;
    private  Boolean preferred;

    public Email(String type, String emailAddress, Boolean preferred) {
        this.type = type;
        this.emailAddress = emailAddress;
        this.preferred = preferred;
    }

    public String getType() {
        return type;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public Boolean getPreferred() {
        return preferred;
    }

    public JSONObject toJson() {
        JSONObject jobj = new JSONObject();

        final String[] keys = { "type", "preferred", "emailAddress" };
        String prefString = null;
        if (getPreferred() != null) {
            prefString = getPreferred() ? "TRUE" : "FALSE";
        }
        final String[] values = { getType(), prefString, getEmailAddress() };

        for (int i = 0; i < keys.length; ++i) {
            if (values[i] == null) continue;
            try {
				jobj.put(keys[i], values[i]);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }

        return jobj;
    }

    public static Email valueOf(JSONObject jobj) {
        String type;
		try {
			type = jobj.has("type") ? jobj.getString("type") : null;
			String emailAddr = jobj.has("emailAddress") ? jobj.getString("emailAddress") : null;
	        Boolean pref = jobj.has("preferred") ? jobj.getBoolean("preferred") : null;
	        return new Email(type, emailAddr, pref);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
        
    }
}
