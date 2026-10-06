package com.instaguard.provider.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by system7 on 10/11/2017.
 */

public class AvailabilityData {
    @SerializedName("service_ids")
    @Expose
    private Object serviceIds;
    @SerializedName("availble_slots")
    @Expose
    private AvailbleSlots availbleSlots;

    public Object getServiceIds() {
        return serviceIds;
    }

    public void setServiceIds(Object serviceIds) {
        this.serviceIds = serviceIds;
    }

    public AvailbleSlots getAvailbleSlots() {
        return availbleSlots;
    }

    public void setAvailbleSlots(AvailbleSlots availbleSlots) {
        this.availbleSlots = availbleSlots;
    }

}