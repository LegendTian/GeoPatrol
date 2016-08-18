package com.al.app.geopatrol.model;

/**
 * Created by txy on 2016-7-21.
 */
public class RiskTrack {
    // Fields
    private static final long serialVersionUID = 1L;
    private String trackID;// 编号
    private String riskGUID;// 隐患GUID编号
    private String riskID;// 隐患编号
    private String employeeID;// 员工编号

    private String keyuan;// 上报人

    private String recordDate;// 日期时间
    private String exception;// 异常信息

    private String imageType;//图片类型
    private String imageLabel;//图片标签

    private String postState;//是否上传成功
    // Constructors

    /** default constructor */
    public RiskTrack() {
    }

    public RiskTrack(String id){
        this();
        this.trackID = id;
    }


    public String getTrackID() {
        return this.trackID;
    }

    public void setTrackID(String record_id) {
        this.trackID = record_id;
    }


    public String getEmployeeID() {
        return this.employeeID;
    }

    public void setEmployeeID(String Employee_id) {
        this.employeeID = Employee_id;
    }


    public String getRiskGUID() {
        return this.riskGUID;
    }

    public void setRiskGUID(String Pipeline_id) {
        this.riskGUID = Pipeline_id;
    }


    public String getRiskID() {
        return this.riskID;
    }

    public void setRiskID(String Instrument_id) {
        this.riskID = Instrument_id;
    }


    public String getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(String record_date) {
        this.recordDate = record_date;
    }


    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public String getImageType() {
        return imageType;
    }

    public void setImageType(String picUrl) {
        this.imageType = picUrl;
    }

    public String getKeyuan() {
        return keyuan;
    }
    public void setKeyuan(String jpm) {
        this.keyuan = jpm;
    }
    public String getImageLabel() {
        return imageLabel;
    }
    public void setImageLabel(String jpm) {
        this.imageLabel = jpm;
    }


    public String getPostState(){return this.postState;}

    public void setPostState(String ps){
        this.postState=ps;
    }
}
