package com.ourcode.models.input;

import com.google.gson.annotations.SerializedName;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleType;
import com.ourcode.exceptions.OurException;

/**
 * Created with IntelliJ IDEA.
 * User: Tung
 * Date: 10/03/17
 * Time: 4:45 PM
 * To change this template use File | Settings | File Templates.
 */

public class OCVehicle
{
    @SerializedName("id")
    private String id; // the id of the vehicle, may refer to plate number

    @SerializedName("fixedCost")
    private double fixedCost;

    @SerializedName("perWaitingTimeCost")
    private double perWaitingTimeCost;

    @SerializedName("perServiceTimeCost")
    private double perServiceTimeCost;

    @SerializedName("breakTime")
    private OCBreak breakTime;

    @SerializedName("maxWeight")
    private double maxWeight; // (double), (in kilograms), max weight it can carry

    @SerializedName("minWeight")
    private double minWeight;

    @SerializedName("maxVolume")
    private double maxVolume; // (double), (in cubic meters), max volume it can carry

    @SerializedName("minVolume")
    private double minVolume;

    @SerializedName("startLocationCode")
    private String startLocationCode; //  (string), the location the vehicle is at

    @SerializedName("startLocationName")
    private String startLocationName;

    @SerializedName("endLocationCode")
    private String endLocationCode;

    @SerializedName("endLocationName")
    private String endLocationName;

    @SerializedName("length")
    private double length; // (double), (optional), (in meters), the length of vehicle's container

    @SerializedName("width")
    private double width; // (double), (optional), (in meters), the width of vehicle's container

    @SerializedName("height")
    private double height; // (double), (optional), (in meters), the height of vehicle's container

    @SerializedName("type")
    private String type;

    private VehicleType j_vehicleType;
    private VehicleImpl j_vehicle;



    // Getter and Setter method
    public String getId() {
        return id;
    }
    public double getFixedCost() {return fixedCost;}
    public double getPerWaitingTimeCost() {return perWaitingTimeCost;}
    public double getPerServiceTimeCost() {return perServiceTimeCost;}

    public OCBreak _getBreakTime()
    {
        return breakTime;
    }

    public double getMaxWeight() {
        return maxWeight;
    }
    public double getMinWeight() {return minWeight;}
    public double getMaxVolume() {
        return maxVolume;
    }
    public double getMinVolume() {return minVolume;}

    public String getStartLocationCode() {
        return startLocationCode;
    }
    public String getStartLocationName() {return startLocationName;}
    public String getEndLocationCode() {return endLocationCode;}
    public String getEndLocationName() {return endLocationName;}
    public String getType() {return type;}

    public OCVehicle(OCVehicle vehicle)
    {
        this.id = vehicle.id;
        this.fixedCost = vehicle.fixedCost;
        this.perWaitingTimeCost = vehicle.perWaitingTimeCost;
        this.perServiceTimeCost = vehicle.perServiceTimeCost;
        this.maxVolume = vehicle.maxVolume;
        this.minVolume = vehicle.minVolume;
        this.maxWeight = vehicle.maxWeight;
        this.minWeight = vehicle.minWeight;
        this.height = vehicle.height;
        this.length = vehicle.length;
        this.width = vehicle.width;
        this.type = vehicle.type;
        this.breakTime = new OCBreak(vehicle.breakTime);
        this.startLocationCode = vehicle.startLocationCode;
        this.startLocationName = vehicle.startLocationName;
        this.endLocationCode = vehicle.endLocationCode;
        this.endLocationName = vehicle.endLocationName;
    }


    public VehicleImpl _getJ_vehicle() {return j_vehicle;}

    public OCVehicle build() throws OurException
    {
        try {
            j_vehicleType = (new
                OCVehicleType(id, maxWeight, maxVolume,
                1.0, 1.0, perServiceTimeCost, perWaitingTimeCost, fixedCost))
                .build()._getJ_vehicleType();
            j_vehicle = VehicleImpl.Builder.newInstance(id)
                .setStartLocation((new OCLocation(startLocationCode, startLocationCode)).build()._getJ_location()).setEndLocation((new OCLocation(endLocationCode, endLocationCode).build()._getJ_location()))
                .setType(j_vehicleType)//.setReturnToDepot(false)
                .setBreak(breakTime.build()._getJ_break())
                .build();

            return this;
        }
        catch (Exception e) {
            throw new OurException(-1, "Wrong Vehicle");
        }
    }
}
