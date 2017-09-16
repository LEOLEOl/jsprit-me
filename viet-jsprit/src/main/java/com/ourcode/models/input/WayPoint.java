package com.ourcode.models.input;

import com.google.gson.annotations.SerializedName;
import com.ourcode.models.input.MidPoint;

import java.util.ArrayList;

/**
 * Created by LEOLEOl on 5/26/2017.
 */
public class WayPoint {
    @SerializedName("from")
    private String from;

    @SerializedName("to")
    private String to;

    @SerializedName("midPoints")
    private ArrayList<MidPoint> midPoints;

    public String getFrom() {return from;}
    public String getTo() {return to;}
    public ArrayList<MidPoint> _getMidPoints() {return midPoints;}
}
