package com.instaguard.provider.Models.SocialLogModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by uplogic on 18/04/2018.
 */

public class SocialLoginModel {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("result")
    @Expose
    private SocialLoginDataModel result;
    @SerializedName("msg")
    @Expose
    private String msg;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public SocialLoginDataModel getSocialLoginDataModel() {
        return result;
    }

    public void setResult(SocialLoginDataModel result) {
        this.result = result;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}