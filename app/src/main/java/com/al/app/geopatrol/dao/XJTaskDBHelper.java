package com.al.app.geopatrol.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by txy on 2016-5-23.
 */
public class XJTaskDBHelper extends SQLiteOpenHelper {
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "geoPatrol.db";
    public static final String TABLE_NAME = "Tasks";
    public XJTaskDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // create table Orders(Id integer primary key, CustomName text, OrderPrice integer, Country text);
        //"MissionID","MissionName","EmployeeID","EmployeeName","InstrumentID", "PipelineID","PipelineName","StartM","EndM","Sector","Unit"
        String sql = "create table if not exists " + TABLE_NAME + " (MissionID text primary key, MissionName text, EmployeeID text, EmployeeName text, InstrumentID text, PipelineID text, PipelineName text, StartM real, EndM real, Sector text, Unit text)";
        sqLiteDatabase.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS " + TABLE_NAME;
        sqLiteDatabase.execSQL(sql);
        onCreate(sqLiteDatabase);
    }



}
