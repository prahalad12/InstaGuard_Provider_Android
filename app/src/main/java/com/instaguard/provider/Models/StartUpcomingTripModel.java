package com.instaguard.provider.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by system7 on 10/11/2017.
 */

public class StartUpcomingTripModel {


    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("service_request")
    @Expose
    private StartUpcomingTripData serviceRequest;
    @SerializedName("message")
    @Expose
    private String message;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public StartUpcomingTripData getServiceRequest() {
        return serviceRequest;
    }

    public void setServiceRequest(StartUpcomingTripData serviceRequest) {
        this.serviceRequest = serviceRequest;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}