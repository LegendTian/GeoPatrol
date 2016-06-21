package com.al.app.geopatrol.network;

import android.content.Context;

import com.al.app.geopatrol.utils.DateUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by Dai Jingjing on 2016/2/26.
 */
public abstract class RequestSaveLocation extends MyBaseRequest {

    private static final String TAG = RequestSaveLocation.class.getSimpleName();

    private String newId;

    private Date _time;

    private String _jobId,_deviceId;
    private double _longitude, _latitude;
    private double _altitude = -1;

    private double _speed = -1;
    private double _accuracy = -1;

    public RequestSaveLocation(Context c, String jobId, String deviceId, Date time, double longitude, double latitude) {
        super(c);

        _time = time;
        _jobId = jobId;
        _deviceId = deviceId;
        _longitude = longitude;
        _latitude = latitude;

    }

    public void setAltitude(double v) {
        _altitude = v;
    }

    public void setSpeed(double v) {
        _speed = v;
    }

    public void setAccuracy(double v) {
        _accuracy = v;
    }

    public String getNewId() {
        return newId;
    }

    @Override
    protected String getMethod() {
        return "save_location";
    }

    @Override
    protected JSONObject getParams() throws JSONException {
        JSONObject p = new JSONObject();

        p.put("Time", DateUtils.toJSONDate(_time));
        p.put("JobId", _jobId);
        p.put("DeviceId", _deviceId);
        p.put("Longitude", _longitude);
        p.put("Latitude", _latitude);

        if (_altitude > 0)
            p.put("Altitude", _altitude);
        if (_speed > 0)
            p.put("Speed", _speed);
        if (_accuracy > 0)
            p.put("Accuracy", _accuracy);

        return p;
    }

    @Override
    protected void onResponse(JSONObject p) throws JSONException {
        newId = p.getString("Id");
    }
}
