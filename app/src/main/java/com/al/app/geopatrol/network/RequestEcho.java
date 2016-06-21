package com.al.app.geopatrol.network;

import android.content.Context;

import com.al.app.geopatrol.utils.HashUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by Dai Jingjing on 2016/2/26.
 */
public abstract class RequestEcho extends MyBaseRequest {

    private static final String TAG = RequestEcho.class.getSimpleName();

    private String randomId;

    public RequestEcho(Context c) {
        super(c);

        randomId = HashUtils.getRandomId();

    }


    @Override
    protected String getMethod() {
        return "echo";
    }

    @Override
    protected JSONObject getParams() throws JSONException {
        JSONObject p = new JSONObject();

        p.put("randomId", randomId);

        return p;
    }

    @Override
    protected void onResponse(JSONObject p) throws JSONException {
        String returnValue = p.getString("randomId");
    }
}
