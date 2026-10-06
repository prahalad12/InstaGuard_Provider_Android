package com.instaguard.provider.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class AvailabilityDayModel {

    @SerializedName("Sunday")
    @Expose
    private List<Integer> Sunday = null;
    @SerializedName("Monday")
    @Expose
    private List<Integer> Monday = null;
    @SerializedName("Tuesday")
    @Expose
    private List<Integer> Tuesday = null;
    @SerializedName("Wednesday")
    @Expose
    private List<Integer> Wednesday = null;
    @SerializedName("Thursday")
    @Expose
    private List<Integer> Thursday = null;
    @SerializedName("Friday")
    @Expose
    private List<Integer> Friday = null;
     @SerializedName("Saturday")
    @Expose
    private List<Integer> Saturday = null;

    public List<Integer> getMonday() {
        return Monday;
    }

    public void setMonday(List<Integer> monday) {
        this.Monday = monday;
    }

    public List<Integer> getTuesday() {
        return Tuesday;
    }
    public void setTuesday(List<Integer> tuesday) {
        this.Tuesday = tuesday;
    }

    public void setWednesday(List<Integer> wednesday) {
        this.Wednesday = wednesday;
    }
    public List<Integer> getWednesday() {
        return Wednesday;
    }

    public void setThursday(List<Integer> thursday) {
        this.Thursday = thursday;
    }
    public List<Integer> getThursday() {
        return Thursday;
    }

    public void setFriday(List<Integer> friday) {
        this.Friday = friday;
    }
    public List<Integer> getFriday() {
        return Tuesday;
    }

    public void setSaturday(List<Integer> saturday) {
        this.Saturday = saturday;
    }
    public List<Integer> getSaturday() {
        return Saturday;
    }

}