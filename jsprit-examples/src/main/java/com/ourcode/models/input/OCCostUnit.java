package com.ourcode.models.input;

import com.google.gson.annotations.SerializedName;
import javafx.util.Pair;

import javax.servlet.http.HttpServlet;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Tung
 * Date: 10/03/17
 * To change this template use File | Settings | File Templates.
 */

public class OCCostUnit extends HttpServlet
{
    @SerializedName("srcLocationCode")
    private String srcLocationCode;

    @SerializedName("desLocationCode")
    private String desLocationCode;

    @SerializedName("distance")
    private double distance; // (double), (in kilometers), the distance between srcLocationCode and desLocationCode

    @SerializedName("travelTime")
    private double travelTime; // (double), (in hours), traveling time from location srcLocationCode to location desLocationCode

    // Getter and Setter method
    public String getSrcLocationCode() {return srcLocationCode;}
    public String getDesLocationCode() {return desLocationCode;}
    public double getDistance() {return distance;}
    public double getTravelTime() {return travelTime;}

    // Constructor
    public OCCostUnit(String srcLocationCode, String desLocationCode, double distance, double travelTime) {
        this.srcLocationCode = srcLocationCode;
        this.desLocationCode = desLocationCode;
        this.distance = distance;
        this.travelTime = travelTime;
    }

    static public Pair findPairInCostUnits(ArrayList<OCCostUnit> costUnits, String srcLocationCode, String desLocationCode)
    {
        for (OCCostUnit costUnit: costUnits)
            if (costUnit.getSrcLocationCode().equals(srcLocationCode)
                && costUnit.getDesLocationCode().equals(desLocationCode))
                return new Pair(costUnit.getDistance(), costUnit.getTravelTime());
        return null;
    }
}
