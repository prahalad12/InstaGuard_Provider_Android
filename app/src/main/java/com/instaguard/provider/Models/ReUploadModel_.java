package com.instaguard.provider.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by system7 on 11/6/2017.
 */

public class ReUploadModel_ {

    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("result")
    @Expose
    private com.instaguard.provider.Models.ReUploadData_ result;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public com.instaguard.provider.Models.ReUploadData_ getResult() {
        return result;
    }

    public void setResult(com.instaguard.provider.Models.ReUploadData_ result) {
        this.result = result;
    }

}