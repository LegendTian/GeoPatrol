package com.al.app.geopatrol.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.al.app.geopatrol.model.XJKeyPoint;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by txy on 2016-5-23.
 */
public class XJKeyPointDao {
    private static final String TAG = "XJKeyPointDao";

    // 列定义
    private final String[] TASK_COLUMNS = new String[] {"PointID","PointName","PipelineID","PipelineName","X", "Y","MissionID","JPM","Descriptions"};

    private Context context;
    private XJKeyPointDBHelper pointsDBHelper;

    public XJKeyPointDao(Context context) {
        this.context = context;
        pointsDBHelper = new XJKeyPointDBHelper(context);
    }

    /**
     * 判断表中是否有数据
     */
    public boolean isDataExist(){
        int count = 0;

        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = pointsDBHelper.getReadableDatabase();

            cursor = db.query(XJKeyPointDBHelper.TABLE_NAME, new String[]{"COUNT(PointID)"}, null, null, null, null, null);

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
            db = pointsDBHelper.getWritableDatabase();
            db.beginTransaction();

            //db.execSQL("insert into " + XJKeyPointDBHelper.TABLE_NAME + " (Id, CustomName, OrderPrice, Country) values (1, 'Arc', 100, 'China')");


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
                db = pointsDBHelper.getWritableDatabase();
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
    public List<XJKeyPoint> getAllDate(){
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = pointsDBHelper.getReadableDatabase();
            // select * from Orders
            cursor = db.query(XJKeyPointDBHelper.TABLE_NAME, TASK_COLUMNS, null, null, null, null, null);

            if (cursor.getCount() > 0) {
                List<XJKeyPoint> orderList = new ArrayList<XJKeyPoint>(cursor.getCount());
                while (cursor.moveToNext()) {
                    orderList.add(parsePoint(cursor));
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


    public boolean insertPointsMap(List<Map<String,Object>> list){
        SQLiteDatabase db = null;
        try {
            db = pointsDBHelper.getWritableDatabase();
            db.beginTransaction();
            String sql="insert into "+XJKeyPointDBHelper.TABLE_NAME+" (PointID,PointName,PipelineID,PipelineName,X,Y,MissionID,JPM,Descriptions) select ";
            for(int i=0,ii=list.size();i<ii;i++){
                if(i!=0){
                    sql+=" union all select ";
                }
                sql+=list.get(i).get("PointID").toString()+", ";
                sql+=list.get(i).get("PointName").toString()+", ";
                sql+=list.get(i).get("PipelineID").toString()+", ";
                sql+=list.get(i).get("PipelineName").toString()+", ";
                sql+=list.get(i).get("X").toString()+", ";
                sql+=list.get(i).get("Y").toString()+", ";
                sql+=list.get(i).get("MissionID").toString()+", ";
                sql+=list.get(i).get("JPM").toString()+", ";
                sql+=list.get(i).get("Descriptions").toString();
            }
            db.execSQL(sql);
            db.setTransactionSuccessful();


            return true;
        } catch (Exception e) {
            return false;

        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }

    }
    /**
     * 新增一条数据
     */
    public boolean insertDate(XJKeyPoint point){
        SQLiteDatabase db = null;

        try {
            db = pointsDBHelper.getWritableDatabase();
            db.beginTransaction();

            //"PointID","PointName","PipelineID","X", "Y","PipelineName","MissionID","JPM","Descriptions"
            ContentValues contentValues = new ContentValues();

            contentValues.put("MissionID", point.getMissionID());
            contentValues.put("PointID", point.getPointID());
            contentValues.put("PointName", point.getPointName());
            contentValues.put("PipelineID", point.getPipelineID());
            contentValues.put("PipelineName", point.getPipelineName());
            contentValues.put("X", point.getX());
            contentValues.put("Y", point.getY());
            contentValues.put("JPM", point.getJPM());
            contentValues.put("Descriptions", point.getInfo());

            db.insertOrThrow(XJKeyPointDBHelper.TABLE_NAME, null, contentValues);

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
     * 新增一条数据
     */
    public boolean insertDateByMap(Map<String,Object> point){
        SQLiteDatabase db = null;

        try {
            db = pointsDBHelper.getWritableDatabase();
            db.beginTransaction();

            //"PointID","PointName","PipelineID","X", "Y","PipelineName","MissionID","JPM","Descriptions"
            ContentValues contentValues = new ContentValues();

            contentValues.put("MissionID", point.get("MissionID").toString());
            contentValues.put("PointID", point.get("PointID").toString());
            contentValues.put("PointName", point.get("PointName").toString());
            contentValues.put("PipelineID", point.get("PipelineID").toString());
            contentValues.put("PipelineName", point.get("PipelineName").toString());
            contentValues.put("X", (double) point.get("X"));
            contentValues.put("Y", (double) point.get("Y"));
            contentValues.put("JPM", point.get("JPM").toString());
            contentValues.put("Descriptions", point.get("Descriptions").toString());

            db.insertOrThrow(XJKeyPointDBHelper.TABLE_NAME, null, contentValues);

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
     * 删除一条数据
     */
    public boolean deletePoint(String id) {
        SQLiteDatabase db = null;

        try {
            db = pointsDBHelper.getWritableDatabase();
            db.beginTransaction();

            // delete from XJKeyPoints where Id = 7
            db.delete(XJKeyPointDBHelper.TABLE_NAME, "PointID = ?", new String[]{id});
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
     * 修改一条数据
     */
    public boolean updatePoint(XJKeyPoint point){
        SQLiteDatabase db = null;
        try {
            db = pointsDBHelper.getWritableDatabase();
            db.beginTransaction();

            // update XJKeyPoints set XJKeyPointPrice = 800 where Id = 6
            ContentValues contentValues = new ContentValues();

            contentValues.put("MissionID", point.getMissionID());
            contentValues.put("PointID", point.getPointID());
            contentValues.put("PointName", point.getPointName());
            contentValues.put("PipelineID", point.getPipelineID());
            contentValues.put("PipelineName", point.getPipelineName());
            contentValues.put("X", point.getX());
            contentValues.put("Y", point.getY());
            contentValues.put("JPM", point.getJPM());
            contentValues.put("Descriptions", point.getInfo());

            db.update(XJKeyPointDBHelper.TABLE_NAME,
                    contentValues,
                    "PointID = ?",
                    new String[]{point.getPointID()});
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
     * 修改一条数据
     */
    public boolean updatePointByMap(Map<String,Object> point){
        SQLiteDatabase db = null;
        try {
            db = pointsDBHelper.getWritableDatabase();
            db.beginTransaction();

            // update XJKeyPoints set XJKeyPointPrice = 800 where Id = 6
            ContentValues contentValues = new ContentValues();

            contentValues.put("MissionID", point.get("MissionID").toString());
            contentValues.put("PointID", point.get("PointID").toString());
            contentValues.put("PointName", point.get("PointName").toString());
            contentValues.put("PipelineID", point.get("PipelineID").toString());
            contentValues.put("PipelineName", point.get("PipelineName").toString());
            contentValues.put("X", (double) point.get("X"));
            contentValues.put("Y", (double) point.get("Y"));
            contentValues.put("JPM", point.get("JPM").toString());
            contentValues.put("Descriptions", point.get("Descriptions").toString());

            db.update(XJKeyPointDBHelper.TABLE_NAME,
                    contentValues,
                    "PointID = ?",
                    new String[]{point.get("PointID").toString()});
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
     * 数据查询
     */
    public List<Map<String,Object>> getXJKeyPointMapByMid(String mid){
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = pointsDBHelper.getReadableDatabase();

            // select * from XJKeyPoints where CustomName = 'Bor'
            cursor = db.query(XJKeyPointDBHelper.TABLE_NAME,
                    TASK_COLUMNS,
                    "MissionID = ?",
                    new String[] {mid},
                    null, null, null);

            if (cursor.getCount() > 0) {
                List<Map<String,Object>> orderList = new ArrayList<Map<String,Object>>(cursor.getCount());
                while (cursor.moveToNext()) {
                    Map<String,Object> order = parsePointMap(cursor);
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
            db = pointsDBHelper.getReadableDatabase();
            // select count(Id) from XJKeyPoints where Country = 'China'
            cursor = db.query(XJKeyPointDBHelper.TABLE_NAME,
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
     * 比较查询  此处查询单笔数据中XJKeyPointPrice最高的
     */
    public XJKeyPoint getMaxXJKeyPointPrice(){
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = pointsDBHelper.getReadableDatabase();
            // select Id, CustomName, Max(XJKeyPointPrice) as XJKeyPointPrice, Country from XJKeyPoints
            cursor = db.query(pointsDBHelper.TABLE_NAME, new String[]{"Id", "CustomName", "Max(XJKeyPointPrice) as XJKeyPointPrice", "Country"}, null, null, null, null, null);

            if (cursor.getCount() > 0){
                if (cursor.moveToFirst()) {
                    return parsePoint(cursor);
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
     * 将查找到的数据转换成XJKeyPoint类
     */
    private XJKeyPoint parsePoint(Cursor cursor){
        XJKeyPoint point = new XJKeyPoint();
        //"PointID","PointName","PipelineID","X", "Y","PipelineName","MissionID","JPM","Descriptions"
        point.setPointID(cursor.getString(cursor.getColumnIndex("PointID")));
        point.setPointName(cursor.getString(cursor.getColumnIndex("PointName")));
        point.setMissionID(cursor.getString(cursor.getColumnIndex("MissionID")));

        point.setPipelineID(cursor.getString(cursor.getColumnIndex("PipelineID")));

        point.setPipelineName(cursor.getString(cursor.getColumnIndex("PipelineName")));
        point.setX(cursor.getDouble(cursor.getColumnIndex("X")));
        point.setY(cursor.getDouble(cursor.getColumnIndex("Y")));
        point.setJPM(cursor.getString(cursor.getColumnIndex("JPM")));
        point.setInfo(cursor.getString(cursor.getColumnIndex("Descriptions")));
        return point;
    }

    public Map<String,Object> parsePointMap(Cursor cursor){
        Map<String,Object> point=new HashMap<>();
        try {
            point.put("MissionID", cursor.getString(cursor.getColumnIndex("MissionID")));
            point.put("PointID", cursor.getString(cursor.getColumnIndex("PointID")));
            point.put("PointName", cursor.getString(cursor.getColumnIndex("PointName")));
            point.put("PipelineID", cursor.getString(cursor.getColumnIndex("PipelineID")));
            point.put("PipelineName", cursor.getString(cursor.getColumnIndex("PipelineName")));
            point.put("X", cursor.getDouble(cursor.getColumnIndex("X")));
            point.put("Y", cursor.getDouble(cursor.getColumnIndex("Y")));
            point.put("JPM", cursor.getString(cursor.getColumnIndex("JPM")));
            point.put("Descriptions", cursor.getString(cursor.getColumnIndex("Descriptions")));


            System.out.println("point");
            System.out.println(point.get("PointName"));
            System.out.println(point);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return point;

    }

}
