package com.instaguard.provider.Models.Availability;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NewDateAvailListModel {

    @SerializedName("result")
    @Expose
    private NewDateAvailData result;
    @SerializedName("message")
    @Expose
    private String message;

    public NewDateAvailData getResult() {
        return result;
    }

    public void setResult(NewDateAvailData result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}