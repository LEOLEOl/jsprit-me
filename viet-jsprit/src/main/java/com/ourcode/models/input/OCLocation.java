package com.ourcode.models.input;

import com.google.gson.annotations.SerializedName;
import com.graphhopper.jsprit.core.problem.Location;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Tung
 * Date: 10/03/17
 * Time: 2:16 PM
 * To change this template use File | Settings | File Templates.
 */

public class OCLocation
{
    @SerializedName("locationCode")
    private String locationCode; // Code of location

    @SerializedName("locationName")
    private String locationName; // Name of location

    //@Deprecated
    @SerializedName("timeWindows")
    private ArrayList<OCTimeWindow> timeWindows; // (in hours), (the time count from optimizing time), Open hours of the location

    Location j_location;

    ////// Getter and Setter method
    public String getLocationName() {return locationName;}
    public String getLocationCode() {return locationCode;}

    public ArrayList<OCTimeWindow> getTimeWindows() {
        ArrayList<OCTimeWindow> ret = new ArrayList<>();
        for (OCTimeWindow timeWindow: timeWindows)
            ret.add(new OCTimeWindow(timeWindow));
        return ret;
    }

    public Location _getJ_location() {return j_location;}


    // Constructor
    public OCLocation(String code, String name)
    {
        this.locationCode = code;
        this.locationName = name;
        this.timeWindows = new ArrayList<>();
    }

    public OCLocation build()
    {
        Location.Builder builder = Location.Builder.newInstance();
        builder.setId(this.locationCode).setName(this.locationName);
        j_location = builder.build();
        return this;
    }
}
