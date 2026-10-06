package com.instaguard.provider.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Uplogic on 21/04/2018.
 */

public class AvailabilityDataExtra {
    public String text;
    public int size;
    public List<Integer> list;
    public AvailabilityDataExtra(String text, int size,List<Integer> list) {
        this.text = text;
        this.size = size;
        this.list = list;

    }

}