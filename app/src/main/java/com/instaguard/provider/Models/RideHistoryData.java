package com.instaguard.provider.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Uplogic on 21/04/2018.
 */

public class RideHistoryData implements Serializable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("job_id")
    @Expose
    private Integer jobId;
    @SerializedName("driver_id")
    @Expose
    private Integer driverId;
    @SerializedName("accept_time")
    @Expose
    private Object acceptTime;
    @SerializedName("start_time")
    @Expose
    private Object startTime;
    @SerializedName("reach_time")
    @Expose
    private Object reachTime;
    @SerializedName("service_start_time")
    @Expose
    private Object serviceStartTime;
    @SerializedName("deducation_status")
    @Expose
    private Object deducationStatus;
    @SerializedName("deducation_amount")
    @Expose
    private Object deducationAmount;
    @SerializedName("deducation_interval")
    @Expose
    private Object deducationInterval;
    @SerializedName("bonus_status")
    @Expose
    private Object bonusStatus;
    @SerializedName("bonus_amount")
    @Expose
    private Object bonusAmount;
    @SerializedName("bonus_interval")
    @Expose
    private Object bonusInterval;
    @SerializedName("req_provider_ids")
    @Expose
    private Object reqProviderIds;
    @SerializedName("provider_ids")
    @Expose
    private Object providerIds;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getJobId() {
        return jobId;
    }

    public void setJobId(Integer jobId) {
        this.jobId = jobId;
    }

    public Integer getDriverId() {
        return driverId;
    }

    public void setDriverId(Integer driverId) {
        this.driverId = driverId;
    }

    public Object getAcceptTime() {
        return acceptTime;
    }

    public void setAcceptTime(Object acceptTime) {
        this.acceptTime = acceptTime;
    }

    public Object getStartTime() {
        return startTime;
    }

    public void setStartTime(Object startTime) {
        this.startTime = startTime;
    }

    public Object getReachTime() {
        return reachTime;
    }

    public void setReachTime(String reachTime) {
        this.reachTime = reachTime;
    }

    public Object getServiceStartTime() {
        return serviceStartTime;
    }

    public void setServiceStartTime(Object serviceStartTime) {
        this.serviceStartTime = serviceStartTime;
    }

    public Object getDeducationStatus() {
        return deducationStatus;
    }

    public void setDeducationStatus(Object deducationStatus) {
        this.deducationStatus = deducationStatus;
    }

    public Object getDeducationAmount() {
        return deducationAmount;
    }

    public void setDeducationAmount(Object deducationAmount) {
        this.deducationAmount = deducationAmount;
    }

    public Object getDeducationInterval() {
        return deducationInterval;
    }

    public void setDeducationInterval(Object deducationInterval) {
        this.deducationInterval = deducationInterval;
    }

    public Object getBonusStatus() {
        return bonusStatus;
    }

    public void setBonusStatus(Object bonusStatus) {
        this.bonusStatus = bonusStatus;
    }

    public Object getBonusAmount() {
        return bonusAmount;
    }

    public void setBonusAmount(Object bonusAmount) {
        this.bonusAmount = bonusAmount;
    }

    public Object getBonusInterval() {
        return bonusInterval;
    }

    public void setBonusInterval(String bonusInterval) {
        this.bonusInterval = bonusInterval;
    }

    public Object getReqProviderIds() {
        return reqProviderIds;
    }

    public void setReqProviderIds(Object reqProviderIds) {
        this.reqProviderIds = reqProviderIds;
    }

    public Object getProviderIds() {
        return providerIds;
    }

    public void setProviderIds(Object providerIds) {
        this.providerIds = providerIds;
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

}