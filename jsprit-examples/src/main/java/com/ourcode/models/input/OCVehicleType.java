package com.ourcode.models.input;

import com.google.gson.annotations.SerializedName;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleType;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleTypeImpl;

/**
 * Created by LEOLEOl on 4/10/2017.
 */

public class OCVehicleType
{
    @SerializedName("idType")
    private String idType; // id of type of vehicle

    @SerializedName("maxWeight")
    private double maxWeight; // (double), (in kilograms), max weight it can carry

    @SerializedName("maxVolume")
    private double maxVolume; // (double), (in cubic meters), max volume it can carry

    @SerializedName("length")
    private double length; // (double), (optional), (in meters), the length of vehicle's container

    @SerializedName("width")
    private double width; // (double), (optional), (in meters), the width of vehicle's container

    @SerializedName("height")
    private double height; // (double), (optional), (in meters), the height of vehicle's container

    private VehicleType j_vehicleType;

    // Getter and Setter method
    public String getIdType() {
        return idType;
    }
    public double getMaxWeight() {
        return maxWeight;
    }
    public double getMaxVolume() {
        return maxVolume;
    }
    public double getLength() {return length;}
    public double getWidth() {return width;}
    public double getHeight() {return height;}

    public VehicleType _getJ_vehicleType() {return j_vehicleType;}

    // Cost
    @SerializedName("fixedCost")
    private double fixedCost;

    @SerializedName("perTransportTimeUnitCost")
    private double perTransportTimeUnitCost;

    @SerializedName("perDistanceUnitCost")
    private double perDistanceUnitCost;

    @SerializedName("perWaitingTimeUnitCost")
    private double perWaitingTimeUnitCost;

    @SerializedName("perServiceTimeUnitCost")
    private double perServiceTimeUnitCost;

    public OCVehicleType(String idType, double maxWeight, double maxVolume, double perTransportTimeUnitCost, double perDistanceUnitCost, double perServiceTimeUnitCost, double perWaitingTimeUnitCost, double fixedCost)
    {
        this.idType = idType;
        this.maxWeight = maxWeight;
        this.maxVolume = maxVolume;
        this.perTransportTimeUnitCost = perTransportTimeUnitCost;
        this.perDistanceUnitCost = perDistanceUnitCost;
        this.perServiceTimeUnitCost = perServiceTimeUnitCost;
        this.perWaitingTimeUnitCost = perWaitingTimeUnitCost;
        this.fixedCost = fixedCost;
    }

    public OCVehicleType build()
    {
        this.j_vehicleType = VehicleTypeImpl.Builder.newInstance(this.idType)
            .addCapacityDimension(0, (int)Math.ceil(this.maxWeight*100)).addCapacityDimension(1, (int)Math.ceil(this.maxVolume*100))
            .setCostPerTransportTime(this.perTransportTimeUnitCost).setCostPerDistance(this.perDistanceUnitCost)
            .setCostPerServiceTime(this.perServiceTimeUnitCost).setCostPerWaitingTime(this.perWaitingTimeUnitCost)
            .setFixedCost(this.fixedCost)
            .build();

        return this;
    }

}
