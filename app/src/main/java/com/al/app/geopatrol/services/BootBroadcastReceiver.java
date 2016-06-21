package com.al.app.geopatrol.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

/**
 * Created by Dai Jingjing on 2016/1/19.
 */
public class BootBroadcastReceiver extends BroadcastReceiver {

    public BootBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent _intent) {
        setAlarm(context);
    }

    public static void setAlarm(Context context) {
        // 启动完成
        Intent intent = new Intent(context, AlarmBoradcastReceiver.class);
        intent.setAction("services.Alarm.BoradcastReceiver");
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // 5分钟一个周期，不停的发送广播
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), 300000, sender);

    }
}
