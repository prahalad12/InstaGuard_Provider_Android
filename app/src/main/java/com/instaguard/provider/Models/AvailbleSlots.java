package com.instaguard.provider.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by system7 on 10/11/2017.
 */

public class AvailbleSlots {
    @SerializedName("Monday")
    @Expose
    private List<Integer> monday = null;
    @SerializedName("Tuesday")
    @Expose
    private List<Integer> tuesday = null;
    @SerializedName("Wednesday")
    @Expose
    private List<Integer> wednesday = null;
    @SerializedName("Thursday")
    @Expose
    private List<Integer> thursday = null;
    @SerializedName("Friday")
    @Expose
    private List<Integer> friday = null;
    @SerializedName("Saturday")
    @Expose
    private List<Integer> saterday = null;
    @SerializedName("Sunday")
    @Expose
    private List<Integer> sunday = null;

    public List<Integer> getMonday() {
        return monday;
    }

    public void setMonday(List<Integer> monday) {
        this.monday = monday;
    }

    public List<Integer> getTuesday() {
        return tuesday;
    }

    public void setTuesday(List<Integer> tuesday) {
        this.tuesday = tuesday;
    }

    public List<Integer> getWednesday() {
        return wednesday;
    }

    public void setWednesday(List<Integer> wednesday) {
        this.wednesday = wednesday;
    }

    public List<Integer> getThursday() {
        return thursday;
    }

    public void setThursday(List<Integer> thursday) {
        this.thursday = thursday;
    }

    public List<Integer> getFriday() {
        return friday;
    }

    public void setFriday(List<Integer> friday) {
        this.friday = friday;
    }

    public List<Integer> getSaterday() {
        return saterday;
    }

    public void setSaterday(List<Integer> saterday) {
        this.saterday = saterday;
    }

    public List<Integer> getSunday() {
        return sunday;
    }

    public void setSunday(List<Integer> sunday) {
        this.sunday = sunday;
    }

}