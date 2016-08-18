package com.al.app.geopatrol.activitys;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.al.app.geopatrol.App;
import com.al.app.geopatrol.R;
import com.al.app.geopatrol.services.GeoService;
import com.al.app.geopatrol.services.TaskService;
import com.al.app.geopatrol.utils.PatrolUtils;
import com.al.app.geopatrol.utils.Res;
import com.google.common.base.Strings;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.greenrobot.event.EventBus;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    static private SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private TextView tv_gps;

    private String userinfo = null;

    private LinearLayout risklayout;
    private LinearLayout risk_showlayout ;
    private LinearLayout recordslayout ;
    private LinearLayout records_tasklayout ;

    private Button postPatrol;
    private Button recordsDiv;
    private Button taskBtn;
    private Button messageDiv;

    private Button postRisk;
    private Button riskTrack;
    private Button risks;
    private Button riskMessage;

    private Button exitDiv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        App.setStartup();
        Res.init(this);

        if (savedInstanceState != null) {
            userinfo = savedInstanceState.getString("userinfo");
        }
        if (Strings.isNullOrEmpty(userinfo) || getIntent().hasExtra("userinfo")) {
            userinfo = getIntent().getStringExtra("userinfo");
        }
        // test user login
        if (Strings.isNullOrEmpty(userinfo)) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }

        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //toolbar.setLogo(R.mipmap.ic_launcher);

        risklayout = (LinearLayout)findViewById(R.id.risk_layout);
        risk_showlayout = (LinearLayout)findViewById(R.id.risk_show_layout);
        recordslayout = (LinearLayout)findViewById(R.id.records_layout);
        records_tasklayout = (LinearLayout)findViewById(R.id.records_task_layout);


        EventBus.getDefault().register(this);

        startService(new Intent(this, GeoService.class));

        tv_gps = (TextView) findViewById(R.id.gps_info);
        postPatrol=(Button) findViewById(R.id.post_patrol_div);
        recordsDiv=(Button) findViewById(R.id.records_div);
        taskBtn=(Button) findViewById(R.id.task_btn);
        messageDiv=(Button) findViewById(R.id.message_div);
        postPatrol.setOnClickListener(this);
        recordsDiv.setOnClickListener(this);
        taskBtn.setOnClickListener(this);
        messageDiv.setOnClickListener(this);

        postRisk=(Button) findViewById(R.id.post_risk_div);
        riskTrack=(Button) findViewById(R.id.risk_track_div);
        risks=(Button) findViewById(R.id.risks_div);
        riskMessage=(Button) findViewById(R.id.risk_message_div);
        postRisk.setOnClickListener(this);
        riskTrack.setOnClickListener(this);
        risks.setOnClickListener(this);
        riskMessage.setOnClickListener(this);

        exitDiv=(Button) findViewById(R.id.exit_div);
        exitDiv.setOnClickListener(this);


        switch (Res.getString("permissionFlag")){
            case "1":
                recordslayout.setVisibility(View.VISIBLE);
                records_tasklayout.setVisibility(View.VISIBLE);
                risklayout.setVisibility(View.GONE);
                risk_showlayout.setVisibility(View.GONE);
                break;
            case "2":
                recordslayout.setVisibility(View.GONE);
                records_tasklayout.setVisibility(View.GONE);
                risklayout.setVisibility(View.VISIBLE);
                risk_showlayout.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }

        PatrolUtils.init();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.post_patrol_div:
                startActivityForResult(new Intent(this, PostPatrolActivity.class), 2);
                break;
            case R.id.records_div:
                startActivityForResult(new Intent(this, RecordsActivity.class), 2);
                break;
            case R.id.task_btn:
                startActivityForResult(new Intent(this, TaskActivity.class), 2);
                break;
            case R.id.message_div:
                startActivityForResult(new Intent(this, RecordsActivity.class), 2);
                break;
            case R.id.post_risk_div:
                startActivityForResult(new Intent(this, PostRiskActivity.class), 2);
                break;
            case R.id.risk_track_div:
                startActivityForResult(new Intent(this, TrackRiskActivity.class), 2);
                break;
            case R.id.risks_div:
                startActivityForResult(new Intent(this, TaskActivity.class), 2);
                break;
            case R.id.risk_message_div:
                startActivityForResult(new Intent(this, RecordsActivity.class), 2);
                break;

            case R.id.exit_div:
                onExitApplicatioin(v);
                break;

            default:
                break;
        }
    }
    public void onExitApplicatioin(View v) {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("退出")
                .setMessage("关闭后服务端将接受不到你的巡检工作记录，确定要关闭巡检客户端吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();

                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    public void onPostGatrol(View view) {
        startActivityForResult(new Intent(this, PostPatrolActivity.class), 2);
    }

    public void onTask(View view) {
        startActivityForResult(new Intent(this, TaskService.class), 2);
    }
    public void onEvent(Location location) {
        if (tv_gps != null) {
            tv_gps.setText(String.format("时间：%s 坐标：%.5f,%.5f 海拔：%.2f 速度：%.2f 精度：%.0f米",
                    sf.format(new Date(location.getTime())),
                    location.getLongitude(),
                    location.getLatitude(),
                    location.getAltitude(),
                    location.getSpeed(),
                    location.getAccuracy()));
        }
    }
}
