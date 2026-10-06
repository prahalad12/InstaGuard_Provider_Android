package com.instaguard.provider.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by uplogic on 18/04/2018.
 */

public class LoginResults {

    @SerializedName("result")
    @Expose
    private LoginModel result;
    @SerializedName("message")
    @Expose
    private String message;

    public LoginModel getResult() {
        return result;
    }

    public void setResult(LoginModel result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}