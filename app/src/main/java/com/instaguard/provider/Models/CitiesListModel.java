package com.instaguard.provider.Models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CitiesListModel {
    @SerializedName("cities")
    @Expose
    private List<CitiesListData> cities;

    public List<CitiesListData> getCities() {
        return cities;
    }

    public void setCities(List<CitiesListData> cities) {
        this.cities = cities;
    }
}