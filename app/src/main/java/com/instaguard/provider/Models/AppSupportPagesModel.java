package com.instaguard.provider.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AppSupportPagesModel {

    @SerializedName("data")
    @Expose
    private List<com.instaguard.provider.Models.AppSupportPagesData> data = null;
    @SerializedName("message")
    @Expose
    private String message;

    public List<com.instaguard.provider.Models.AppSupportPagesData> getData() {
        return data;
    }

    public void setData(List<com.instaguard.provider.Models.AppSupportPagesData> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}