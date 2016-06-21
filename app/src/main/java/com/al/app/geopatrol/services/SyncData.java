package com.al.app.geopatrol.services;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.common.base.Joiner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by Dai Jingjing on 2016/1/19.
 */
public class SyncData extends SQLiteOpenHelper {

    public static final int DATA_VERSION = 1;

    public final static String		DATABASE_NAME		= "local_db";
    public final static String		TABLE_NAME			= "local_data";

    public final static String[] COLUMN_ID = new String[]{"ID", "TEXT PRIMARY KEY"};
    public final static String[] COLUMN_CREATE_TIME = new String[]{"CREATE_DATE", "DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP"};
    public final static String[] COLUMN_SYNC_TIME = new String[]{"SYNC_DATE", "DATETIME NOT NULL"};
    public final static String[] COLUMN_CONTENT = new String[]{"CONTENT", "TEXT"};
    public final static Object[] COLUMNS = new Object[]{COLUMN_ID, COLUMN_CREATE_TIME, COLUMN_SYNC_TIME, COLUMN_CONTENT};

    public static String generateId() {
        return UUID.randomUUID().toString();
    }

    public SyncData(Context context) {
        super(context, DATABASE_NAME, null, DATA_VERSION);
    }

    public SyncData(Context context, SQLiteDatabase.CursorFactory factory) {
        super(context, DATABASE_NAME, factory, DATA_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        ArrayList<String> columns = new ArrayList<String>();
        for (Object column : COLUMNS)
            columns.add(Joiner.on(" ").join((String[]) column));

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" + Joiner.on(",").join(columns) + ")");
        db.execSQL("CREATE INDEX IF NOT EXISTS " + "IDX_" + TABLE_NAME + "_" + COLUMN_CREATE_TIME[0] + " ON " + TABLE_NAME + "(" + COLUMN_CREATE_TIME[0] + ")");
        db.execSQL("CREATE INDEX IF NOT EXISTS " + "IDX_" + TABLE_NAME + "_" + COLUMN_SYNC_TIME[0] + " ON " + TABLE_NAME + "(" + COLUMN_SYNC_TIME[0] + ")");
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void append(String id, Date createDate, String content) throws Exception {
        ContentValues values = new ContentValues();

        values.put(COLUMN_ID[0], id);
        values.put(COLUMN_CREATE_TIME[0], new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(createDate));
        values.put(COLUMN_SYNC_TIME[0], 0);
        values.put(COLUMN_CONTENT[0], content);

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public void remove(String id) throws Exception {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_ID[0] + "=?", new String[]{id});
        db.close();
    }

    public void updateSyncTime(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SYNC_TIME[0], new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        db.update(TABLE_NAME, values, COLUMN_ID[0] + "=?", new String[]{id});
        db.close();
    }

    public List<Object[]> list(int max) {
        List<Object[]> list = new ArrayList<Object[]>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(Joiner.on(" ").join(new String[]{
                "SELECT * FROM", TABLE_NAME, "WHERE", COLUMN_SYNC_TIME[0], "< ?", "ORDER BY", COLUMN_CREATE_TIME[0], "DESC", "LIMIT 0,", String.valueOf(max)}),
                new String[]{new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(new Date().getTime() - 60000))});

        while (cursor.moveToNext()) {
            list.add(new Object[] {
                    getStringField(cursor, COLUMN_ID[0], ""),
                    getDateField(cursor, COLUMN_CREATE_TIME[0], new Date(0)),
                    getStringField(cursor, COLUMN_CONTENT[0], ""),
            });
        }

        cursor.close();
        db.close();

        return list;
    }

    private static Date getDateField(Cursor cur, String ColumnName, Date Default) {
        int colIdx = cur.getColumnIndex(ColumnName);
        if (cur.isNull(colIdx))
            return Default;

        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(cur.getString(colIdx));
        } catch (ParseException e) {
            return Default;
        }
    }


    private static String getStringField(Cursor cur, String ColumnName, String Default) {
        int colIdx = cur.getColumnIndex(ColumnName);
        if (cur.isNull(colIdx))
            return Default;
        return cur.getString(colIdx);
    }

}
