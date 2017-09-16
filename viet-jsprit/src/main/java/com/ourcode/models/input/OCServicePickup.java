package com.ourcode.models.input;

import com.google.gson.annotations.SerializedName;
import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.job.Pickup;
import com.graphhopper.jsprit.core.problem.job.Service;
import com.ourcode.exceptions.OurException;

import java.util.ArrayList;

/**
 * Created by LEOLEOl on 5/26/2017.
 */
public class OCServicePickup {
    @SerializedName("shipCode")
    private String shipCode; // Code of job, used to identify

    @SerializedName("pickupServiceTime")
    private double pickupServiceTime; // (double), (optional), (in hours), (the time count from optimizing time), Minimum to pickup or deliver the cargo

    @SerializedName("weight")
    private double weight; // (double), (in kilograms), the weight of cargo that needs to be picked up, delivered

    @SerializedName("volume")
    private double volume; // (double), (in cubic meters), the volume of cargo that needs to be picked up, delivered

    @SerializedName("pickupLocationCode")
    private String pickupLocationCode; // (string), this is the location that the job must be performed

    @SerializedName("pickupTimeWindows")
    private ArrayList<OCTimeWindow> pickupTimeWindows; // (in hours), (the time count from optimizing time), time window required for the job to be delivered/picked up.

    @SerializedName("priority")
    private int priority = 2; // priority of job, highest level is 1

    private Service j_pickup;

    // Getter and Setter Method
    public String getShipCode() {
        return shipCode;
    }

    public double getPickupServiceTime() {
        return pickupServiceTime;
    }

    public double getWeight() {
        return weight;
    }

    public double getVolume() {
        return volume;
    }

    public String getPickupLocationCode() {
        return pickupLocationCode;
    }

    public void setPickupTimeWindows(ArrayList<OCTimeWindow> timeWindows) {
        this.pickupTimeWindows.clear();
        for (OCTimeWindow timeWindow: timeWindows)
            this.pickupTimeWindows.add(new OCTimeWindow(timeWindow));
    }

    public int getPriority() {
        return priority;
    }

    public Service _getJ_pickup() {
        return j_pickup;
    }

    // Constructor
    public OCServicePickup(String shipCode, double pickupServiceTime, double weight, double volume,
                      String pickupLocationCode, ArrayList<OCTimeWindow> pickupTimeWindows,
                      int priority) {
        this.shipCode = shipCode;
        this.pickupServiceTime = pickupServiceTime;
        this.weight = weight;
        this.volume = volume;
        this.pickupLocationCode = pickupLocationCode;
        this.pickupTimeWindows = new ArrayList<>();
        for (OCTimeWindow timeWindow: pickupTimeWindows)
            this.pickupTimeWindows.add(new OCTimeWindow(timeWindow));
        this.priority = priority;
    }

    public OCServicePickup build() throws OurException {
        try {
            if (this.priority < 1 || this.priority > 3) this.priority = 2;

            Service.Builder builder = Pickup.Builder.newInstance(this.shipCode)
                .addSizeDimension(0, (int) Math.ceil(this.weight * 100)).addSizeDimension(1, (int) (Math.ceil(this.volume * 100)))
                .setServiceTime(this.pickupServiceTime)
                .setLocation(Location.newInstance(this.pickupLocationCode))
                .setPriority(this.priority);

            for (OCTimeWindow ocTimeWindow : this.pickupTimeWindows)
                builder.addTimeWindow(ocTimeWindow.build()._getJ_timeWindow());

            j_pickup = builder.build();
            return this;
        } catch (Exception e) {
            throw new OurException(-1, "Wrong ServicePickup");
        }
    }

    public void addPickupTimeWindow(OCTimeWindow ocTimeWindow) {
        pickupTimeWindows.add(new OCTimeWindow(ocTimeWindow));
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
