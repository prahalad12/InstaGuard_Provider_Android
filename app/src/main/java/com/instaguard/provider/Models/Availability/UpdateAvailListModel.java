package com.instaguard.provider.Models.Availability;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UpdateAvailListModel {

    @SerializedName("result")
    @Expose
    private UpdateAvailListData result;
    @SerializedName("message")
    @Expose
    private String message;

    public UpdateAvailListData getResult() {
        return result;
    }

    public void setResult(UpdateAvailListData result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}