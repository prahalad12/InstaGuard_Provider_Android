package com.instaguard.provider.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by system7 on 10/11/2017.
 */

public class ProfileModel {

    @SerializedName("data")
    @Expose
    private ProfileData ProfileData;
    @SerializedName("rating")
    @Expose
    private Float rating;
    @SerializedName("bonus_earning")
    @Expose
    private Object bonusEarning;
    @SerializedName("message")
    @Expose
    private String message;

    public ProfileData getProfileData() {
        return ProfileData;
    }

    public void setProfileData(ProfileData ProfileData) {
        this.ProfileData = ProfileData;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getBonusEarning() {
        return bonusEarning;
    }

    public void setBonusEarning(Object bonusEarning) {
        this.bonusEarning = bonusEarning;
    }
}