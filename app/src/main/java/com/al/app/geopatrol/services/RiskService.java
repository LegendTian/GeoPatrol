package com.al.app.geopatrol.services;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Binder;
import android.os.IBinder;

import com.al.app.geopatrol.R;
import com.al.app.geopatrol.activitys.TrackRiskActivity;
import com.al.app.geopatrol.model.XJKeyPoint;
import com.al.app.geopatrol.model.XJTask;
import com.al.app.geopatrol.model.XJTrouble;
import com.al.app.geopatrol.utils.HttpUtils;
import com.al.app.geopatrol.utils.Res;
import com.google.common.collect.Lists;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RiskService extends Service {
    public static final String TAG = "RiskService";

    private RiskBinder riskBinder = new RiskBinder();

    private String riskUrl="";
    public RiskService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.

        return riskBinder;
    }





    public class RiskBinder extends Binder {
        private XJTrouble risk;
        private Map<String,String> map;
        public void initRiskUrl(){
            System.out.println("initRiskUrl");
            switch (TrackRiskActivity.RISKTYPE) {
                case "A":
                    riskUrl=Res.getString("app_server")+ "trouBuild/";
                    break;
                case "B":

                    riskUrl=Res.getString("app_server")+ "trouBuild/";
                    break;
            }
            System.out.println(riskUrl);
        }
        public String getRiskByID(String rid){
            String target = riskUrl + rid;

            String result= HttpUtils.get(target);

            return result;
        }
        public String getRisks(){

            String target = riskUrl + "lists";

            String result= HttpUtils.get(target);
            System.out.println("result");
            System.out.println(result);


            /*try {
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
*/
            return result;
        }
        public String getRisksByOfficeID(String officeId,String flag){

            String target = riskUrl + "processlist/"+officeId+"/"+flag;

            String result= HttpUtils.get(target);
            System.out.println("result");
            System.out.println(result);

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
                System.out.println(jsonArray.get(jsonArray.length() - 1));
            }
            catch (JSONException e){
                System.out.println("erroooooooooooooe");
                e.printStackTrace();
            }
            return result;

        }
        public JSONArray getRisksArrayING(String oid,String flag){

            String result= getRisksByOfficeID(oid,flag);
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
        public ArrayList<XJTrouble> getRisksListING(String oid,String flag){
            ArrayList<XJTrouble> points = Lists.newArrayList();
            try {
                JSONArray jsonArray=getRisksArrayING(oid,flag);

                for(int i=0,ii=jsonArray.length();i<ii;i++){
                    JSONObject jsonObject = (JSONObject)jsonArray.get(i);
                    XJTrouble point=parseRisk(jsonObject);
                    points.add(point);

                    System.out.println("point");
                    System.out.println(point);
                    System.out.println(point.getException());

                }

                return points;
            }
            catch (JSONException e){
                System.out.println("erroooooooooooooe");
                e.printStackTrace();
                return null;
            }

        }

        public JSONArray getImagesByLabel(String label){
            String target = Res.getString("dfs_server")+ "loadData?tag=" + label;

            String result= HttpUtils.get(target);
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

        public Bitmap getImageByLabel(String label){
            JSONArray jsonArray=getImagesByLabel(label);
            try {
                Bitmap bitmap;
                if(jsonArray.length()>0){
                    JSONObject jsonObject = (JSONObject)jsonArray.get(0);
                    String imageID=jsonObject.get("id").toString();
                    String imageUrl=Res.getString("dfs_server")+ "show?id=" + imageID;
                    bitmap= getImageBitmap(imageUrl);

                    if(bitmap==null){
                        bitmap=BitmapFactory.decodeResource(getResources(), R.drawable.norway);
                    }

                }
                else {
                    bitmap=BitmapFactory.decodeResource(getResources(), R.drawable.norway);
                }
                return bitmap;
            }
            catch (JSONException e){
                System.out.println("erroooooooooooooe");
                e.printStackTrace();
                return null;
            }
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
        public XJTrouble parseRisk(JSONObject jsonPoint){
            XJTrouble trouble=new XJTrouble();
            try {
                trouble.setTroubleID(jsonPoint.get("objectId").toString());

                String ti=jsonPoint.get("troubleIn").toString();
                System.out.println("ti"+ti);
                if(!ti.equals("null")){
                    JSONObject troubleIn=(JSONObject) jsonPoint.get("troubleIn");
                    trouble.setRiskGUID(troubleIn.get("objectId").toString());
                }


                trouble.setRiskID(jsonPoint.get("buildId").toString());
                trouble.setFindDate(jsonPoint.get("findDate").toString());
                trouble.setSiteName(jsonPoint.get("sitename").toString());
                trouble.setException(jsonPoint.get("miaoshu").toString());

                trouble.setImage(getImageByLabel(jsonPoint.get("imgsLabel").toString()));


                System.out.println("trouble");
                System.out.println(trouble.getException());
                System.out.println(trouble);
            }
            catch (JSONException e){
                e.printStackTrace();
            }
            return trouble;

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
