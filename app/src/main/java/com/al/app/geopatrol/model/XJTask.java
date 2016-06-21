package com.al.app.geopatrol.model;

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * Created by TXY on 2016-5-19.
 */
public class XJTask {
    // Fields
    private static final long serialVersionUID = 1L;
    private String missionID;// 编号
    private String pipelineID;// 管线编号
    private String pipelineName;// 管线名称
    private String beginEndIndex;// 起始点索引 STARTVERTEXINDEX
    private String employeeID;// 员工编号
    private String employeeName;// 员工
    private String instrumentID;// 巡线仪编号
    private Double startM;
    private Double endM;
    private String missionName;// 名称
    private String sector;// 所属部门
    private String  unit;// 所属单位
    // Constructors

    /** default constructor */
    public XJTask() {
    }

    public XJTask(String id){
        this();
        this.missionID = id;
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


    public String getPipelineName() {
        return pipelineName;
    }

    public void setPipelineName(String exception) {
        this.pipelineName = exception;
    }
    public String getBeginEndIndex() {
        return beginEndIndex;
    }

    public void setBeginEndIndex(String exception) {
        this.beginEndIndex = exception;
    }

    public String getEmployeeID() {
        return this.employeeID;
    }

    public void setEmployeeID(String Employee_id) {
        this.employeeID = Employee_id;
    }
    public String getEmployeeName() {
        return this.employeeName;
    }

    public void setEmployeeName(String Employee_id) {
        this.employeeName = Employee_id;
    }

    public String getInstrumentID() {
        return this.instrumentID;
    }

    public void setInstrumentID(String Instrument_id) {
        this.instrumentID = Instrument_id;
    }
    public Double getStartM() {
        return startM;
    }

    public void setStartM(Double x) {
        this.startM = x;
    }

    public Double getEndM() {
        return endM;
    }
    public void setEndM(Double y) {
        this.endM = y;
    }

    public String getMissionName() {
        return this.missionName;
    }

    public void setMissionName(String Instrument_id) {
        this.missionName = Instrument_id;
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
}
