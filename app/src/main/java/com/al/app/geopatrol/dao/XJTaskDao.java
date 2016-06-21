package com.al.app.geopatrol.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.al.app.geopatrol.model.XJTask;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by txy on 2016-5-23.
 */
public class XJTaskDao {
    private static final String TAG = "XJTaskDao";

    // 列定义
    private final String[] TASK_COLUMNS = new String[] {"MissionID","MissionName","EmployeeID","EmployeeName","InstrumentID", "PipelineID","PipelineName","StartM","EndM","Sector","Unit"};

    private Context context;
    private XJTaskDBHelper tasksDBHelper;

    public XJTaskDao(Context context) {
        this.context = context;
        tasksDBHelper = new XJTaskDBHelper(context);
    }

    /**
     * 判断表中是否有数据
     */
    public boolean isDataExist(){
        int count = 0;

        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = tasksDBHelper.getReadableDatabase();
            // select count(Id) from Orders
            cursor = db.query(XJTaskDBHelper.TABLE_NAME, new String[]{"COUNT(MissionID)"}, null, null, null, null, null);

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
            db = tasksDBHelper.getWritableDatabase();
            db.beginTransaction();

            //db.execSQL("insert into " + XJTaskDBHelper.TABLE_NAME + " (Id, CustomName, OrderPrice, Country) values (1, 'Arc', 100, 'China')");


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
                db = tasksDBHelper.getWritableDatabase();
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
    public List<XJTask> getTaskByEmployeeID(String id){
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = tasksDBHelper.getReadableDatabase();

            // select * from XJTasks where CustomName = 'Bor'
            cursor = db.query(XJTaskDBHelper.TABLE_NAME,
                    TASK_COLUMNS,
                    "EmployeeID = ?",
                    new String[] {id},
                    null, null, null);

            if (cursor.getCount() > 0) {
                List<XJTask> orderList = new ArrayList<XJTask>(cursor.getCount());
                while (cursor.moveToNext()) {
                    XJTask order = parseTask(cursor);
                    orderList.add(order);
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
     * 查询数据库中所有数据
     */
    public List<XJTask> getAllDate(){
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = tasksDBHelper.getReadableDatabase();
            // select * from Orders
            cursor = db.query(XJTaskDBHelper.TABLE_NAME, TASK_COLUMNS, null, null, null, null, null);

            if (cursor.getCount() > 0) {
                List<XJTask> orderList = new ArrayList<XJTask>(cursor.getCount());
                while (cursor.moveToNext()) {
                    orderList.add(parseTask(cursor));
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
    public boolean insertDate(XJTask task){
        SQLiteDatabase db = null;

        try {
            db = tasksDBHelper.getWritableDatabase();
            db.beginTransaction();

            //"MissionID","MissionName","EmployeeID","InstrumentID", "PipelineID","PipelineName","StartM","EndM","Sector","Unit"
            ContentValues contentValues = new ContentValues();
            System.out.println(task.getMissionID());
            contentValues.put("MissionID", task.getMissionID());
            System.out.println(task.getMissionName());
            contentValues.put("MissionName", task.getMissionName());
            System.out.println(task.getEmployeeID());
            contentValues.put("EmployeeID", task.getEmployeeID());
            System.out.println(task.getEmployeeName());
            contentValues.put("EmployeeName", task.getEmployeeName());
            System.out.println(task.getInstrumentID());
            contentValues.put("InstrumentID", task.getInstrumentID());
            System.out.println(task.getPipelineID());
            contentValues.put("PipelineID", task.getPipelineID());
            System.out.println(task.getPipelineName());
            contentValues.put("PipelineName", task.getPipelineName());
            System.out.println(task.getStartM());
            contentValues.put("StartM", task.getStartM());
            System.out.println(task.getEndM());
            contentValues.put("EndM", task.getEndM());
            System.out.println(task.getSector());
            contentValues.put("Sector", task.getSector());
            System.out.println(task.getUnit());
            contentValues.put("Unit", task.getUnit());


            db.insertOrThrow(XJTaskDBHelper.TABLE_NAME, null, contentValues);

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
     * 删除一条数据  此处删除Id为7的数据
     */
    public boolean deleteTask(String id) {
        SQLiteDatabase db = null;

        try {
            db = tasksDBHelper.getWritableDatabase();
            db.beginTransaction();

            // delete from XJTasks where Id = 7
            db.delete(XJTaskDBHelper.TABLE_NAME, "MissionID = ?", new String[]{id});
            db.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            Log.e(TAG, "", e);
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }
        return false;
    }

    /**
     * 修改一条数据  此处将Id为6的数据的XJTaskPrice修改了800
     */
    public boolean updateTask(XJTask task){
        SQLiteDatabase db = null;
        try {
            db = tasksDBHelper.getWritableDatabase();
            db.beginTransaction();

            // update XJTasks set XJTaskPrice = 800 where Id = 6
            ContentValues cv = new ContentValues();

            cv.put("MissionID", task.getMissionID());
            cv.put("MissionName", task.getMissionName());
            cv.put("EmployeeID", task.getEmployeeID());
            cv.put("EmployeeName", task.getEmployeeName());
            cv.put("InstrumentID", task.getInstrumentID());
            cv.put("PipelineID", task.getPipelineID());
            cv.put("PipelineName", task.getPipelineName());
            cv.put("StartM", task.getStartM());
            cv.put("EndM", task.getEndM());
            cv.put("Sector", task.getSector());
            cv.put("Unit", task.getUnit());

            db.update(XJTaskDBHelper.TABLE_NAME,
                    cv,
                    "MissionID = ?",
                    new String[]{task.getMissionID()});
            db.setTransactionSuccessful();
            return true;
        }
        catch (Exception e) {
            Log.e(TAG, "", e);
        }
        finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }

        return false;
    }

    /**
     * 数据查询  此处将用户名为"Bor"的信息提取出来
     */
    public List<XJTask> getBorXJTask(){
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = tasksDBHelper.getReadableDatabase();

            // select * from XJTasks where CustomName = 'Bor'
            cursor = db.query(XJTaskDBHelper.TABLE_NAME,
                    TASK_COLUMNS,
                    "CustomName = ?",
                    new String[] {"Bor"},
                    null, null, null);

            if (cursor.getCount() > 0) {
                List<XJTask> orderList = new ArrayList<XJTask>(cursor.getCount());
                while (cursor.moveToNext()) {
                    XJTask order = parseTask(cursor);
                    orderList.add(order);
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
     * 统计查询  此处查询Country为China的用户总数
     */
    public int getChinaCount(){
        int count = 0;

        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = tasksDBHelper.getReadableDatabase();
            // select count(Id) from XJTasks where Country = 'China'
            cursor = db.query(XJTaskDBHelper.TABLE_NAME,
                    new String[]{"COUNT(Id)"},
                    "Country = ?",
                    new String[] {"China"},
                    null, null, null);

            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
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

        return count;
    }

    /**
     * 比较查询  此处查询单笔数据中XJTaskPrice最高的
     */
    public XJTask getMaxXJTaskPrice(){
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = tasksDBHelper.getReadableDatabase();
            // select Id, CustomName, Max(XJTaskPrice) as XJTaskPrice, Country from XJTasks
            cursor = db.query(tasksDBHelper.TABLE_NAME, new String[]{"Id", "CustomName", "Max(XJTaskPrice) as XJTaskPrice", "Country"}, null, null, null, null, null);

            if (cursor.getCount() > 0){
                if (cursor.moveToFirst()) {
                    return parseTask(cursor);
                }
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
     * 将查找到的数据转换成XJTask类
     */
    private XJTask parseTask(Cursor cursor){
        XJTask task = new XJTask();
        //"MissionID","MissionName","EmployeeID","InstrumentID", "PipelineID","PipelineName","StartM","EndM","Sector","Unit"
        task.setMissionID(cursor.getString(cursor.getColumnIndex("MissionID")));
        task.setMissionName(cursor.getString(cursor.getColumnIndex("MissionName")));

        task.setEmployeeID(cursor.getString(cursor.getColumnIndex("EmployeeID")));
        task.setEmployeeName(cursor.getString(cursor.getColumnIndex("EmployeeName")));
        task.setInstrumentID(cursor.getString(cursor.getColumnIndex("InstrumentID")));
        task.setPipelineID(cursor.getString(cursor.getColumnIndex("PipelineID")));

        task.setPipelineName(cursor.getString(cursor.getColumnIndex("PipelineName")));
        task.setStartM(cursor.getDouble(cursor.getColumnIndex("StartM")));
        task.setEndM(cursor.getDouble(cursor.getColumnIndex("EndM")));
        task.setSector(cursor.getString(cursor.getColumnIndex("Sector")));
        task.setUnit(cursor.getString(cursor.getColumnIndex("Unit")));
        return task;
    }


}
