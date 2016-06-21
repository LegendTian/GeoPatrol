package com.al.app.geopatrol.model;

import org.codehaus.jackson.annotate.JsonIgnore;

import java.sql.Date;

/**
 * Created by admin on 2016-5-19.
 */
public class XJRecord {
    // Fields
    private static final long serialVersionUID = 1L;
    private String recordID;// 编号
    private String employeeID;// 员工编号
    private String pipelineID;// 管线编号
    private String instrumentID;// 巡线仪编号
    private String recordDate;// 日期时间
    private String exception;// 异常信息
    private Double x;// 经度
    private Double y;// 纬度
    private Double zz;// 高度
    private String abnormalMark;// 异常标志（1：异常；0：正常）
    private String checkMark;// 确认标志（1：已确认；0：未确认）
    private String checker;// 确认人
    private String sector;// 所属部门
    private String unit;// 所属单位
    private String picUrl;//图片路径
    private String JPM;//桩+里程
    private String level;//预警级别
    private String postState;//是否上传成功
    // Constructors

    /** default constructor */
    public XJRecord() {
    }

    public XJRecord(String id){
        this();
        this.recordID = id;
    }


    public String getRecordID() {
        return this.recordID;
    }

    public void setRecordID(String record_id) {
        this.recordID = record_id;
    }


    public String getEmployeeID() {
        return this.employeeID;
    }

    public void setEmployeeID(String Employee_id) {
        this.employeeID = Employee_id;
    }


    public String getPipelineID() {
        return this.pipelineID;
    }

    public void setPipelineID(String Pipeline_id) {
        this.pipelineID = Pipeline_id;
    }


    public String getInstrumentID() {
        return this.instrumentID;
    }

    public void setInstrumentID(String Instrument_id) {
        this.instrumentID = Instrument_id;
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

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getJPM() {
        return JPM;
    }
    public void setJPM(String jpm) {
        this.JPM = jpm;
    }
    public String getLevel() {
        return level;
    }
    public void setLevel(String jpm) {
        this.level = jpm;
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


    public Double getZ() {
        return zz;
    }

    public void setZ(Double z) {
        this.zz = z;
    }


    public String getAbnormalMark() {
        return this.abnormalMark;
    }

    public void setAbnormalMark(String abnormal_mark) {
        this.abnormalMark = abnormal_mark;
    }


    public String getCheckMark() {
        return this.checkMark;
    }

    public void setCheckMark(String check_mark) {
        this.checkMark = check_mark;
    }


    public String getChecker() {
        return this.checker;
    }

    public void setChecker(String Employee_id) {
        this.checker = Employee_id;
    }


    public String getSector() {
        return this.sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }


    public String getUnit() {
        return this.unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getPostState(){return this.postState;}

    public void setPostState(String ps){
        this.postState=ps;
    }
}
