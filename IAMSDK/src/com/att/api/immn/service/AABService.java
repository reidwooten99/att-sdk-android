package com.att.api.immn.service;

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

import java.text.ParseException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.att.api.oauth.OAuthToken;
import com.att.api.rest.APIResponse;
import com.att.api.rest.RESTClient;
import com.att.api.rest.RESTException;
import com.att.api.service.APIService;

/**
 * Used to interact with version 1 of the Address Book API.
 *
 * <p>
 * This class is thread safe.
 * </p>
 *
 */
public class AABService extends APIService {
    
    private void addPageParams(RESTClient client, PageParams params) {
        if (params == null) return;

        final String[] keys = { "order", "orderBy", "limit", "offset" };
        final String[] values = { 
            params.getOrder(), params.getOrderBy(), params.getLimit(),
            params.getOffset() 
        };

        for (int i = 0; i < keys.length; ++i) {
            if (values[i] == null) continue;
            client.addParameter(keys[i], values[i]);
        }
    }

    private void addSearchParams(RESTClient client, SearchParams params) {
        if (params == null) return;

        final String[] keys = { 
            "formattedName", "firstName", "middleName", "lastName", "nickName",
            "organization", "email", "phone", "addressLine1", "addressLine2",
            "city", "zipcode" 
        };
        final String[] values = { 
            params.getFormattedName(), params.getFirstName(),
            params.getMiddleName(), params.getLastName(), params.getNickname(),
            params.getOrganization(), params.getEmail(), params.getPhone(),
            params.getAddressLineOne(), params.getAddressLineTwo(),
            params.getCity(), params.getZipcode()
        };

        for (int i = 0; i < keys.length; ++i) {
            if (values[i] == null) continue;
            client.addParameter(keys[i], values[i]);
        }
    }

    /**
     * Creates a AABService object.
     *
     * @param fqdn fully qualified domain name to use for sending requests
     * @param token OAuth token to use for authorization
     */
    public AABService(final String fqdn, final OAuthToken token) {
        super(fqdn, token);
    }

    public String createContact(Contact contact) throws RESTException {
        String endpoint = getFQDN() + "/addressBook/v1/contacts";

        JSONObject payload = new JSONObject();
        try {
			payload.put("contact", contact.toJson());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        APIResponse response = new RESTClient(endpoint)
            .addHeader("Content-Type", "application/json")
            .addAuthorizationHeader(getToken())
            .httpPost(payload.toString());

        if (response.getStatusCode() != 201) {
            throw new RESTException(response.getResponseBody());
        }

        return response.getHeader("location");
    }

    public ContactWrapper getContact(String contactId, String xFields)
            throws RESTException, ParseException {
        String endpoint = getFQDN() + "/addressBook/v1/contacts/" + contactId;

        RESTClient client = new RESTClient(endpoint)
            .addHeader("Accept", "application/json")
            .addAuthorizationHeader(getToken());

        if (xFields != null) {
            client.addHeader("x-fields", xFields);
        }

        APIResponse response = client.httpGet();
        if (response.getStatusCode() != 200) {
            throw new RESTException(response.getResponseBody());
        }

        final String body = response.getResponseBody();
        JSONObject jobj = null;
		try {
			jobj = new JSONObject(body);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (jobj.has("contact")) {
		    Contact contact =  null;
			try {
				contact = Contact.valueOf(jobj.getJSONObject("contact"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    return new ContactWrapper(contact);
		} 

		JSONObject jQc = null;
		try {
			jQc = jobj.getJSONObject("quickContact");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ContactWrapper(QuickContact.valueOf(jQc));
    }

    public ContactResultSet getContacts(String xFields, PageParams pParams,
            SearchParams sParams) throws RESTException, ParseException {

        String endpoint = getFQDN() + "/addressBook/v1/contacts";

        RESTClient client = new RESTClient(endpoint)
            .addHeader("Accept", "application/json")
            .addAuthorizationHeader(getToken());

        if (xFields != null) {
            client.addHeader("x-fields", xFields);
        }

        this.addPageParams(client, pParams);
        this.addSearchParams(client, sParams);

        APIResponse response = client.httpGet();
        if (response.getStatusCode() != 200) {
            throw new RESTException(response.getResponseBody());
        }
        final String body = response.getResponseBody();
        JSONObject jrs = null;
		try {
			jrs = new JSONObject(body).getJSONObject("resultSet");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ContactResultSet.valueOf(jrs);
    }

    public GroupResultSet getContactGroups(String contactId, PageParams params)
            throws RESTException, ParseException {
        String endpoint = getFQDN() 
            + "/addressBook/v1/contacts/" + contactId + "/groups";

        RESTClient client = new RESTClient(endpoint)
            .addHeader("Accept", "application/json")
            .addAuthorizationHeader(getToken());

        this.addPageParams(client, params);
        APIResponse response = client.httpGet();
        if (response.getStatusCode() != 200) {
            throw new RESTException(response.getResponseBody());
        }
        final String body = response.getResponseBody();
        JSONObject jrs = null;
		try {
			jrs = new JSONObject(body).getJSONObject("resultSet");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return GroupResultSet.valueOf(jrs);
    }

    public void updateContact(Contact contact, String contactId)
            throws RESTException {

        String endpoint = getFQDN() + "/addressBook/v1/contacts/" + contactId;

        JSONObject payload = new JSONObject();
        try {
			payload.put("contact", contact.toJson());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        APIResponse response = new RESTClient(endpoint)
            .addHeader("Content-Type", "application/json")
            .addAuthorizationHeader(getToken())
            .httpPatch(payload.toString());

        int statusCode = response.getStatusCode();
        if (statusCode != 200 && statusCode != 204) {
            throw new RESTException(response.getResponseBody());
        }
    }

   /* public void deleteContact(String contactId) throws RESTException {
        String endpoint = getFQDN() + "/addressBook/v1/contacts/" + contactId;

        APIResponse response = new RESTClient(endpoint)
            .addAuthorizationHeader(getToken())
            .httpDelete();

        int statusCode = response.getStatusCode();
        if (statusCode != 204) {
            throw new RESTException(response.getResponseBody());
        }
    }*/

    public String createGroup(Group group) throws RESTException {
        String endpoint = getFQDN() + "/addressBook/v1/groups";

        JSONObject payload = new JSONObject();
        try {
			payload.put("group", group.toJson());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        APIResponse response = new RESTClient(endpoint)
            .addHeader("Content-Type", "application/json")
            .addAuthorizationHeader(getToken())
            .httpPost(payload.toString());

        int statusCode = response.getStatusCode();
        if (statusCode != 201) {
            throw new RESTException(response.getResponseBody());
        }

        return response.getHeader("location");
    }

    public GroupResultSet getGroups(PageParams params, String groupName)
            throws RESTException, ParseException {
        String endpoint = getFQDN() + "/addressBook/v1/groups";

        RESTClient client = new RESTClient(endpoint)
            .addHeader("Accept", "application/json")
            .addAuthorizationHeader(getToken());

        this.addPageParams(client, params);
        if (groupName != null) client.addParameter("groupName", groupName);
        APIResponse response = client.httpGet();

        int statusCode = response.getStatusCode();
        if (statusCode != 200) {
            throw new RESTException(response.getResponseBody());
        }

        final String body = response.getResponseBody();
        JSONObject jrs = null;
		try {
			jrs = new JSONObject(body).getJSONObject("resultSet");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return GroupResultSet.valueOf(jrs);
    }

   /* public void deleteGroup(String groupId) throws RESTException {
        String endpoint = getFQDN() + "/addressBook/v1/groups/" + groupId;

        APIResponse response = new RESTClient(endpoint)
            .addAuthorizationHeader(getToken())
            .httpDelete();

        int statusCode = response.getStatusCode();
        if (statusCode != 204) {
            throw new RESTException(response.getResponseBody());
        }
    }
*/
    /*public void updateGroup(Group group, String groupId) throws RESTException {
        String endpoint = getFQDN() + "/addressBook/v1/groups/" + groupId;
        
        JSONObject payload = new JSONObject();
        payload.put("group", group.toJson());
        APIResponse response = new RESTClient(endpoint)
            .addAuthorizationHeader(getToken())
            .addHeader("Content-Type", "application/json")
            .httpPatch(payload.toString());

        int statusCode = response.getStatusCode();
        if (statusCode != 204) {
            throw new RESTException(response.getResponseBody());
        }
    }*/

    public void addContactsToGroup(String groupId, String contactIds)
            throws RESTException {

        // TODO: encode contactIds
        String endpoint = getFQDN() + "/addressBook/v1/groups/" + groupId 
            + "/contacts?contactIds=" + contactIds;

        APIResponse response = new RESTClient(endpoint)
            .addAuthorizationHeader(getToken())
            .httpPost();

        int statusCode = response.getStatusCode();
        if (statusCode != 204) {
            throw new RESTException(response.getResponseBody());
        }
    }

    public void removeContactsFromGroup(String groupId, String contactIds)
            throws RESTException {

        // TODO: encode contactIds
        String endpoint = getFQDN() + "/addressBook/v1/groups/" + groupId 
            + "/contacts?contactIds=" + contactIds;

        APIResponse response = new RESTClient(endpoint)
            .addAuthorizationHeader(getToken())
            .httpDelete();

        int statusCode = response.getStatusCode();
        if (statusCode != 204) {
            throw new RESTException(response.getResponseBody());
        }
    }

    public String[] getGroupContacts(String groupId, PageParams params)
            throws RESTException, ParseException {
        String endpoint = getFQDN() + "/addressBook/v1/groups/" + groupId 
            + "/contacts";

        RESTClient client = new RESTClient(endpoint)
            .addHeader("Accept", "application/json")
            .addAuthorizationHeader(getToken());

        this.addPageParams(client, params);
        APIResponse response = client.httpGet();

        int statusCode = response.getStatusCode();
        if (statusCode != 200) {
            throw new RESTException(response.getResponseBody());
        }

        final String body = response.getResponseBody();
        JSONObject jIds = null;
		try {
			jIds = new JSONObject(body).getJSONObject("contactIds");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JSONArray idsArr = null;
		try {
			idsArr = jIds.getJSONArray("id");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String[] ids = new String[idsArr.length()];
		for (int i = 0; i < ids.length; ++i) {
		    try {
				ids[i] = idsArr.getString(i);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return ids;
    }

    public Contact getMyInfo() throws RESTException, ParseException {
        String endpoint = getFQDN() + "/addressBook/v1/myInfo";

        APIResponse response = new RESTClient(endpoint)
            .addAuthorizationHeader(getToken())
            .addHeader("Accept", "application/json")
            .httpGet();

        int statusCode = response.getStatusCode();
        if (statusCode != 200) {
            throw new RESTException(response.getResponseBody());
        }

        final String body = response.getResponseBody();
        JSONObject jMyInfo = null;
		try {
			jMyInfo = new JSONObject(body);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			return Contact.valueOf(jMyInfo.getJSONObject("myInfo"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
    }

    public void updateMyInfo(Contact myInfo) throws RESTException {
        String endpoint = getFQDN() + "/addressBook/v1/myInfo";

        JSONObject payload = new JSONObject();
        try {
			payload.put("myInfo", myInfo.toJson());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        APIResponse response = new RESTClient(endpoint)
            .addAuthorizationHeader(getToken())
            .addHeader("Content-Type", "application/json")
            .httpPatch(payload.toString());

        int statusCode = response.getStatusCode();
        if (statusCode != 204) {
            throw new RESTException(response.getResponseBody());
        }
    }

}
