package com.instaguard.provider.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Uplogic on 27/04/2018.
 */

public class UploadDocModel_ {

    @SerializedName("data")
    @Expose
    private UploadDocData_ UploadDocData;
    @SerializedName("message")
    @Expose
    private String message;

    public UploadDocData_ getUploadDocData() {
        return UploadDocData;
    }

    public void setData(UploadDocData_ UploadDocData_) {
        this.UploadDocData = UploadDocData_;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}