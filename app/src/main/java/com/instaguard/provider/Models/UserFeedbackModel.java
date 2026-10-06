package com.instaguard.provider.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class UserFeedbackModel{

    @SerializedName("data")
    @Expose
    private List<UserFeedbackDataModel> UserFeedbackDataModel = null;
    @SerializedName("message")
    @Expose
    private String message;

    public List<UserFeedbackDataModel> getUserFeedbackDataModel() {
        return UserFeedbackDataModel;
    }

    public void setUserFeedbackDataModel(List<UserFeedbackDataModel> UserFeedbackDataModel) {
        this.UserFeedbackDataModel = UserFeedbackDataModel;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}