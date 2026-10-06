package com.instaguard.provider.Models;

import java.util.List;

/**
 * Created by Uplogic on 21/04/2018.
 */

public class AvailabilitySubDataExtra {
    public String text;
    public int size;
    public List<Integer> list;
    public AvailabilitySubDataExtra(String text, int size, List<Integer> list) {
        this.text = text;
        this.size = size;
        this.list = list;

    }

}