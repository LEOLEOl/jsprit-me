package com.ourcode.models.input;

import com.google.gson.annotations.SerializedName;
import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.job.Shipment;
import com.ourcode.exceptions.OurException;

import java.util.ArrayList;

/**
 * Created by LEOLEOl on 4/13/2017.
 */
public class OCShipment
{
    @SerializedName("shipCode")
    private String shipCode; // Code of job, used to identify

    @SerializedName("pickupServiceTime")
    private double pickupServiceTime; // (double), (optional), (in hours), (the time count from optimizing time), Minimum to pickup or deliver the cargo

    @SerializedName("deliveryServiceTime")
    private double deliveryServiceTime; // (double), (optional), (in hours), (the time count from optimizing time), Minimum to pickup or deliver the cargo

    @SerializedName("weight")
    private double weight; // (double), (in kilograms), the weight of cargo that needs to be picked up, delivered

    @SerializedName("volume")
    private double volume; // (double), (in cubic meters), the volume of cargo that needs to be picked up, delivered

    @SerializedName("pickupLocationCode")
    private String pickupLocationCode; // (string), this is the location that the job must be performed

    @SerializedName("deliveryLocationCode")
    private String deliveryLocationCode; // (string), this is the location that the job must be performed

    @SerializedName("pickupTimeWindows")
    private ArrayList<OCTimeWindow> pickupTimeWindows; // (in hours), (the time count from optimizing time), time window required for the job to be delivered/picked up.

    @SerializedName("deliveryTimeWindows")
    private ArrayList<OCTimeWindow> deliveryTimeWindows; // (in hours), (the time count from optimizing time), time window required for the job to be delivered/picked up.

    @SerializedName("priority")
    private int priority = 2; // priority of job, highest level is 1

    private Shipment j_service;

    // Getter and Setter Method
    public String getShipCode() {return shipCode;}
    public  double getPickupServiceTime() {return pickupServiceTime;}
    public  double getDeliveryServiceTime() {return deliveryServiceTime;}
    public double getWeight() {return weight;}
    public double getVolume() {return volume;}
    public String getPickupLocationCode() {return pickupLocationCode;}
    public String getDeliveryLocationCode() {return deliveryLocationCode;}

    public void setPickupTimeWindows(ArrayList<OCTimeWindow> timeWindows) {
        pickupTimeWindows.clear();
        for (OCTimeWindow timeWindow: timeWindows)
            pickupTimeWindows.add(new OCTimeWindow(timeWindow));
    }

    public void setDeliveryTimeWindows(ArrayList<OCTimeWindow> timeWindows) {
        deliveryTimeWindows.clear();
        for (OCTimeWindow timeWindow: timeWindows)
            deliveryTimeWindows.add(new OCTimeWindow(timeWindow));
    }

    public int getPriority() {return priority;}

    public Shipment _getJ_service() {return j_service;}
    // Constructor
    public OCShipment(String shipCode, double pickupServiceTime, double deliveryServiceTime, double weight, double volume,
                      String pickupLocationCode, String deliveryLocationCode, ArrayList<OCTimeWindow> pickupTimeWindows, ArrayList<OCTimeWindow> deliveryTimeWindows,
                      int priority)
    {
        this.shipCode = shipCode;
        this.pickupServiceTime = pickupServiceTime;
        this.deliveryServiceTime = deliveryServiceTime;
        this.weight = weight;
        this.volume = volume;
        this.pickupLocationCode = pickupLocationCode;
        this.deliveryLocationCode = deliveryLocationCode;

        this.priority = priority;

        this.pickupTimeWindows = new ArrayList<>();
        for (OCTimeWindow timeWindow: pickupTimeWindows)
            this.pickupTimeWindows.add(new OCTimeWindow(timeWindow));
        this.deliveryTimeWindows = new ArrayList<>();
        for (OCTimeWindow timeWindow: deliveryTimeWindows)
            this.deliveryTimeWindows.add(new OCTimeWindow(timeWindow));
    }

    public OCShipment(OCShipment shipment)
    {
        this.shipCode = shipment.shipCode;
        this.pickupServiceTime = shipment.pickupServiceTime;
        this.deliveryServiceTime = shipment.deliveryServiceTime;
        this.weight = shipment.weight;
        this.volume = shipment.volume;
        this.pickupLocationCode = shipment.pickupLocationCode;
        this.deliveryLocationCode = shipment.deliveryLocationCode;

        this.priority = shipment.priority;

        this.pickupTimeWindows = new ArrayList<>();
        for (OCTimeWindow timeWindow: shipment.pickupTimeWindows)
            this.pickupTimeWindows.add(new OCTimeWindow(timeWindow));
        this.deliveryTimeWindows = new ArrayList<>();
        for (OCTimeWindow timeWindow: shipment.deliveryTimeWindows)
            this.deliveryTimeWindows.add(new OCTimeWindow(timeWindow));

    }

    public OCShipment build() throws OurException
    {
        try {
            if (this.priority < 1 || this.priority > 3) this.priority = 2;

            Shipment.Builder builder = Shipment.Builder.newInstance(this.shipCode)
                    .addSizeDimension(0, (int) Math.ceil(this.weight*100)).addSizeDimension(1, (int)(Math.ceil(this.volume*100)))
                    .setPickupServiceTime(this.pickupServiceTime).setDeliveryServiceTime(this.deliveryServiceTime)
                    .setPickupLocation(Location.newInstance(this.pickupLocationCode)).setDeliveryLocation(Location.newInstance(this.deliveryLocationCode))
                    .setPriority(this.priority);

            for (OCTimeWindow ocTimeWindow: this.pickupTimeWindows)
                builder.addPickupTimeWindow(ocTimeWindow.build()._getJ_timeWindow());
            for (OCTimeWindow ocTimeWindow: this.deliveryTimeWindows)
                builder.addDeliveryTimeWindow(ocTimeWindow.build()._getJ_timeWindow());

            j_service = builder.build();
            return this;
        } catch (Exception e) {
            throw new OurException(-1, "Wrong Shipment");
        }
    }

    public void addPickupTimeWindow(OCTimeWindow ocTimeWindow)
    {
        pickupTimeWindows.add(new OCTimeWindow(ocTimeWindow));
    }

    public void addDeliveryTimeWindow(OCTimeWindow ocTimeWindow)
    {
        deliveryTimeWindows.add(new OCTimeWindow(ocTimeWindow));
    }

    public ArrayList<OCTimeWindow> getPickupTimeWindows()
    {
        ArrayList<OCTimeWindow> ret = new ArrayList<>();
        for (OCTimeWindow ocTimeWindow: pickupTimeWindows)
            ret.add(new OCTimeWindow(ocTimeWindow));
        return ret;
    }

    public ArrayList<OCTimeWindow> getDeliveryTimeWindows()
    {
        ArrayList<OCTimeWindow> ret = new ArrayList<>();
        for (OCTimeWindow ocTimeWindow: deliveryTimeWindows)
            ret.add(new OCTimeWindow(ocTimeWindow));
        return ret;
    }

    public void setPriority(int priority)
    {
        this.priority = priority;
    }
}
