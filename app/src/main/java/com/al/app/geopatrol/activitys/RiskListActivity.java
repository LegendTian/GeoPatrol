package com.al.app.geopatrol.activitys;

import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.TypedArray;

import android.os.Bundle;
import android.os.IBinder;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.al.app.geopatrol.adapter.RiskListAdapter;
import com.al.app.geopatrol.model.XJTrouble;
import com.al.app.geopatrol.services.RiskService;
import com.al.app.geopatrol.utils.Res;

import java.util.ArrayList;


public class RiskListActivity extends ListActivity {

    public static String RESULT_RISKCODE = "riskCode";
    public static String RESULT_RISKNAME = "riskName";
    public static String RESULT_RISKUUID="riskUUID";
    public String[] countrynames, countrycodes;
    private TypedArray imgs;
    private ArrayList<XJTrouble> troubleList=null;
    private RiskService.RiskBinder riskBinder;
    private ArrayAdapter<XJTrouble> adapter;
    private Handler handler=null;

    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            riskBinder = (RiskService.RiskBinder) service;

            new Thread(new Runnable() {
                @Override
                public void run() {
                // 执行具体的任务
                System.out.println("执行具体的任务");
                initTroubleList();
                adapter = new RiskListAdapter(RiskListActivity.this, troubleList);
                handler.post(runnableAdapter);
                }
            }).start();

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("RiskLists");
        //troubleList=getIntent().getExtras().getParcelableArrayList("troubleList");
        //System.out.println("troubleList----"+troubleList.size());

        //创建属于主线程的handler
        handler=new Handler();

        Intent bindIntent = new Intent(RiskListActivity.this, RiskService.class);
        bindService(bindIntent, connection, BIND_AUTO_CREATE);
        /*if (troubleList != null) {
            adapter = new RiskListAdapter(this, troubleList);
            setListAdapter(adapter);
        }*/
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                XJTrouble c = troubleList.get(position);
                Intent returnIntent = new Intent();
                System.out.println("RESULT_RISKCODE-----" + c.getRiskID());
                returnIntent.putExtra(RESULT_RISKCODE, c.getRiskID());
                returnIntent.putExtra(RESULT_RISKUUID, c.getTroubleID());
                returnIntent.putExtra(RESULT_RISKNAME, c.getException());
                setResult(RESULT_OK, returnIntent);

                finish();
            }
        });
    }
    private void initTroubleList(){
        riskBinder.initRiskUrl();
        troubleList=riskBinder.getRisksListING(Res.getString("sectorID"),"0");
    }

    // 构建Runnable对象，在runnable中更新界面
    Runnable runnableAdapter=new  Runnable(){
        @Override
        public void run() {
            //更新界面
            setListAdapter(adapter);
        }
    };

}