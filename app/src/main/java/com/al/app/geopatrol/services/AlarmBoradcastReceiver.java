package com.al.app.geopatrol.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Dai Jingjing on 2016/1/19.
 */
public class AlarmBoradcastReceiver extends BroadcastReceiver {

    public AlarmBoradcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("services.Alarm.BoradcastReceiver")||
                intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            context.startService(new Intent(context, GeoService.class));
            context.startService(new Intent(context, SyncService.class));
        }

        if (intent.getAction().equals("services.Alarm.BoradcastReceiver")) {
            Log.i("AlarmBoradcastReceiver", String.format("Alarm: %s", new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date())));
        }

        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo networkInfo = connManager.getActiveNetworkInfo();

            Boolean available = networkInfo.isAvailable();
            Boolean mobileAvailable = !available ? false : connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED;
            Boolean wifiAvailable = !available ? false : connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;

            Log.i("AlarmBoradcastReceiver", String.format("网络有效: %b , Mobile: %b , WIFI: %b", available, mobileAvailable, wifiAvailable));
        }
    }
}
