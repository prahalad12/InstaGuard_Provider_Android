package com.instaguard.provider.Models.Availability;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ViewAvailListModel {

    @SerializedName("result")
    @Expose
    private List<ViewAvailListData> result = null;
    @SerializedName("message")
    @Expose
    private String message;

    public List<ViewAvailListData> getResult() {
        return result;
    }

    public void setResult(List<ViewAvailListData> result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}