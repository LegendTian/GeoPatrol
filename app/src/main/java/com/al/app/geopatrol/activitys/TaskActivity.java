package com.al.app.geopatrol.activitys;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;

import android.graphics.Color;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import android.os.Bundle;

import android.os.IBinder;

import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.al.app.geopatrol.R;
import com.al.app.geopatrol.dao.XJKeyPointDao;
import com.al.app.geopatrol.dao.XJTaskDao;
import com.al.app.geopatrol.map.TianDiMapLayer;
import com.al.app.geopatrol.map.TianDiMapLayerType;
import com.al.app.geopatrol.model.XJKeyPoint;
import com.al.app.geopatrol.model.XJTask;
import com.al.app.geopatrol.services.TaskService;
import com.al.app.geopatrol.utils.FileUtils;
import com.al.app.geopatrol.utils.Res;
import com.esri.android.map.Callout;
import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapView;
import com.esri.android.map.event.OnSingleTapListener;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.MapGeometry;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polyline;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

public class TaskActivity extends AppCompatActivity {

    private static final int RESULT_CAMERA_CAPTURE = 0x802;

    private static SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private TaskService.TaskBinder taskBinder;

    private String taskStr=null;
    private XJTask task=null;

    private XJTaskDao taskDao;
    private XJKeyPointDao pointDao;

    private Polyline taskLine;
    private List<XJKeyPoint> xjKeyPointsData;
    private List<Map<String,Object>> xjPointsMap;

    GraphicsLayer taskLayer = new GraphicsLayer();

    double x_min=0,x_max=0,y_min=0,y_max=0;

    private View parentView;

    private LocationManager locationManager = null;
    private MapView mapView = null;

    GraphicsLayer graphicsLayer = new GraphicsLayer();
    Location GeoLocation=null;

    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            taskBinder = (TaskService.TaskBinder) service;

            new Thread(new Runnable() {
                @Override
                public void run() {
                    // 执行具体的任务 先查看本地有无任务记录，有就读取本地，否则调用服务,并保存到本地
                    initTask();

                    initLineloopGeo();

                    initKeyPoints();



                }
            }).start();




        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Res.init(this);
        parentView = getLayoutInflater().inflate(R.layout.activity_task, null);
        setContentView(parentView);

        taskDao=new XJTaskDao(TaskActivity.this);
        pointDao=new XJKeyPointDao(TaskActivity.this);

        Intent bindIntent = new Intent(TaskActivity.this, TaskService.class);
        bindService(bindIntent, connection, BIND_AUTO_CREATE);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mapView = (MapView) findViewById(R.id.mapView);
        mapView.setEsriLogoVisible(false);
        mapView.enableWrapAround(true);


        TianDiMapLayer layer = new TianDiMapLayer(this, TianDiMapLayerType.IMG_C);
        mapView.addLayer(layer);
        TianDiMapLayer layer3 = new TianDiMapLayer(this, TianDiMapLayerType.CIA_C);
        mapView.addLayer(layer3);



        taskLayer.setSelectionColor(Color.YELLOW);
        taskLayer.setSelectionColorWidth(10);
        mapView.addLayer(taskLayer);

        System.out.println("centerAndZoom");
        mapView.centerAndZoom(38, 112, 10);



        mapView.setOnStatusChangedListener(new OnStatusChangedListener() {
            @Override
            public void onStatusChanged(Object o, STATUS status) {

                if (status == STATUS.LAYER_LOADED) {


                }

            }
        });
        // Identify on single tap of map
        mapView.setOnSingleTapListener(new OnSingleTapListener() {

            private static final long serialVersionUID = 1L;

            @Override
            public void onSingleTap(final float x, final float y) {

                if (!mapView.isLoaded()) {
                    return;
                }

                // Add to Identify Parameters based on tapped location
                Point identifyPoint = mapView.toMapPoint(x, y);

                int[] pointsID=taskLayer.getGraphicIDs(x,y,20);

                if(pointsID.length>0){
                    Graphic singleTapGraphic = new Graphic(identifyPoint,new SimpleMarkerSymbol(Color.RED,20, SimpleMarkerSymbol.STYLE.CIRCLE));

                    graphicsLayer.addGraphic(singleTapGraphic);

                    taskLayer.clearSelection();
                    //taskLayer.setSelectedGraphics(pointsID,true);

                    for(int i=0,ii=pointsID.length;i<ii;i++){
                        Graphic graphic=taskLayer.getGraphic(pointsID[i]);
                        System.out.println(graphic.getAttributes().get("PointID"));
                        if(graphic.getAttributes().get("PointID")!=null){
                            taskLayer.setSelectedGraphics(new int[]{pointsID[i]},true);
                            showPointInfo(graphic);
                        }
                    }

                }else {
                    hideCallout();
                }

            }

        });


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            Toast.makeText(this, "请查看帮助，打开GPS访问权限", Toast.LENGTH_LONG).show();
            Log.e("PostPatrolActivity", "don't have acccess ACCESS_FINE_LOCATION & ACCESS_COARSE_LOCATION!!!");
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 1000, 0, locationListener);
        }


    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {
        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
        }
        mapView.recycle();
        unbindService(connection);
        super.onDestroy();
    }
    protected void onRestart() {
        //mapView
        super.onRestart();
    }
    /**
     * Called when the activity is Paused
     */
    protected void onPause() {
        super.onPause();
        mapView.pause();
    }

    /**
     * Called when the activity is Resumed
     */
    protected void onResume() {
        super.onResume();
        mapView.unpause();
    }
    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            GeoLocation=location;
            Log.i("LocationListener", String.format("lon: %f, lat: %f", location.getLongitude(), location.getLatitude()));
            if (mapView != null) {
                //mapView.centerAndZoom(location.getLatitude(), location.getLongitude(), 16);

                graphicsLayer.removeAll();
                Point point = GeometryEngine.project(location.getLatitude(), location.getLongitude(), mapView.getSpatialReference());
                graphicsLayer.addGraphic(new Graphic(point, new SimpleMarkerSymbol(Color.RED, 10, SimpleMarkerSymbol.STYLE.CIRCLE)));

            }

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    public void showPointInfo(Graphic graphic){
        String LSP = System.getProperty("line.separator");
        StringBuilder outputVal = new StringBuilder();
        if (graphic.getAttributes().containsKey("PointName")) {
            outputVal.append("关键点: "+ graphic.getAttributes().get("PointName").toString());
            outputVal.append(LSP);
        }
        if (graphic.getAttributes().containsKey("PipelineName")) {
            outputVal.append("管段: "+ graphic.getAttributes().get("PipelineName").toString());
            outputVal.append(LSP);
        }
        if (graphic.getAttributes().containsKey("JPM")) {
            String loc=graphic.getAttributes().get("JPM").toString();
            System.out.println("oooooooooooooo");
            System.out.println(loc);
            if(loc!=null||loc!="null"){
                outputVal.append("位置: "+ loc);
                outputVal.append(LSP);
            }
        }
        if (graphic.getAttributes().containsKey("Descriptions")) {
            String desc=graphic.getAttributes().get("Descriptions").toString();
            if(desc!=null||desc!="null"){
                outputVal.append("备注: "+ desc);
                outputVal.append(LSP);
            }
        }

        // Create a TextView to write identify results
        TextView txtView;
        txtView = new TextView(TaskActivity.this);
        txtView.setText(outputVal);
        txtView.setTextColor(Color.BLACK);
        txtView.setLayoutParams(new ListView.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        txtView.setGravity(Gravity.CENTER_VERTICAL);

        Callout callout = mapView.getCallout();
        callout.setContent(txtView);
        callout.show((Point) graphic.getGeometry());
    }

    public void hideCallout(){
        mapView.getCallout().hide();
        taskLayer.clearSelection();
    }

    public Geometry json2Geometry(String jsonStr){
        JsonFactory jsonFactory = new JsonFactory();
        Geometry geometry=null;
        try {
            JsonParser jsonParser = jsonFactory.createJsonParser(jsonStr);
            MapGeometry mapgeo = GeometryEngine.jsonToGeometry(jsonParser);
            geometry =mapgeo.getGeometry();

        } catch (JsonParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return geometry;
    }

    /**
     * 初始化任务：先查看本地有无任务记录，有就读取本地，否则调用服务,并保存到本地
     * **/
    public void initTask(){
        System.out.println("initTask");
        System.out.println(taskDao.isDataExist());
        System.out.println("initTask");
        if(taskDao.isDataExist()){
            List<XJTask> tasks=taskDao.getTaskByEmployeeID(Res.getString("employeeID"));
            if(tasks!=null&&tasks.size()>0){
                task=taskDao.getTaskByEmployeeID(Res.getString("employeeID")).get(0);
            }else {
                task=taskBinder.getTaskAndInfoByEmployeeID(Res.getString("employeeID"));
                taskDao.insertDate(task);
            }

        }
        else {
            task=taskBinder.getTaskAndInfoByEmployeeID(Res.getString("employeeID"));
            taskDao.insertDate(task);
        }
        System.out.println("initTask end");
        System.out.println(task);
    }

    /**
     * 初始化管线：先查看本地有无保存管线的文件，有则直接解析加载，否则调用服务,并保存到本地。然后地图展示
     * */
    public void initLineloopGeo(){
        if(task==null)return;
        String json= FileUtils.readTextFile(Res.getString("pipelineID")+"_"+task.getStartM());
        System.out.println("本地读取json");
        System.out.println(json);

        if(json.length()==0){
            json=taskBinder.locateLine("LINELOOPEVENTID",task.getPipelineID(),task.getStartM(),task.getEndM());
            boolean res= FileUtils.saveToSDCard(Res.getString("pipelineID")+"_"+task.getStartM(),json);
            System.out.println("json保存结果");
            System.out.println(res);
        }

        taskLine =(Polyline) json2Geometry(json);
        if(taskLine!=null){
            taskLayer.addGraphic(new Graphic(taskLine, new SimpleLineSymbol(Color.RED, 4)));

            mapView.setExtent(taskLine);
        }

    }

    /**
     * 初始化关键点：先查看本地有关键点记录，有则直接解析加载，否则调用服务,并保存到本地。然后地图展示
     * */
    public void initKeyPoints(){
        System.out.println("initKeyPoints");
        System.out.println(task);
        if(task==null)return;
        System.out.println(pointDao.isDataExist());
        if(pointDao.isDataExist()){
            xjPointsMap=pointDao.getXJKeyPointMapByMid(task.getMissionID());
        }else {
            xjPointsMap=taskBinder.getKeyPointsMapListByTaskID(task.getMissionID());

            pointDao.insertPointsMap(xjPointsMap);
        }
        System.out.println(xjPointsMap);
        for(int i=0,ii=xjPointsMap.size();i<ii;i++){
            Map<String,Object> p=xjPointsMap.get(i);
            double px=(double)p.get("X");
            double py=(double)p.get("Y");
            Graphic graphic =new Graphic(new Point(px,py),new SimpleMarkerSymbol(Color.RED, 10, SimpleMarkerSymbol.STYLE.CIRCLE),p);

            taskLayer.addGraphic(graphic);
            /*if(x_min==0&&x_max==0&&y_min==0&&y_max==0){
                x_min=x_max=px;
                y_min=y_max=py;
            }
            if(px>x_min){
                x_max=px;
            }
            else {
                x_min=px;
            }
            if(py>y_min){y_max=py;}else {y_min=py;}*/

        }
        //mapView.setExtent(new Envelope(x_min-0.02,y_min-0.02,x_max+0.02,y_max+0.02));
    }





}
