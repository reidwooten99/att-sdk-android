package com.att.api.immn.service;

import org.json.JSONException;
import org.json.JSONObject;

public final class SendResponse {
    private String id;

    public SendResponse(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public static SendResponse valueOf(JSONObject jobj) throws JSONException {
        String id = jobj.getString("id");

        return new SendResponse(id);
    }
}
