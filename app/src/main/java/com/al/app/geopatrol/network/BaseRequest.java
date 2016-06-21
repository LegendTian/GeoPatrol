package com.al.app.geopatrol.network;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.al.app.geopatrol.utils.DateUtils;
import com.al.app.geopatrol.utils.HashUtils;

/**
 * Created by Dai Jingjing on 2016/2/26.
 */
public abstract class BaseRequest {
    private static final Object lock = new Object();
    private static RequestQueue gRequestQueue = null;

    private static final String TAG = "BaseRequest";
    public static final int TIMEOUT_MS = 10000;
    public static final int MAX_RETRIES = 3;
    public static final float BACKOFF_MULTIPLIER = 1.2f;

    protected boolean mIsReady = false;

    private int responseStatusCode = 0;

    protected RequestQueue requestQueue;
    protected Context mContext;
    protected JsonObjectRequest request = null;

    public BaseRequest(Context c) {
        this.mContext = c;
        this.requestQueue = getRequestQueue(c);
    }

    public static RequestQueue getRequestQueue(Context c) {
        synchronized (lock) {
            if (gRequestQueue == null) {
                gRequestQueue = Volley.newRequestQueue(c.getApplicationContext());
            }
        }
        return gRequestQueue;
    }

    public boolean isReady() {
        return mIsReady;
    }

    public Context getContext() {
        return mContext;
    }

    protected int requestMethod() {
        return Request.Method.POST;
    }

    protected String requestURL() {
        return getBaseUrl() + getMethod();
    }

    public void request() {
        mIsReady = false;
        cancel();

        JSONObject params = null;
        try {
            params = getParams();
        } catch (Exception e) {
            e.printStackTrace();
            doOnError();
        }

        responseStatusCode = 0;
        request = new JsonObjectRequest(requestMethod(), requestURL(), params, responseListener, errorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();

                headers.putAll(super.getHeaders());

                String ts = DateUtils.toTimestamp(new Date());

                headers.put("API-Ver", String.valueOf(getApiVersion()));
                headers.put("API-Id", getApiId());
                headers.put("API-Sign", HashUtils.md5(getApiId() + getApiPassword() + ts));
                headers.put("API-Timestamp", ts);

                return headers;
            }
        };

        config(request);
        requestQueue.add(request);
    }

    Listener<JSONObject> responseListener = new Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject arg0) {
            try {
                _onResponse(arg0);
                doOnReady();
            } catch (Exception e) {
                e.printStackTrace();
                doOnError();
            }
        }
    };

    ErrorListener errorListener = new ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError arg0) {
            if (arg0 != null && arg0.networkResponse != null){
                responseStatusCode = arg0.networkResponse.statusCode;
            }
            doOnError();
        }
    };

    public int getStatusCode() {
        return responseStatusCode;
    }

    protected void config(Request<?> r) {
        r.setTag(TAG);
        r.setShouldCache(false);
        r.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT_MS, MAX_RETRIES, BACKOFF_MULTIPLIER));
    }

    public void cancel() {
        if (request != null) {
            request.cancel();
        }
    }

    private void doOnReady() {
        mIsReady = true;
        if (mContext instanceof Activity && ((Activity)mContext).isFinishing())
            return;

        onReady();
    }

    private void doOnError() {
        mIsReady = false;
        if (mContext instanceof Activity && ((Activity)mContext).isFinishing())
            return;

        onError();
    }

    protected static boolean attr(JSONObject p, String name) {
        return p.has(name) && !p.isNull(name);
    }


    private void _onResponse(JSONObject p) throws Exception{
        onResponse(p);
    }

    protected String getApiVersion() {
        return "1.0";
    }

    protected String getApiId() {
        return "";
    }

    protected String getApiPassword() {
        return "";
    }

    abstract protected String getBaseUrl();
    abstract protected String getMethod();
    abstract protected JSONObject getParams() throws Exception;
    abstract protected void onResponse(JSONObject p) throws Exception;

    abstract protected void onReady();
    abstract protected void onError();

}
