package com.instaguard.provider.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by system7 on 10/11/2017.
 */

public class AvailabilitySubModel {


    @SerializedName("data")
    @Expose
    private AvailabilitySubData data;
    @SerializedName("message")
    @Expose
    private String message;

    public AvailabilitySubData getData() {
        return data;
    }

    public void setData(AvailabilitySubData data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}