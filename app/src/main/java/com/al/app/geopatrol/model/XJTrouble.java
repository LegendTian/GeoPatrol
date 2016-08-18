package com.al.app.geopatrol.model;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by txy on 2016-7-21.
 */
public class XJTrouble implements Parcelable {
    // Fields
    private static final long serialVersionUID = 1L;
    private String troubleID;// 编号

    private String riskGUID;// 隐患GUID编号
    private String riskID;// 隐患编号

    private String findDate;// 发现时间
    private String exception;// 隐患描述

    private String siteName;// 站场名称

    private Bitmap image;//图片标识
    // Constructors

    /** default constructor */
    public XJTrouble() {
    }
    @SuppressWarnings("unchecked")
    public XJTrouble(Parcel in) {
        // TODO Auto-generated constructor stub
        troubleID = in.readString();
        riskGUID = in.readString();
        riskID = in.readString();
        findDate = in.readString();
        exception = in.readString();
        siteName = in.readString();

        image = in.readParcelable(XJTrouble.class.getClassLoader());  //这个地方的ClassLoader不能为null

    }
    public XJTrouble(String id){
        this();
        this.troubleID = id;
    }


    public String getTroubleID() {
        return this.troubleID;
    }

    public void setTroubleID(String record_id) {
        this.troubleID = record_id;
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

    public String getFindDate() {
        return findDate;
    }

    public void setFindDate(String record_date) {
        this.findDate = record_date;
    }


    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public Bitmap getImage(){return image;}
    public void setImage(Bitmap image){this.image=image;}



    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // TODO Auto-generated method stub
        dest.writeString(troubleID);
        dest.writeString(riskGUID);
        dest.writeString(riskID);
        dest.writeString(findDate);
        dest.writeString(exception);
        dest.writeString(siteName);
        dest.writeParcelable(image, flags);

    }
    public static final Parcelable.Creator<XJTrouble> CREATOR = new Creator<XJTrouble>() {

        @Override
        public XJTrouble[] newArray(int size) {
            // TODO Auto-generated method stub
            return new XJTrouble[size];
        }

        @Override
        public XJTrouble createFromParcel(Parcel source) {
            // TODO Auto-generated method stub
            return new XJTrouble(source);
        }
    };
}
