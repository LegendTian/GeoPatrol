package com.al.app.geopatrol.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.al.app.geopatrol.model.XJRecord;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by txy on 2016-5-23.
 */
public class XJRecordDao {
    private static final String TAG = "XJRecordDao";

    // 列定义recordID,employeeID,pipelineID,instrumentID,recordDate,exception,x,y,zz,abnormalMark,checkMark,checker,sector,unit,picUrl,JPM,level,postState
    private final String[] TASK_COLUMNS = new String[] {"RecordID","EmployeeID","PipelineID","InstrumentID","RecordDate", "Exception","X", "Y","Z","AbnormalMark","CheckMark","Checker","Sector","Unit","PicUrl","JPM","Level","PostState"};

    private Context context;

    private XJRecordDBHelper recordsDBHelper;

    public XJRecordDao(Context context) {
        this.context = context;
        recordsDBHelper = new XJRecordDBHelper(context);
    }
    /**
     * 判断表中是否有数据
     */
    public boolean isDataExist(){
        int count = 0;

        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = recordsDBHelper.getReadableDatabase();

            cursor = db.query(XJRecordDBHelper.TABLE_NAME, new String[]{"COUNT(RecordID)"}, null, null, null, null, null);

            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
            if (count > 0) return true;
        }
        catch (Exception e) {
            Log.e(TAG, "", e);
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return false;
    }

    /**
     * 初始化数据
     */
    public void initTable(){
        SQLiteDatabase db = null;

        try {
            db = recordsDBHelper.getWritableDatabase();
            db.beginTransaction();

            //db.execSQL("insert into " + XJRecordDBHelper.TABLE_NAME + " (Id, CustomName, OrderPrice, Country) values (1, 'Arc', 100, 'China')");


            db.setTransactionSuccessful();
        }catch (Exception e){
            Log.e(TAG, "", e);
        }finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }
    }

    /**
     * 执行自定义SQL语句
     */
    public void execSQL(String sql) {
        SQLiteDatabase db = null;

        try {
            if (sql.contains("select")){

            }else if (sql.contains("insert") || sql.contains("update") || sql.contains("delete")){
                db = recordsDBHelper.getWritableDatabase();
                db.beginTransaction();
                db.execSQL(sql);
                db.setTransactionSuccessful();

            }
        } catch (Exception e) {

            Log.e(TAG, "", e);
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }
    }
    /**
     * 查询数据库中所有数据
     */
    public List<XJRecord> getAllDate(){
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = recordsDBHelper.getReadableDatabase();
            // select * from Orders
            cursor = db.query(XJRecordDBHelper.TABLE_NAME, TASK_COLUMNS, null, null, null, null, null);

            if (cursor.getCount() > 0) {
                List<XJRecord> orderList = new ArrayList<XJRecord>(cursor.getCount());
                while (cursor.moveToNext()) {
                    orderList.add(parseRecord(cursor));
                }
                return orderList;
            }
        }
        catch (Exception e) {
            Log.e(TAG, "", e);
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }

        return null;
    }
    /**
     * 新增一条数据
     */
    public boolean insertDataByMap(Map<String,String> record){
        SQLiteDatabase db = null;

        try {
            db = recordsDBHelper.getWritableDatabase();
            db.beginTransaction();

            //RecordID , EmployeeID , PipelineID ,InstrumentID ,RecordDate , Exception , X , Y , Z , AbnormalMark ,CheckMark , Checker , Sector ,Unit ,PicUrl ,JPM ,Level ,PostState
            ContentValues contentValues = new ContentValues();

            if(record.containsKey("recordID"))contentValues.put("RecordID", record.get("recordID"));
            if(record.containsKey("employeeID"))contentValues.put("EmployeeID", record.get("employeeID"));
            if(record.containsKey("pipelineID"))contentValues.put("PipelineID", record.get("pipelineID"));
            if(record.containsKey("instrumentID"))contentValues.put("InstrumentID", record.get("instrumentID"));
            if(record.containsKey("recordDate"))contentValues.put("RecordDate", record.get("recordDate"));
            if(record.containsKey("exception"))contentValues.put("Exception", record.get("exception"));
            if(record.containsKey("X"))contentValues.put("X",  record.get("X"));
            if(record.containsKey("Y"))contentValues.put("Y",  record.get("Y"));
            if(record.containsKey("Z"))contentValues.put("Z",  record.get("Z"));
            if(record.containsKey("AbnormalMark"))contentValues.put("AbnormalMark", record.get("AbnormalMark"));
            if(record.containsKey("CheckMark"))contentValues.put("CheckMark", record.get("CheckMark"));
            if(record.containsKey("Checker"))contentValues.put("Checker", record.get("Checker"));
            if(record.containsKey("sector"))contentValues.put("Sector", record.get("sector"));
            if(record.containsKey("unit"))contentValues.put("Unit", record.get("unit"));
            if(record.containsKey("picUrl"))contentValues.put("PicUrl", record.get("picUrl"));
            if(record.containsKey("JPM"))contentValues.put("JPM", record.get("JPM"));
            if(record.containsKey("xjLevel"))contentValues.put("Level", record.get("xjLevel"));
            if(record.containsKey("PostState"))contentValues.put("PostState", record.get("PostState"));
            db.insertOrThrow(XJRecordDBHelper.TABLE_NAME, null, contentValues);

            db.setTransactionSuccessful();
            return true;
        }catch (SQLiteConstraintException e){
            Toast.makeText(context, "主键重复", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Log.e(TAG, "", e);
        }finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }
        return false;
    }


    /**
     * 将查找到的数据转换成XJRecord类
     */
    private XJRecord parseRecord(Cursor cursor){
        XJRecord record = new XJRecord();
        //RecordID , EmployeeID , PipelineID ,InstrumentID ,RecordDate , Exception , X , Y , Z , AbnormalMark ,CheckMark , Checker , Sector ,Unit ,PicUrl ,JPM ,Level ,PostState
        record.setRecordID(cursor.getString(cursor.getColumnIndex("RecordID")));
        record.setEmployeeID(cursor.getString(cursor.getColumnIndex("EmployeeID")));

        record.setPipelineID(cursor.getString(cursor.getColumnIndex("PipelineID")));
        record.setInstrumentID(cursor.getString(cursor.getColumnIndex("InstrumentID")));

        String d=cursor.getString(cursor.getColumnIndex("RecordDate"));

        record.setRecordDate(d);
        record.setException(cursor.getString(cursor.getColumnIndex("Exception")));
        record.setX(cursor.getDouble(cursor.getColumnIndex("X")));
        record.setY(cursor.getDouble(cursor.getColumnIndex("Y")));
        record.setZ(cursor.getDouble(cursor.getColumnIndex("Z")));
        record.setAbnormalMark(cursor.getString(cursor.getColumnIndex("AbnormalMark")));
        record.setCheckMark(cursor.getString(cursor.getColumnIndex("CheckMark")));
        record.setChecker(cursor.getString(cursor.getColumnIndex("Checker")));
        record.setSector(cursor.getString(cursor.getColumnIndex("Sector")));
        record.setUnit(cursor.getString(cursor.getColumnIndex("Unit")));
        record.setPicUrl(cursor.getString(cursor.getColumnIndex("PicUrl")));
        record.setJPM(cursor.getString(cursor.getColumnIndex("JPM")));
        record.setLevel(cursor.getString(cursor.getColumnIndex("Level")));
        record.setPostState(cursor.getString(cursor.getColumnIndex("PostState")));
        return record;
    }

    public Map<String,Object> parseRecordMap(Cursor cursor){
        Map<String,Object> record=new HashMap<>();
        try {
            record.put("RecordID", cursor.getString(cursor.getColumnIndex("RecordID")));
            record.put("EmployeeID", cursor.getString(cursor.getColumnIndex("EmployeeID")));

            record.put("PipelineID", cursor.getString(cursor.getColumnIndex("PipelineID")));
            record.put("InstrumentID", cursor.getString(cursor.getColumnIndex("InstrumentID")));
            record.put("RecordDate", cursor.getString(cursor.getColumnIndex("RecordDate")));
            record.put("Exception", cursor.getString(cursor.getColumnIndex("Exception")));
            record.put("X", cursor.getDouble(cursor.getColumnIndex("X")));
            record.put("Y", cursor.getDouble(cursor.getColumnIndex("Y")));
            record.put("Z", cursor.getDouble(cursor.getColumnIndex("Z")));
            record.put("AbnormalMark", cursor.getString(cursor.getColumnIndex("AbnormalMark")));
            record.put("CheckMark", cursor.getString(cursor.getColumnIndex("CheckMark")));

            record.put("Checker", cursor.getString(cursor.getColumnIndex("Checker")));
            record.put("Sector", cursor.getString(cursor.getColumnIndex("Sector")));
            record.put("Unit", cursor.getString(cursor.getColumnIndex("Unit")));
            record.put("PicUrl", cursor.getString(cursor.getColumnIndex("PicUrl")));
            record.put("JPM", cursor.getString(cursor.getColumnIndex("JPM")));
            record.put("Level", cursor.getString(cursor.getColumnIndex("Level")));
            record.put("PostState", cursor.getString(cursor.getColumnIndex("PostState")));

            System.out.println("record");
            System.out.println(record.get("RecordID"));
            System.out.println(record);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return record;

    }
}
