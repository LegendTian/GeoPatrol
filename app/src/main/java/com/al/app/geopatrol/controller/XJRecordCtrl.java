package com.al.app.geopatrol.controller;

import android.app.Application;
import android.widget.Toast;

import com.al.app.geopatrol.R;
import com.al.app.geopatrol.model.XJRecord;
import com.al.app.geopatrol.utils.HttpUtils;
import com.al.app.geopatrol.utils.Res;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;


public class XJRecordCtrl {
    private XJRecord record;
    private Map<String,String> recordMap;
    public XJRecordCtrl( ){
    }
    public XJRecordCtrl(XJRecord record){
        this();
        this.record = record;
    }
    public XJRecordCtrl(Map<String,String> record){
        this();
        this.recordMap = record;
    }


    public String getRecordByID(String rid){

        String target = Res.getString("app_server")+ "records/" + rid;

        String resultStr= HttpUtils.get(target);
        return resultStr;
    }










}
