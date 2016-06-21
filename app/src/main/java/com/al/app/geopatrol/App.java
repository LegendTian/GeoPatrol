package com.al.app.geopatrol;

import android.app.Application;

import com.al.app.geopatrol.services.BootBroadcastReceiver;
import com.esri.android.runtime.ArcGISRuntime;

/**
 * Created by Dai Jingjing on 2016/1/19.
 */
public class App extends Application {

    static {
        System.loadLibrary("geoPatrol");
    }

    private static boolean startup = false;

    public App() {
    }

    public static boolean isStartup() {
        return startup;
    }

    public static void setStartup() {
        startup = true;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        BootBroadcastReceiver.setAlarm(this);

        appSetup();

        ArcGISRuntime.setClientId("hQOMBGeniPWcVyCr");

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    public native void appSetup();
}
