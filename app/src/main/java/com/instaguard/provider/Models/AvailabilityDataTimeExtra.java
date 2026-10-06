package com.instaguard.provider.Models;

import java.util.List;

/**
 * Created by Uplogic on 21/04/2018.
 */

public class AvailabilityDataTimeExtra {
    public String text;
    public boolean selected;
    public int size;
    public List<Integer> list;
    public AvailabilityDataTimeExtra(String text, boolean selected) {
        this.text = text;
        this.selected = selected;
       // this.list = list;

    }

}