package com.al.app.geopatrol.services;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.greenrobot.event.EventBus;

public class GeoService extends Service {

    private String deviceId;
    private LocationManager locationManager;
    private SimpleDateFormat sf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    public GeoService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        deviceId = telephonyManager.getDeviceId();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            Log.e("", "don't have access fine & coarse location service!!!");
            return;
        }

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, locationListener);
        //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 0, locationListener);
        //locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 10000, 0, locationListener);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                EventBus.getDefault().post(location);

                try {
                    String id = SyncData.generateId();
                    JSONObject json = new JSONObject();
                    json.put("Id", id);
                    json.put("DeviceId", deviceId);
                    json.put("Time", new Date(location.getTime()));
                    json.put("Longitude", location.getLongitude());
                    json.put("Latitude", location.getLatitude());
                    json.put("Altitude", location.getAltitude());
                    json.put("Speed", location.getSpeed());
                    json.put("Accuracy", location.getAccuracy());
                    json.put("Provider", location.getProvider());

                    new SyncData(GeoService.this).append(id, new Date(location.getTime()), json.toString());

                    //Log.i("Location", json.toString());

                    GeoService.this.startService(new Intent(GeoService.this, SyncService.class));

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

}
