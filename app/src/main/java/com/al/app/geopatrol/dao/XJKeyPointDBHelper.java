package com.al.app.geopatrol.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by txy on 2016-5-23.
 */
public class XJKeyPointDBHelper extends SQLiteOpenHelper {
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "KeyPoints.db";
    public static final String TABLE_NAME = "XJKeyPoints";
    public XJKeyPointDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // create table Orders(Id integer primary key, CustomName text, OrderPrice integer, Country text);
        //"PointID","PointName","PipelineID","X", "Y","PipelineName","MissionID","JPM","Descriptions"
        String sql = "create table if not exists " + TABLE_NAME + " (PointID text primary key, PointName text, PipelineID text,PipelineName text,  X real, Y real, MissionID text, JPM text, Descriptions text)";
        sqLiteDatabase.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS " + TABLE_NAME;
        sqLiteDatabase.execSQL(sql);
        onCreate(sqLiteDatabase);
    }



}
