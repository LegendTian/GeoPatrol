package com.al.app.geopatrol.controller;

import com.al.app.geopatrol.model.XJTask;
import com.al.app.geopatrol.utils.HttpUtils;
import com.al.app.geopatrol.utils.Res;

import java.util.Map;

/**
 * Created by txy on 2016-5-19.
 */
public class XJTaskCtrl {
    private XJTask task;
    private Map<String,String> taskMap;
    public XJTaskCtrl(){
    }
    public XJTaskCtrl(XJTask task){
        this();
        this.task = task;
    }
    public XJTaskCtrl(Map<String, String> task){
        this();
        this.taskMap = task;
    }


    public String getTaskByID(String rid){
        String target = Res.getString("app_server")+ "tasks/" + rid;

        String result= HttpUtils.get(target);

        return result;
    }
    public String getTaskByEmployeeID(String eid){

        String target = Res.getString("app_server")+ "tasks/employee/" + eid;

        String result= HttpUtils.get(target);

        return result;
    }
    public String getTaskInfoByID(String rid){
        String target = Res.getString("app_server")+ "tasksinfo/" + rid;

        String result= HttpUtils.get(target);

        return result;
    }

    public String getKeyPointsByID(String pid){
        String target = Res.getString("app_server")+ "keypoints/" + pid;

        String result= HttpUtils.get(target);

        return result;

    }

    public String getKeyPointsByTaskID(String tid){
        String target = Res.getString("app_server")+ "keypoints/task/" + tid;

        String result= HttpUtils.get(target);

        return result;

    }






}
