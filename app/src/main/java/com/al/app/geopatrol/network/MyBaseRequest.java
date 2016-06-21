package com.al.app.geopatrol.network;

import android.content.Context;

/**
 * Created by Dai Jingjing on 2016/2/26.
 */
public abstract class MyBaseRequest extends BaseRequest {

    public MyBaseRequest(Context c) {
        super(c);
    }

    @Override
    protected String getBaseUrl() {
        return "http://221.204.205.10/geo/api/";
    }
}
