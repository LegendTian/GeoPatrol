package com.al.app.geopatrol.model;

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * Created by admin on 2016-5-19.
 */
public class XJTaskInfo {
    // Fields
    private static final long serialVersionUID = 1L;
    private String missionID;// 编号

    private String missionName;// 名称
    private String sector;// 所属部门
    private String  unit;// 所属单位


    // Constructors

    /** default constructor */
    public XJTaskInfo() {
    }

    public XJTaskInfo(String id){
        this();
        this.missionID = id;
    }

    public String getMissionID() {
        return this.missionID;
    }

    public void setMissionID(String record_id) {
        this.missionID = record_id;
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
