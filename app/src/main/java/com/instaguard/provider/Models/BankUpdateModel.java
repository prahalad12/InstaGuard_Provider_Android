package com.instaguard.provider.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by system7 on 10/11/2017.
 */

public class BankUpdateModel {

    @SerializedName("data")
    @Expose
    private BankUpdateData BankUpdateData;
    @SerializedName("message")
    @Expose
    private String message;

    public BankUpdateData getBankUpdateData() {
        return BankUpdateData;
    }

    public void setBankUpdateData(BankUpdateData BankUpdateData) {
        this.BankUpdateData = BankUpdateData;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}