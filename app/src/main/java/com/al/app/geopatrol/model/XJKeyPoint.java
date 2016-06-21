package com.al.app.geopatrol.model;

/**
 * Created by admin on 2016-5-19.
 */
public class XJKeyPoint {
    // Fields
    private static final long serialVersionUID = 1L;
    private String pointID;// 编号

    private String pointName;// 名称
    private String pipelineID;// 管线编号

    private Double x;// 经度
    private Double y;// 纬度

    private String pipelineName;// 管线名称
    private String missionID;//任务编号
    private String JPM;//桩+里程
    private String descriptions;
    // Constructors

    /** default constructor */
    public XJKeyPoint() {
    }

    public XJKeyPoint(String id){
        this();
        this.pointID = id;
    }


    public String getPointID() {
        return this.pointID;
    }

    public void setPointID(String record_id) {
        this.pointID = record_id;
    }

    public String getMissionID() {
        return this.missionID;
    }

    public void setMissionID(String record_id) {
        this.missionID = record_id;
    }
    public String getPipelineID() {
        return this.pipelineID;
    }

    public void setPipelineID(String Pipeline_id) {
        this.pipelineID = Pipeline_id;
    }

    public String getPointName() {
        return this.pointName;
    }

    public void setPointName(String Instrument_id) {
        this.pointName = Instrument_id;
    }
    public String getInfo() {
        return descriptions;
    }
    public void setInfo(String desc) {
        this.descriptions = desc;
    }
    public String getJPM() {
        return JPM;
    }
    public void setJPM(String jpm) {
        this.JPM = jpm;
    }

    public String getPipelineName() {
        return pipelineName;
    }

    public void setPipelineName(String exception) {
        this.pipelineName = exception;
    }

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }


    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }

}
