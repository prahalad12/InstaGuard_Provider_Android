package com.instaguard.provider.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by system7 on 10/11/2017.
 */

public class AvailabilityModel {

    @SerializedName("data")
    @Expose
    private AvailabilityData data;
    @SerializedName("message")
    @Expose
    private String message;

    public AvailabilityData getAvailabilityData() {
        return data;
    }

    public void setAvailabilityData(AvailabilityData data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}