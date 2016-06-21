package com.al.app.geopatrol.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.al.app.geopatrol.network.RequestSaveLocation;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SyncService extends Service {

    private boolean is_shutdown = false;
    private boolean is_working = false;

    public SyncService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        new Thread(new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat sf = new SimpleDateFormat("HH:mm:ss");
                for(;;){
                    //Log.i("Sync", String.format("I'm alive! %s", sf.format(new Date())));
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    public void onDestroy() {
        is_shutdown = true;
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!is_working) {
            is_working = true;
            new Thread(work).start();
        }

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    Runnable work = new Runnable() {
        @Override
        public void run() {
            boolean hasMore = true;

            while(hasMore) {
                try {
                    final SyncData ds = new SyncData(SyncService.this);

                    List<Object[]> list = ds.list(10);
                    hasMore = list.size() > 0;

                    for (Object[] data : list) {
                        JSONObject obj = new JSONObject((String) data[2]);
                        Date time = (Date)data[1];

                        new RequestSaveLocation(SyncService.this,
                                "TestJobId",
                                obj.getString("DeviceId"),
                                time,
                                obj.getDouble("Longitude"),
                                obj.getDouble("Latitude") ) {

                            @Override
                            protected void onReady() {
                            }

                            @Override
                            protected void onError() {
                            }

                        }.request();

                        Log.i("SyncService", String.format("%s", (String) data[2]));
                        ds.remove((String) data[0]);
                    }

                    Thread.sleep(10L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            is_working = false;
        }
    };


}
