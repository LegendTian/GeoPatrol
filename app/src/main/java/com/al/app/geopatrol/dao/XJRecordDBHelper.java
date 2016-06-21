package com.al.app.geopatrol.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by txy on 2016-5-23.
 */
public class XJRecordDBHelper extends SQLiteOpenHelper {
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "Records.db";
    public static final String TABLE_NAME = "Records";
    public XJRecordDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // create table Orders(Id integer primary key, CustomName text, OrderPrice integer, Country text);
        //RecordID , EmployeeID , PipelineID ,InstrumentID ,RecordDate , Exception , X , Y , Z , AbnormalMark ,CheckMark , Checker , Sector ,Unit ,PicUrl ,JPM ,Level ,PostState
        String sql = "create table if not exists " + TABLE_NAME + " (RecordID text primary key, EmployeeID text, PipelineID text,InstrumentID text,RecordDate text, Exception text, X real, Y real, Z real, AbnormalMark text,CheckMark text, Checker text, Sector text,Unit text,PicUrl text,JPM text,Level text,PostState text)";
        sqLiteDatabase.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS " + TABLE_NAME;
        sqLiteDatabase.execSQL(sql);
        onCreate(sqLiteDatabase);
    }



}
