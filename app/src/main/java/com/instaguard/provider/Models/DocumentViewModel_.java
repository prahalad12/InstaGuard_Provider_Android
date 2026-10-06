package com.instaguard.provider.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by system7 on 11/6/2017.
 */

public class DocumentViewModel_ {

    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("result")
    @Expose
    private List<DocumentViewData_> result = null;
    @SerializedName("reject_status")
    @Expose
    private Integer rejectStatus;

    public Integer getRejectStatus() {
        return rejectStatus;
    }

    public void setRejectStatus(Integer rejectStatus) {
        this.rejectStatus = rejectStatus;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<DocumentViewData_> getResult() {
        return result;
    }

    public void setResult(List<DocumentViewData_> result) {
        this.result = result;
    }

}