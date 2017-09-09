package com.ourcode.models.input;

import com.google.gson.annotations.SerializedName;

/**
 * Created by LEOLEOl on 5/26/2017.
 */
public class MidPoint {
    @SerializedName("point")
    public String point;

    @SerializedName("distanceFromRoot")
    public double distanceFromRoot; // (in kilometers)
}
