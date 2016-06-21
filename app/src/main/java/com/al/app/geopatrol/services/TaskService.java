package com.al.app.geopatrol.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.al.app.geopatrol.R;
import com.al.app.geopatrol.model.XJKeyPoint;
import com.al.app.geopatrol.model.XJTask;
import com.al.app.geopatrol.utils.HttpUtils;
import com.al.app.geopatrol.utils.Res;
import com.google.common.collect.Lists;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.RecursiveTask;

public class TaskService extends Service {
    public static final String TAG = "TaskService";

    private TaskBinder taskBinder = new TaskBinder();

    public TaskService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return taskBinder;
    }





    public class TaskBinder extends Binder {
        private XJTask task;
        private Map<String,String> taskMap;

        public String getTaskByID(String rid){
            String target = Res.getString("app_server")+ "tasks/" + rid;

            String result= HttpUtils.get(target);

            return result;
        }
        public String getTaskByEmployeeID(String eid){

            String target = Res.getString("app_server")+ "tasks/employee/" + eid;
            System.out.println("getTaskByEmployeeID");

            String result= HttpUtils.get(target);
            System.out.println("result");
            System.out.println(result);


            try {
                if(result.length()==0)return null;
                result = result.replace("\\", "");
                result = result.substring(1);
                result = result.substring(0, result.length() - 2);
                JSONObject jsonObject = new JSONObject(result);
                System.out.println(jsonObject);
                System.out.println("missionID");
                System.out.println(jsonObject.get("missionID"));
            }
            catch (JSONException e){
                e.printStackTrace();
            }

            return result;
        }
        public XJTask getTaskAndInfoByEmployeeID(String eid){
            XJTask task=new XJTask();

            String result= getTaskByEmployeeID(eid);
            if(result==null||result==""){return null;}
            try {

                JSONObject jsonTask = new JSONObject(result);
                System.out.println(jsonTask);
                System.out.println("missionID");
                System.out.println(jsonTask.get("missionID"));
                String MID=jsonTask.get("missionID").toString();
                System.out.println(MID);
                String infoStr=getTaskInfoByID(MID);
                System.out.println("infoStr");
                System.out.println(infoStr);
                infoStr = infoStr.replace("\\", "");
                infoStr = infoStr.substring(1);
                infoStr = infoStr.substring(0, infoStr.length() - 2);
                JSONObject jsonTaskInfo = new JSONObject(infoStr);
                System.out.println(jsonTaskInfo);
                task=parseTask(jsonTask, jsonTaskInfo);
            }
            catch (JSONException e){
                System.out.println("erroooooooooooooooooooooor");
                e.printStackTrace();
            }
            System.out.println(task.getPipelineID());
            System.out.println(task.getStartM());
            System.out.println(task.getEndM());
            return task;
        }
        public String getTaskInfoByID(String rid){
            String target = Res.getString("app_server")+ "tasksinfo/" + rid;

            String result= HttpUtils.get(target);

            return result;
        }
        public String locateLine(String field,String value,Double startM,Double endM){
            String locateLineUrl=Res.getString("locate_line")  +"?RouteFieldName="+field+"&RouteFieldValue="+value+"&BeginMeasure="+startM+"&EndMeasure="+endM+"&f=pjson";
            String result= HttpUtils.get(locateLineUrl);
            try {
                JSONObject jsonLine = new JSONObject(result);
                System.out.println(jsonLine);
                System.out.println("geometry");

                String jsonGeo = jsonLine.get("geometry").toString();
                System.out.println(jsonGeo);


                return jsonGeo;
            }
            catch (JSONException e){
                System.out.println("erroooooooooooooooooooooor");
                e.printStackTrace();
                return null;
            }

        }
        public String getKeyPointsByID(String pid){
            String target = Res.getString("app_server")+ "keypoints/" + pid;

            String result= HttpUtils.get(target);

            return result;

        }

        public String getKeyPointsByTaskID(String tid){
            String target = Res.getString("app_server")+ "keypoints/task/" + tid;

            String result= HttpUtils.get(target);
            /*result = result.replace("\\", "");
            result = result.substring(1);
            result = result.substring(0, result.length() - 2);*/
            try {
                JSONArray jsonArray=new JSONArray(result);
                //JSONObject jsonObject = new JSONObject(result);
                System.out.println("jsonArray");
                System.out.println(jsonArray);
                System.out.println(jsonArray.length());
                System.out.println(jsonArray.get(jsonArray.length()-1));
            }
            catch (JSONException e){
                System.out.println("erroooooooooooooe");
                e.printStackTrace();
            }
            return result;

        }
        public JSONArray getKeyPointsArrayByTaskID(String tid){
            String target = Res.getString("app_server")+ "keypoints/task/" + tid;

            String result= HttpUtils.get(target);
            /*result = result.replace("\\", "");
            result = result.substring(1);
            result = result.substring(0, result.length() - 2);*/
            try {
                JSONArray jsonArray=new JSONArray(result);
                //JSONObject jsonObject = new JSONObject(result);
                System.out.println("jsonArray");
                System.out.println(jsonArray);
                System.out.println(jsonArray.length());


                return jsonArray;
            }
            catch (JSONException e){
                System.out.println("erroooooooooooooe");
                e.printStackTrace();
                return null;
            }

        }
        public List<XJKeyPoint> getKeyPointsListByTaskID(String tid){
            List<XJKeyPoint> points = Lists.newArrayList();
            try {
                JSONArray jsonArray=getKeyPointsArrayByTaskID(tid);

                for(int i=0,ii=jsonArray.length();i<ii;i++){
                    JSONObject jsonObject = (JSONObject)jsonArray.get(i);
                    XJKeyPoint point=parsePoint(jsonObject);
                    points.add(point);

                    System.out.println("point");
                    System.out.println(point);
                    System.out.println(point.getPointName());
                    System.out.println(point.getX());
                }



                return points;
            }
            catch (JSONException e){
                System.out.println("erroooooooooooooe");
                e.printStackTrace();
                return null;
            }

        }
        public List<Map<String,Object>> getKeyPointsMapListByTaskID(String tid){
            List<Map<String,Object> > points = Lists.newArrayList();
            try {
                JSONArray jsonArray=getKeyPointsArrayByTaskID(tid);
                if(jsonArray==null)return points;
                for(int i=0,ii=jsonArray.length();i<ii;i++){
                    JSONObject jsonObject = (JSONObject)jsonArray.get(i);
                    Map<String,Object>  point=parsePointMap(jsonObject);
                    points.add(point);

                    System.out.println("point");
                    System.out.println(point);
                    System.out.println(point.get("PointName"));
                    System.out.println(point.get("X"));
                }



                return points;
            }
            catch (JSONException e){
                System.out.println("erroooooooooooooe");
                e.printStackTrace();
                return null;
            }

        }
        public XJTask parseTask(JSONObject jsonTask,JSONObject taskInfoJson){
            XJTask task=new XJTask();
            try {
                task.setMissionID(jsonTask.get("missionID").toString());
                task.setMissionName(taskInfoJson.get("missionName").toString());
                task.setInstrumentID(jsonTask.get("instrumentID").toString());
                task.setEmployeeID(new JSONObject(jsonTask.get("employeeID").toString()).get("employeeID").toString());
                task.setEmployeeName(new JSONObject(jsonTask.get("employeeID").toString()).get("name").toString());

                task.setPipelineID(jsonTask.get("pipelineID").toString());

                task.setPipelineName(jsonTask.get("pipelineName").toString());
                task.setStartM((double) jsonTask.get("startM"));
                task.setEndM((double) jsonTask.get("endM"));
                task.setSector(taskInfoJson.get("sector").toString());
                task.setUnit(taskInfoJson.get("unit").toString());

                System.out.println("task");
                System.out.println(task.getMissionName());
                System.out.println(task);
            }
            catch (JSONException e){
                e.printStackTrace();
            }
            return task;
        }

        public XJKeyPoint parsePoint(JSONObject jsonPoint){
            XJKeyPoint point=new XJKeyPoint();
            try {
                point.setMissionID(jsonPoint.get("missionID").toString());
                point.setPipelineID(jsonPoint.get("pipelineID").toString());
                point.setPipelineName(jsonPoint.get("pipelineName").toString());
                point.setX((double) jsonPoint.get("x"));
                point.setY((double) jsonPoint.get("y"));
                point.setPointID(jsonPoint.get("pointID").toString());
                point.setPointName(jsonPoint.get("pointName").toString());
                point.setJPM(jsonPoint.get("jpm").toString());
                point.setInfo(jsonPoint.get("info").toString());

                System.out.println("point");
                System.out.println(point.getPointName());
                System.out.println(point);
            }
            catch (JSONException e){
                e.printStackTrace();
            }
            return point;

        }

        public Map<String,Object> parsePointMap(JSONObject jsonPoint){
            Map<String,Object> point=new HashMap<>();
            try {
                point.put("MissionID", jsonPoint.get("missionID").toString());
                point.put("PointID", jsonPoint.get("pointID").toString());
                point.put("PointName", jsonPoint.get("pointName").toString());
                point.put("PipelineID", jsonPoint.get("pipelineID").toString());
                point.put("PipelineName", jsonPoint.get("pipelineName").toString());
                point.put("X", (double) jsonPoint.get("x"));
                point.put("Y", (double) jsonPoint.get("y"));
                point.put("JPM", jsonPoint.get("jpm").toString());
                point.put("Descriptions", jsonPoint.get("info").toString());


                System.out.println("point");
                System.out.println(point.get("PointName"));
                System.out.println(point);
            }
            catch (JSONException e){
                e.printStackTrace();
            }
            return point;

        }



    }
}
