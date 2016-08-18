package com.al.app.geopatrol.activitys;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.al.app.geopatrol.R;
import com.al.app.geopatrol.dao.XJRecordDao;
import com.al.app.geopatrol.model.XJTrouble;
import com.al.app.geopatrol.services.RiskService;
import com.al.app.geopatrol.utils.Bimp;
import com.al.app.geopatrol.utils.DateUtils;
import com.al.app.geopatrol.utils.FileUtils;
import com.al.app.geopatrol.utils.HashUtils;
import com.al.app.geopatrol.utils.HttpUtils;
import com.al.app.geopatrol.utils.ImageItem;
import com.al.app.geopatrol.utils.ImageUtils;
import com.al.app.geopatrol.utils.PatrolUtils;
import com.al.app.geopatrol.utils.PhotoFileUtils;
import com.al.app.geopatrol.utils.PublicWay;
import com.al.app.geopatrol.utils.Res;
import com.al.app.geopatrol.utils.SpinerPopWindow;
import com.al.app.geopatrol.widget.PictureView;
import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapView;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Point;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.google.common.collect.Maps;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TrackRiskActivity extends AppCompatActivity {

    private static final int RESULT_CAMERA_CAPTURE = 0x802;
    private static final int RESULT_CROP_IMAGE = 0x804;
    private final static int CAMERA_RESULT = 0;
    private final static int RISK_RESULT = 0x804;
    private static SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static SimpleDateFormat sf2 = new SimpleDateFormat("yyyyMMdd_HHmmssSSS");
    public static Bitmap bimap ;
    public static String RISKTYPE="risktype";
    private GridView noScrollgridview;
    private GridAdapter adapter;
    private View parentView;
    private PopupWindow pop = null;
    private LinearLayout ll_popup;

    private LocationManager locationManager = null;
    private MapView mapView = null;
    private TextView tv_gps = null;
    private TextView tv_date = null;
    private TextView tv_exception = null;
    private PictureView imageView = null;
    private SpinerPopWindow<String> mSpinerPopWindow;
    private TextView tv_trouble_type=null;
    private TextView tv_risk_value=null;
    private TextView tv_post_user = null;
    private double mapScale = 8000d;
    GraphicsLayer graphicsLayer = new GraphicsLayer();

    private String imageFile = null;
    ProgressBar progressBar = null;
    ViewGroup viewGroup = null;
    Location GeoLocation=null;
    Context context;

    Map<String, String> recordMap = Maps.newHashMap();
    Intent riskViewIntent=null;
    private XJRecordDao recordsDao;

    private ArrayList<XJTrouble> troubleList=null;
    private RiskService.RiskBinder riskBinder;
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

                }
            }).start();

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parentView = getLayoutInflater().inflate(R.layout.activity_track_risk, null);
        setContentView(parentView);
        Res.init(this);

        Intent bindIntent = new Intent(TrackRiskActivity.this, RiskService.class);
        bindService(bindIntent, connection, BIND_AUTO_CREATE);

        recordsDao = new XJRecordDao(this);
        if (! recordsDao.isDataExist()){
            recordsDao.initTable();
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        bimap = BitmapFactory.decodeResource(
                getResources(),
                R.mipmap.icon_addpic_unfocused);
        PublicWay.activityList.add(this);

        tv_trouble_type = (TextView) findViewById(R.id.trouble_type_view);
        tv_trouble_type.setOnClickListener(clickListener);
        riskViewIntent = new Intent(this, RiskListActivity.class);
        tv_risk_value=(TextView) findViewById(R.id.tv_risk_value);
        tv_risk_value.setOnClickListener(clickListener);
        mSpinerPopWindow = new SpinerPopWindow<String>(this, PatrolUtils.ttList,itemClickListener);
        mSpinerPopWindow.setOnDismissListener(dismissListener);

        //mapView = (MapView) findViewById(R.id.mapView);
        tv_gps = (TextView) findViewById(R.id.gps_info);
        tv_date= (TextView) findViewById(R.id.recordDate);
        tv_exception= (TextView) findViewById(R.id.exception_info);
        tv_post_user=(TextView)findViewById(R.id.post_user);
        tv_post_user.setText(Res.getString("employeeName"));
        //控制位置和时间图标大小
        Drawable locationIcon = getResources().getDrawable(R.mipmap.location);
        locationIcon.setBounds(0, 0, 28, 36);//第一0是距左边距离，第二0是距上边距离，24分别是长宽
        tv_gps.setCompoundDrawables(locationIcon, null, null, null);//只放左边
        Drawable dateIcon = getResources().getDrawable(R.mipmap.date);
        dateIcon.setBounds(0, 0, 32, 32);
        tv_date.setCompoundDrawables(dateIcon, null, null, null);

        imageView = (PictureView) findViewById(R.id.image);

        Init();
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
        noScrollgridview = (GridView) findViewById(R.id.noScrollgridview);
        noScrollgridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
        adapter = new GridAdapter(this);
        adapter.update();
        noScrollgridview.setAdapter(adapter);
        noScrollgridview.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                if (arg2 == Bimp.tempSelectBitmap.size()) {
                    System.out.println("--------------------------------------------------------------");
                    ll_popup.startAnimation(AnimationUtils.loadAnimation(TrackRiskActivity.this, R.anim.activity_translate_in));
                    pop.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
                } else {
                    Intent intent = new Intent(TrackRiskActivity.this,
                            GalleryActivity.class);
                    intent.putExtra("position", "1");
                    intent.putExtra("ID", arg2);
                    System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                    System.out.println(arg2);
                    startActivity(intent);
                }
            }
        });


    }

    @Override
    public boolean onSupportNavigateUp() {
        return true;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_record_post, menu);

        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_record_post:

                recordMap.put("imageType", RISKTYPE);
                if(recordMap.containsKey("riskID")) {
                    new SendRecordsAsyncTask().execute();
                }
                else {
                    Toast.makeText(this, "请先进行隐患匹配~！", Toast.LENGTH_SHORT).show();
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        System.out.println("onMenuOpened");
        System.out.println("featureId");
        System.out.println(featureId);
        System.out.println(Window.FEATURE_ACTION_BAR);
        System.out.println(Window.FEATURE_OPTIONS_PANEL);
        if ( menu != null) {
            System.out.println(menu.getClass().getSimpleName().toString());
            if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
                try {
                    Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception e) {
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
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
        super.onDestroy();
    }
    public void Init() {

        pop = new PopupWindow(TrackRiskActivity.this);

        View view = getLayoutInflater().inflate(R.layout.item_popupwindows, null);

        ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);

        pop.setWidth(LayoutParams.MATCH_PARENT);
        pop.setHeight(LayoutParams.WRAP_CONTENT);
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.setFocusable(true);
        pop.setOutsideTouchable(true);
        pop.setContentView(view);

        RelativeLayout parent = (RelativeLayout) view.findViewById(R.id.parent);
        Button bt1 = (Button) view
                .findViewById(R.id.item_popupwindows_camera);
        Button bt2 = (Button) view
                .findViewById(R.id.item_popupwindows_Photo);
        Button bt3 = (Button) view
                .findViewById(R.id.item_popupwindows_cancel);
        parent.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });
        bt1.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                photo();
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });
        bt2.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(TrackRiskActivity.this,
                        AlbumActivity.class);
                intent.putExtra("activityName", "TrackRiskActivity");
                startActivity(intent);
                //startActivityForResult(intent, RESULT_CAMERA_CAPTURE);
                overridePendingTransition(R.anim.activity_translate_in, R.anim.activity_translate_out);
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });
        bt3.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });

        noScrollgridview = (GridView) findViewById(R.id.noScrollgridview);
        noScrollgridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
        adapter = new GridAdapter(this);
        adapter.update();
        noScrollgridview.setAdapter(adapter);
        noScrollgridview.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                if (arg2 == Bimp.tempSelectBitmap.size()) {
                    Log.i("ddddddd", "----------");
                    ll_popup.startAnimation(AnimationUtils.loadAnimation(TrackRiskActivity.this,R.anim.activity_translate_in));
                    pop.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
                } else {
                    Intent intent = new Intent(TrackRiskActivity.this,
                            GalleryActivity.class);
                    intent.putExtra("position", "1");
                    intent.putExtra("ID", arg2);
                    startActivity(intent);
                }
            }
        });

    }

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            GeoLocation=location;
            Log.i("LocationListener", String.format("lon: %f, lat: %f", location.getLongitude(), location.getLatitude()));
            if (mapView != null) {
                mapView.centerAt(location.getLatitude(), location.getLongitude(), false);
                mapView.setScale(mapScale, true);

                graphicsLayer.removeAll();
                Point point = GeometryEngine.project(location.getLatitude(), location.getLongitude(), mapView.getSpatialReference());
                graphicsLayer.addGraphic(new Graphic(point, new SimpleMarkerSymbol(Color.RED, 10, SimpleMarkerSymbol.STYLE.CIRCLE)));

            }

            if (tv_gps != null) {
                tv_gps.setText(String.format("%.4f, %.4f",
                        location.getLongitude(),
                        location.getLatitude()));
            }
            if(tv_date!=null){
                tv_date.setText(String.format("%s",
                        sf.format(new Date(location.getTime()))));
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

    public void onGetPicture(View view) {
        imageFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + String.format("/IMG_%s.jpg", sf2.format(new Date()));
        startCameraCapture();
    }
    public void onSendRecord(View view){
        //String target="http://192.168.0.151:8080/patrolservice/records/44887A73-1B32-4ED9-8F4A-842F4A5B9C96";
        //new GetRecordsAsyncTask().execute(target);
        new SendRecordsAsyncTask().execute();
    }


  /**
     * 拍照
     */
    public void startCameraCapture() {
        try {
            Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(imageFile)));
            startActivityForResult(intentFromCapture, RESULT_CAMERA_CAPTURE);
        } catch (Exception ex) {
            Toast.makeText(this, "无法调用本地照相程序", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 上报巡检记录
     *
     */
    public String sendRecord(){
        //参数
        //Looper.prepare();

        //recordMap = Maps.newHashMap();
        recordMap.put("objectID",UUID.randomUUID().toString().toUpperCase());//"39606AE7-BE36-4879-973B-F85F9A69077F");

        recordMap.put("employeeID", Res.getString("employeeID"));
        recordMap.put("keYuan", tv_post_user.getText().toString());

        recordMap.put("findDate", DateUtils.formatDate("yyyy-MM-dd HH:mm:ss", new Date(System.currentTimeMillis())));
        String info=tv_exception.getText().toString();
        recordMap.put("description", info);


        recordMap.put("images", getImageNames());
        recordMap.put("imageLabel", recordMap.get("objectID"));


        recordMap.put("deviceCode", Res.getString("deviceCode"));
        System.out.println(recordMap);

        return sendByType(recordMap);
    }

    public String sendByType(Map<String, String> recordMap){

        //服务器请求路径
        String strUrlPath ="";
        switch (RISKTYPE){
            case "A":
                strUrlPath=Res.getString("app_server")+ "buildFoll/"+ recordMap.get("objectID");
                break;
            case "B":

                strUrlPath=Res.getString("app_server")+ "buildFoll/"+ recordMap.get("objectID");
                break;
            default:
                strUrlPath=Res.getString("app_server")+ "buildFoll/"+ recordMap.get("objectID");
                break;
        }

        System.out.println(strUrlPath);
        String strResult= HttpUtils.submitPostData(strUrlPath, recordMap, "utf-8");


        return strResult;

    }
    /**
     * 上传图片
     *imagePath 图片路径
     * bitmap 直接上传bitmap
     */
    public String uploadImage(String imagePath,Bitmap bitmap){

        String imageName=null;
        Map<String, String> params = new HashMap<String, String>();
        params.put("type", recordMap.get("riskID").toString());
        params.put("tig", recordMap.get("objectID").toString());

        if(bitmap!=null){
            Map<String,Bitmap> bs=new HashMap<String,Bitmap>();
            bs.put("temp",bitmap);
            try{

                imageName = HttpUtils.postBitmap(getString(R.string.dfs_server) + "save", params, bs);

            } catch(IOException ioEx) {
                ioEx.printStackTrace(); // or what ever you want to do with it

            }
        }
        else if(imagePath!=null){
            Map<String, File> upFiles=new HashMap<String, File>();
            File file=new File(imagePath);
            String fileName=FileUtils.getFileName(imagePath);
            upFiles.put(fileName, file);
            try{

                imageName= HttpUtils.post(getString(R.string.dfs_server)+"save", params, upFiles);
            } catch(IOException ioEx) {
                ioEx.printStackTrace();

            }
        }
        return imageName;

    }

    public String getImageNames(){
        String names="";
        int count=Bimp.tempSelectBitmap.size();
        for(int i=0;i<count;i++){
            String imagePath=Bimp.tempSelectBitmap.get(i).getImagePath();
            System.out.println("imagepath:" + imagePath);
            Bitmap bitmap=Bimp.tempSelectBitmap.get(i).getBitmap();
            System.out.println("bitmap:");
            System.out.println("bitmap:" + bitmap);
            String is= uploadImage(imagePath,bitmap);
            names+=(i+1==count)?is:is+";";
        }
        System.out.println("names:"+names);
        return names;
    }


    /**
     * 从指定URL获取图片
     * @param url
     * @return
     */
    private Bitmap getImageBitmap(String url){
        URL imgUrl = null;
        Bitmap bitmap = null;
        try {
            imgUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection)imgUrl.openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
        return bitmap;
    }

    class SendRecordsAsyncTask extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            //showImageView.setImageBitmap(null);

            showProgressBar();//显示进度条提示框

        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            String result=sendRecord();
            //String strResult= HttpUtils.submitPostData(params[0], params[1], params[2]);
            //String result=getRecords();
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if(result!=null){

                System.out.println("result");
                System.out.println(result);
                showResult(result);
                dismissProgressBar();

            }
        }



    }

    class GetRecordsAsyncTask extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            //showImageView.setImageBitmap(null);

            showProgressBar();//显示进度条提示框

        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            String result=HttpUtils.get(params[0]);

            //String result=getRecords();
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if(result!=null){
                dismissProgressBar();
                //showImageView.setImageBitmap(result);
                System.out.println(result);
            }
        }



    }

    /**
     * 在母布局中间显示进度条
     */
    private void showProgressBar(){
        progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleLarge);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT,  RelativeLayout.TRUE);
        progressBar.setVisibility(View.VISIBLE);
        Context context = getApplicationContext();
        viewGroup = (ViewGroup)findViewById(R.id.track_risk_view);
        //      MainActivity.this.addContentView(progressBar, params);
        viewGroup.addView(progressBar, params);
    }
    /**
     * 隐藏进度条
     */
    private void dismissProgressBar(){
        if(progressBar != null){
            progressBar.setVisibility(View.GONE);
            viewGroup.removeView(progressBar);
            progressBar = null;
        }

    }

    private void showResult(String result){

        Context context = getApplicationContext();
        Toast.makeText(context,(result.equals("-1"))?"上传失败，请检查网络！":result,Toast.LENGTH_SHORT).show();

        if(result.contains("成功")){
            if (tv_exception != null) {
                tv_exception.setText("");
            }
            Bimp.tempSelectBitmap.clear();
            adapter.loading();
        }

    }

    @SuppressLint("HandlerLeak")
    public class GridAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private int selectedPosition = -1;
        private boolean shape;

        public boolean isShape() {
            return shape;
        }

        public void setShape(boolean shape) {
            this.shape = shape;
        }

        public GridAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        public void update() {
            loading();
        }

        public int getCount() {
            if(Bimp.tempSelectBitmap.size() == 9){
                return 9;
            }
            return (Bimp.tempSelectBitmap.size() + 1);
        }

        public Object getItem(int arg0) {
            return null;
        }

        public long getItemId(int arg0) {
            return 0;
        }

        public void setSelectedPosition(int position) {
            selectedPosition = position;
        }

        public int getSelectedPosition() {
            return selectedPosition;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_published_grida,
                        parent, false);
                holder = new ViewHolder();
                holder.image = (ImageView) convertView
                        .findViewById(R.id.item_grida_image);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (position ==Bimp.tempSelectBitmap.size()) {
                holder.image.setImageBitmap(BitmapFactory.decodeResource(
                        getResources(), R.mipmap.icon_addpic_unfocused));
                if (position == 9) {
                    holder.image.setVisibility(View.GONE);
                }
            } else {
                holder.image.setImageBitmap(Bimp.tempSelectBitmap.get(position).getBitmap());
            }

            return convertView;
        }

        public class ViewHolder {
            public ImageView image;
        }

        Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        adapter.notifyDataSetChanged();
                        break;
                }
                super.handleMessage(msg);
            }
        };

        public void loading() {
            new Thread(new Runnable() {
                public void run() {
                    while (true) {
                        if (Bimp.max == Bimp.tempSelectBitmap.size()) {
                            Message message = new Message();
                            message.what = 1;
                            handler.sendMessage(message);
                            break;
                        } else {
                            Bimp.max += 1;
                            Message message = new Message();
                            message.what = 1;
                            handler.sendMessage(message);
                        }
                    }
                }
            }).start();
        }
    }

    public String getString(String s) {
        String path = null;
        if (s == null)
            return "";
        for (int i = s.length() - 1; i > 0; i++) {
            s.charAt(i);
        }
        return path;
    }
    protected void onRestart() {
        adapter.update();
        super.onRestart();
    }

    private static final int TAKE_PICTURE = 0x000001;

    public void photo() {

        //try {

            Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, imageFileUri);
            intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(
                Environment.getExternalStorageDirectory(), "temp.jpg")));
            startActivityForResult(intentFromCapture, RESULT_CAMERA_CAPTURE);
        //} catch (Exception ex) {
        //    Toast.makeText(this, "无法调用本地照相程序", Toast.LENGTH_LONG).show();
        //}

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PICTURE:
                if (Bimp.tempSelectBitmap.size() < 9 && resultCode == RESULT_OK) {

                    String fileName = DateUtils.formatDate("yyyyMMddHHmmss", new Date(System.currentTimeMillis()));

                    Bitmap bm = (Bitmap) data.getExtras().get("data");
                    System.out.println("TAKE_PICTURE");
                    System.out.println(bm);
                    PhotoFileUtils.saveBitmap(bm, fileName);

                    ImageItem takePhoto = new ImageItem();
                    takePhoto.setBitmap(bm);
                    Bimp.tempSelectBitmap.add(takePhoto);

                }
                break;
            case RESULT_CAMERA_CAPTURE:
                if (Bimp.tempSelectBitmap.size() < 9 && resultCode == RESULT_OK) {

                    String fileName = DateUtils.formatDate("yyyyMMddHHmmss", new Date(System.currentTimeMillis()));
                    System.out.println("RESULT_CAMERA_CAPTURE");

                    Bitmap bitmap =ImageUtils.getSmallBitmap(Environment.getExternalStorageDirectory()+"/temp.jpg");
                    System.out.println(bitmap);
                    PhotoFileUtils.saveBitmap(bitmap, fileName);
                    ImageItem takePhoto = new ImageItem();
                    takePhoto.setBitmap(bitmap);
                    Bimp.tempSelectBitmap.add(takePhoto);


                }
                break;
            case RISK_RESULT:
                String countryCode="",name="",trUUID="";
                try{
                    countryCode = data.getStringExtra(RiskListActivity.RESULT_RISKCODE);
                    trUUID=data.getStringExtra(RiskListActivity.RESULT_RISKUUID);
                    name=data.getStringExtra(RiskListActivity.RESULT_RISKNAME);
                    System.out.println(countryCode+"---"+name);
                }catch (Exception e){}
                if(!countryCode.equals("")) {
                    tv_risk_value.setText(name);
                    recordMap.put("riskID", countryCode);
                    recordMap.put("riskUUID", trUUID);
                    Toast.makeText(this, "You selected : " + countryCode+name, Toast.LENGTH_SHORT).show();
                }
                break;

        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            for(int i=0;i<PublicWay.activityList.size();i++){
                if (null != PublicWay.activityList.get(i)) {
                    PublicWay.activityList.get(i).finish();
                }
            }
            System.exit(0);
        }
        return true;
    }
    /**
     * 监听popupwindow取消
     */
    private PopupWindow.OnDismissListener dismissListener=new PopupWindow.OnDismissListener() {
        @Override
        public void onDismiss() {
            setTextImage(R.mipmap.icon_down);
        }
    };


    /**
     * popupwindow显示的ListView的item点击事件
     */
    private OnItemClickListener itemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
            mSpinerPopWindow.dismiss();

            tv_trouble_type.setText(PatrolUtils.ttList.get(position));
            RISKTYPE = HashUtils.keyString(PatrolUtils.troubleType,PatrolUtils.ttList.get(position));

            Toast.makeText(TrackRiskActivity.this, "选择了:" +RISKTYPE+"--"+ PatrolUtils.ttList.get(position),Toast.LENGTH_SHORT).show();

            /*new Thread(new Runnable() {
                @Override
                public void run() {
                    // 执行具体的任务
                    System.out.println("执行具体的任务");
                    riskBinder.initRiskUrl();
                    troubleList=riskBinder.getRisksListING();
                }
            }).start();*/

        }
    };

    /**
     * 显示PopupWindow
     */
    private OnClickListener clickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.trouble_type_view:
                    mSpinerPopWindow.setWidth(tv_trouble_type.getWidth());
                    mSpinerPopWindow.showAsDropDown(tv_trouble_type);
                    setTextImage(R.mipmap.icon_up);
                    break;
                case R.id.tv_risk_value:
                    if(!RISKTYPE.equals("risktype")) {
                        Bundle bundle = new Bundle();
                        bundle.putParcelableArrayList("troubleList", troubleList);
                        //riskViewIntent.putExtras(bundle);
                        //riskViewIntent.putExtra("troubleList",troubleList);
                        startActivityForResult(riskViewIntent, RISK_RESULT);
                    }
                    else {
                        Toast.makeText(TrackRiskActivity.this, "请先选择隐患类型！",Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };



    /**
     * 给TextView右边设置图片
     * @param resId
     */
    private void setTextImage(int resId) {
        Drawable drawable = getResources().getDrawable(resId);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(),drawable.getMinimumHeight());// 必须设置图片大小，否则不显示
        tv_trouble_type.setCompoundDrawables(null, null, drawable, null);
    }
}
