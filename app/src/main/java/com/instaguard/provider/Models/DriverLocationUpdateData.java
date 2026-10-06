package com.instaguard.provider.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by system7 on 10/11/2017.
 */

public class DriverLocationUpdateData {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("driver_id")
    @Expose
    private Integer driverId;
    @SerializedName("driver_first_name")
    @Expose
    private String driverFirstName;
    @SerializedName("driver_last_name")
    @Expose
    private String driverLastName;
    @SerializedName("checkin_time")
    @Expose
    private String checkinTime;
    @SerializedName("checkin_date")
    @Expose
    private String checkinDate;
    @SerializedName("driver_current_lat")
    @Expose
    private Float driverCurrentLat;
    @SerializedName("driver_current_lon")
    @Expose
    private Float driverCurrentLon;
    @SerializedName("checkin_status")
    @Expose
    private Integer checkinStatus;
    @SerializedName("waiting_status")
    @Expose
    private Integer waitingStatus;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("document_name")
    @Expose
    private String documentName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDriverId() {
        return driverId;
    }

    public void setDriverId(Integer driverId) {
        this.driverId = driverId;
    }

    public String getDriverFirstName() {
        return driverFirstName;
    }

    public void setDriverFirstName(String driverFirstName) {
        this.driverFirstName = driverFirstName;
    }

    public String getDriverLastName() {
        return driverLastName;
    }

    public void setDriverLastName(String driverLastName) {
        this.driverLastName = driverLastName;
    }

    public String getCheckinTime() {
        return checkinTime;
    }

    public void setCheckinTime(String checkinTime) {
        this.checkinTime = checkinTime;
    }

    public String getCheckinDate() {
        return checkinDate;
    }

    public void setCheckinDate(String checkinDate) {
        this.checkinDate = checkinDate;
    }

    public Float getDriverCurrentLat() {
        return driverCurrentLat;
    }

    public void setDriverCurrentLat(Float driverCurrentLat) {
        this.driverCurrentLat = driverCurrentLat;
    }

    public Float getDriverCurrentLon() {
        return driverCurrentLon;
    }

    public void setDriverCurrentLon(Float driverCurrentLon) {
        this.driverCurrentLon = driverCurrentLon;
    }

    public Integer getCheckinStatus() {
        return checkinStatus;
    }

    public void setCheckinStatus(Integer checkinStatus) {
        this.checkinStatus = checkinStatus;
    }

    public Integer getWaitingStatus() {
        return waitingStatus;
    }

    public void setWaitingStatus(Integer waitingStatus) {
        this.waitingStatus = waitingStatus;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }
}