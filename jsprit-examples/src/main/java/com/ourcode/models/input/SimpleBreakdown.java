package com.ourcode.models.input;

import java.util.ArrayList;

/**
 * Created by LEOLEOl on 5/4/2017.
 */
public class SimpleBreakdown implements ShipmentBreakdown{
    private double minWeightOfVehicles;
    private double minVolumeOfVehicles;
    private OCShipment shipment;

    public SimpleBreakdown(double minWeight, double minVolume, OCShipment shipment) {
        this.minWeightOfVehicles = minWeight; // min của maxWeight của đội xe
        this.minVolumeOfVehicles = minVolume; // min của maxVoluem của đội xe
        this.shipment = new OCShipment(shipment);
    }

    @Override
    public ArrayList<OCShipment> breakDownShipmentsByWeight() {
        double minWeight = minWeightOfVehicles;
        int num = ((int) Math.ceil(shipment.getWeight() / minWeight));

        ArrayList<OCShipment> ret = new ArrayList<>();
        int i = 0;
        OCShipment shipmentUnit;
        while (i++ < num - 1) {
            shipmentUnit = new OCShipment(shipment.getShipCode() +  "---" + i,
                shipment.getPickupServiceTime() / num, shipment.getDeliveryServiceTime() / num,
                minWeight, shipment.getVolume() / num,
                shipment.getPickupLocationCode(), shipment.getDeliveryLocationCode(),
                shipment.getPickupTimeWindows(), shipment.getDeliveryTimeWindows(), shipment.getPriority());
            ret.add(shipmentUnit);
        }
        shipmentUnit = new OCShipment(shipment.getShipCode() +  "---" + i,
            shipment.getPickupServiceTime() / num, shipment.getDeliveryServiceTime() / num,
            shipment.getWeight() - minWeight * (num-1), shipment.getVolume() / num,
            shipment.getPickupLocationCode(), shipment.getDeliveryLocationCode(),
            shipment.getPickupTimeWindows(), shipment.getDeliveryTimeWindows(), shipment.getPriority());
        ret.add(shipmentUnit);
        return ret;
    }

    @Override
    public ArrayList<OCShipment> breakDownShipmentsByVolume() {
        double minVolume = minVolumeOfVehicles;
        int num = ((int) Math.ceil(shipment.getVolume() / minVolume));

        ArrayList<OCShipment> ret = new ArrayList<>();
        int i = 0;
        OCShipment shipmentUnit;
        while (i++ < num - 1) {
            shipmentUnit = new OCShipment(shipment.getShipCode() + "---" + i,
                shipment.getPickupServiceTime() / num, shipment.getDeliveryServiceTime() / num,
                shipment.getWeight() / num, minVolume,
                shipment.getPickupLocationCode(), shipment.getDeliveryLocationCode(),
                shipment.getPickupTimeWindows(), shipment.getDeliveryTimeWindows(), shipment.getPriority());
            ret.add(shipmentUnit);
        }
        shipmentUnit = new OCShipment(shipment.getShipCode() + "---" + i,
            shipment.getPickupServiceTime() / num, shipment.getDeliveryServiceTime() / num,
            shipment.getWeight() / num, shipment.getVolume() - minVolume * (num-1),
            shipment.getPickupLocationCode(), shipment.getDeliveryLocationCode(),
            shipment.getPickupTimeWindows(), shipment.getDeliveryTimeWindows(), shipment.getPriority());
        ret.add(shipmentUnit);
        return ret;
    }
}
