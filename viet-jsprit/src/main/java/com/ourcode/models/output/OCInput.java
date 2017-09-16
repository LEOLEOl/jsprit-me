package com.ourcode.models.output;

import com.google.gson.annotations.SerializedName;
import com.ourcode.exceptions.OurException;
import com.ourcode.models.input.*;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by LEOLEOl on 4/18/2017.
 */
public class OCInput {
    final public double MAX_LENGTH = 1000000.0;
    final public double MAX_TIME = 1000000.0;

    // Input
    @SerializedName("timeAtRequest")
    private long timeAtRequest;

    @SerializedName("locations")
    private ArrayList<OCLocation> locations;

    @SerializedName("shipments")
    private ArrayList<OCShipment> shipments;

    private ArrayList<OCServicePickup> pickups;

    @SerializedName("costUnits")
    private ArrayList<OCCostUnit> costUnits;

    @SerializedName("vehicles")
    private ArrayList<OCVehicle> vehicles;

    @SerializedName("wayPoints")
    private ArrayList<WayPoint> wayPoints;



    public OCVRP checkAndGetOCVRP() throws OurException {
        java.util.Set<String> locationCodeSet = new java.util.HashSet<>();
        OCVRP vrp = new OCVRP();

        // Get locationCodeSet
        getLocationCodeSet(locationCodeSet);

        ///// Check and get timeAtRequest
        // 1. Check timeAtRequest is >= 0
        // 2. Set timeAtRequest
        doGetTimeAtRequest(vrp);

        ///// Check locations
        // 1. Check duplicated location ID
        // 2. Check time window in locations if they are overlapped or invalid
        doGetLocations();

        ///// Check and get vehicles
        // 1. Check duplicated vehicle ID
        // 2. Check maxWeight, minWeight, maxVolume, minVolume is valid
        // 3. Set vehicles
        doGetVehicles(vrp);

        ///// Check and get wayPoint
        // 1. Insert point to locationCodeSet
        // 2. Insert costUnits
        //      + In AB: the distance and time
        //      + Out AB: >>>
        // 3. Set pseudo-pickups
        doGetPseudoPickups(locationCodeSet, vrp);

        ///// Check and get costUnits
        // 1. Check all the pair in locationCodeSet exist
        // 2. If one pair doesn't exist, throw Error or create value for costUnit
        // 3. Run Floyd
        // 4. Set Costs
        doGetCostUnits(locationCodeSet, vrp);

        ///// Check and get shipments
        // 1. Check duplicated shipments' code
        // 2. Check pickupServiceTime, deliveryServiceTime is valid
        // 3. Check weight and volume is valid
        // 4. Check timeWindows(pickup & delivery) are neither overlapped nor invalid
        // 5. Get timeWindows(pickup & delivery) from location if missing
        // 6. Break down shipments
        // 7. Set shipments
        doGetShipments(vrp);

        return vrp;
    }

    private void getLocationCodeSet(Set<String> locationCodeSet)    {
        for (OCVehicle vehicle: vehicles) {
            locationCodeSet.add(vehicle.getStartLocationCode());
            locationCodeSet.add(vehicle.getEndLocationCode());
        }
        for (OCShipment shipment: shipments) {
            locationCodeSet.add(shipment.getPickupLocationCode());
            locationCodeSet.add(shipment.getDeliveryLocationCode());
        }
    }

    private void doGetPseudoPickups(Set<String> locationCodeSet, OCVRP vrp) throws OurException {
        pickups = new ArrayList<>();

        for (WayPoint wayPoint: wayPoints) {
            Set<String> locCodeSetWay = new HashSet<>();
            String from = wayPoint.getFrom(), to = wayPoint.getTo();
            if (!locationCodeSet.contains(from)) throw new OurException(61, "(wayPoints): from: " + from + " does not exist");
            if (!locationCodeSet.contains(to)) throw new OurException(62, "(wayPoints): to: " + to + " does not exist");

            Pair distance_time = getDistanceTimeFromCostUnits(from, to);
            if (distance_time == null) distance_time = getDistanceTimeFromCostUnits(to, from);
            if (distance_time == null) throw new OurException(63, "(wayPoints): " + from + " to " + to + " doesn't exist");

            double totalDistance = ((double) distance_time.getKey()),
                totalTime = ((double) distance_time.getValue());

            ArrayList<MidPoint> midPoints = wayPoint._getMidPoints();
            int sizeMidPoints = midPoints.size();
            double timeUnit = totalTime / (sizeMidPoints + 1);

            for (int i = 0; i < sizeMidPoints; ++i) {
                locCodeSetWay.add(midPoints.get(i).point);
                costUnits.add(new OCCostUnit(from, midPoints.get(i).point, midPoints.get(i).distanceFromRoot, timeUnit * (i+1)));
                costUnits.add(new OCCostUnit(midPoints.get(i).point, to, totalDistance-midPoints.get(i).distanceFromRoot, totalTime-timeUnit*(i+1)));

                pickups.add(new OCServicePickup("pickup" + midPoints.get(i).point, 0, 0, 0, midPoints.get(i).point, new ArrayList<>(), 3 ));

                for (int j = i+1; j < sizeMidPoints; ++j)
                    costUnits.add(new OCCostUnit(midPoints.get(i).point, midPoints.get(j).point, midPoints.get(j).distanceFromRoot - midPoints.get(i).distanceFromRoot, (j-i)* timeUnit));
            }
            locationCodeSet.addAll(locCodeSetWay);
        }
        vrp._setPickups(pickups);
    }

    private void doGetTimeAtRequest(OCVRP vrp) throws OurException {
        if (timeAtRequest < 0) throw new OurException(1, "(timeAtRequest): Time at request < 0");
        vrp.setTimeAtRequest(this.timeAtRequest);
    }

    private void doGetLocations() throws OurException {
        Set<String> tempSet = new HashSet<>();
        for (OCLocation location: locations) {
            String locationCode = location.getLocationCode();
            // Check duplicated location ID
            if (!tempSet.add(locationCode)) throw new OurException(21, "(locations): Duplicated location code: " + locationCode);
            // Check time window in locations
            int erCode = OCTimeWindow.isValidTimeWindows(location.getTimeWindows(), timeAtRequest);
            if (erCode == 1) throw new OurException(231, "(locations): timeWindows in location " + locationCode + " are invalid");
            else if (erCode == 2) throw new OurException(232, "(locations): timeWindows in location " + locationCode + " are overlapped");
        }
    }

    private void doGetVehicles(OCVRP vrp) throws OurException {
        Set<String> tempSet = new HashSet<>();
        for (OCVehicle vehicle: vehicles) {
            String vehicleID = vehicle.getId();
            // Check duplicated vehicles
            if (!tempSet.add(vehicleID)) throw new OurException(51, "(vehicles): Duplicated vehicle ID: " + vehicleID);
            //Check weight and volume is positive
            if (vehicle.getMaxWeight() <= 0) throw new OurException(541, "(vehicles): " + vehicleID + " has maxWeight <= 0");
            if (vehicle.getMinWeight() < 0) throw new OurException(542, "(vehicles): " + vehicleID + " has minWeight < 0");
            if (vehicle.getMaxVolume() <= 0) throw new OurException(551, "(vehicles): " + vehicleID + " has maxVolume <= 0");
            if (vehicle.getMinVolume() < 0) throw new OurException(552, "(vehicles): " + vehicleID + " has minVolume < 0");
            if (vehicle.getFixedCost() < 0) throw new OurException(561, "(vehicles): " + vehicleID + " has fixedCost < 0");
            if (vehicle.getPerWaitingTimeCost() < 0) throw new OurException(562, "(vehicles): " + vehicleID + " has perWaitingTime < 0");
            if (vehicle.getPerServiceTimeCost() < 0) throw new OurException(563, "(vehicles): " + vehicleID + " has perServiceTime < 0");

            if (vehicle._getBreakTime().getServiceTime() < 0) throw new OurException(571, "(vehicles): " + vehicleID + " has Break_serviceTime < 0");

            int erCode = OCTimeWindow.isValidTimeWindows(vehicle._getBreakTime().timeWindows, timeAtRequest);
            if (erCode == 1) throw new OurException(581, "(vehicles): timeWindows in breakTime of vehicle " + vehicleID + " are invalid");
            else if (erCode == 2) throw new OurException(582, "(vehicles): timeWindows in breakTime of vehicle " + vehicleID + " are overlapped");

            getRelativeTimeWindowsForBreakTimeVehicle(vehicle);
        }
        vrp._setVehicles(vehicles);
    }

    private void doGetShipments(OCVRP vrp) throws OurException {
        Set<String> tempSet = new HashSet<>();
        for (OCShipment shipment: shipments) {
            String shipCode = shipment.getShipCode();

            // Check duplicated ship code
            if (!tempSet.add(shipCode)) throw new OurException(31, "(shipments): Duplicated ship code: " + shipCode);
            // Check service time is non-negative
            if (shipment.getPickupServiceTime() < 0) throw new OurException(32, "(shipments): " + shipCode + " has pickupServiceTime < 0");
            if (shipment.getDeliveryServiceTime() < 0) throw new OurException(33, "(shipments): " + shipCode + " has deliveryServiceTime < 0");
            // Check weight and volume is positive
            if (shipment.getWeight() <= 0) throw new OurException(34, "(shipments): " + shipCode + " has weight <= 0");
            if (shipment.getVolume() <= 0) throw new OurException(35, "(shipments): " + shipCode + " has volume <= 0");

            ArrayList<OCTimeWindow> pickUpTimeWindows = shipment.getPickupTimeWindows();
            ArrayList<OCTimeWindow> deliveryTimeWindows = shipment.getDeliveryTimeWindows();

            // Check pickup time window in shipments
            int erCode = OCTimeWindow.isValidTimeWindows(pickUpTimeWindows, timeAtRequest);
            if (erCode == 1) throw new OurException(381, "(shipments): pickupTimeWindows in shipment " + shipCode + " are invalid");
            else if (erCode == 2) throw new OurException(382, "(shipments): pickupTimeWindows in shipment " + shipCode + " are overlapped");
            // Check delivery time window in shipments
            erCode = OCTimeWindow.isValidTimeWindows(deliveryTimeWindows, timeAtRequest);
            if (erCode == 1) throw new OurException(391, "(shipments): deliveryTimeWindows in shipment " + shipCode + " are invalid");
            else if (erCode == 2) throw new OurException(392, "(shipments): deliveryTimeWindows in shipment " + shipCode + " are overlapped");

            // Normalize timeWindows
            getRelativeTimeWindowsForShipment(shipment, pickUpTimeWindows, deliveryTimeWindows);
        }

        ArrayList<OCVehicle> vehicles = vrp._getVehicles();
        breakDownShipments(vehicles, this.shipments);
        vrp._setShipments(this.shipments);
    }

    private void doGetCostUnits(Set<String> locationCodeSet, OCVRP vrp) throws OurException {
        String[] locationCodeArray = locationCodeSet.toArray(new String[locationCodeSet.size()]);
        int size = locationCodeArray.length;

        for (int i = 0; i < size-1; ++i) {
            for (int j = i+1; j < size; ++j) {
                Pair pair1 = OCCostUnit.findPairInCostUnits(costUnits, locationCodeArray[i], locationCodeArray[j]),
                    pair2 = OCCostUnit.findPairInCostUnits(costUnits, locationCodeArray[j], locationCodeArray[i]);
                double distance, travelTime;
                if (pair1 == null) {
                    if (pair2 == null) {
                        // costUnits.add(new OCCostUnit(locationCodeArray[i], locationCodeArray[j], MAX_LENGTH, MAX_TIME));
                        // costUnits.add(new OCCostUnit(locationCodeArray[j], locationCodeArray[i], MAX_LENGTH, MAX_TIME));
                        throw new OurException(40, "(costUnits): Cannot find costUnit between " + locationCodeArray[i] + " and " + locationCodeArray[j]);
                    }
                    else {
                        distance = ((double) pair2.getKey()); travelTime = ((double) pair2.getValue());
                        if (distance < 0) throw new OurException(43, "(costUnits): " + locationCodeArray[j] + " - " + locationCodeArray[i] + " has distance < 0");
                        if (travelTime < 0) throw new OurException(44, "(costUnits): " + locationCodeArray[j] + " - " + locationCodeArray[i] + " has travelTime < 0");
                        costUnits.add(new OCCostUnit(locationCodeArray[i], locationCodeArray[j], distance, travelTime));
                    }
                }
                else {
                    distance = ((double) pair1.getKey()); travelTime = ((double) pair1.getValue());
                    if (distance < 0) throw new OurException(43, "(costUnits): " + locationCodeArray[i] + " - " + locationCodeArray[j] + " has distance < 0");
                    if (travelTime < 0) throw new OurException(44, "(costUnits): " + locationCodeArray[i] + " - " + locationCodeArray[j] + " has travelTime < 0");
                    if (pair2 == null) costUnits.add(new OCCostUnit(locationCodeArray[j], locationCodeArray[i], distance, travelTime));
                }
            }
        }
        vrp._setCostUnitsAndRunFloyd(this.costUnits, locationCodeSet);
    }


    ArrayList<OCTimeWindow> getTimeWindowsFromLocationCode(String locationCode)    {
        for (OCLocation location : locations)
            if (location.getLocationCode().equals(locationCode))
                return location.getTimeWindows();
        return null;
    }

    private Pair getDistanceTimeFromCostUnits(String from, String to) {
        for (OCCostUnit costUnit : costUnits)
            if (costUnit.getSrcLocationCode().equals(from) & costUnit.getDesLocationCode().equals(to))
                return new Pair(costUnit.getDistance(), costUnit.getTravelTime());
        return null;
    }

    private void breakDownShipments(ArrayList<OCVehicle> vehicles, ArrayList<OCShipment> shipments) {
        // breakDownShipment by Volume
        double maxVolume = vehicles.get(0).getMaxVolume(), minVolume = maxVolume; // maxVolume là Volume lon nhat trong doi xe, minWeight là Volume nho nhat trong doi xe
        double maxWeight = vehicles.get(0).getMaxWeight(), minWeight = maxWeight; // ___
        for (int i = 1; i < vehicles.size(); ++i) {
            if (maxVolume < vehicles.get(i).getMaxVolume()) maxVolume = vehicles.get(i).getMaxVolume();
            if (maxWeight < vehicles.get(i).getMaxWeight()) maxWeight = vehicles.get(i).getMaxWeight();
            if (minVolume > vehicles.get(i).getMaxVolume()) minVolume = vehicles.get(i).getMaxVolume();
            if (minWeight > vehicles.get(i).getMaxWeight()) minWeight = vehicles.get(i).getMaxWeight();
        }

        int shipmentSize = shipments.size();
        for (int i = 0; i < shipmentSize; ++i) {
            if (shipments.get(i).getVolume() > maxVolume || shipments.get(i).getWeight() > maxWeight) {
                ShipmentBreakdown shipmentBreakdown = new SimpleBreakdown(minWeight, minVolume, shipments.get(i));
                ArrayList<OCShipment> subShipments;

                if (shipments.get(i).getWeight()/shipments.get(i).getVolume() >= maxWeight/maxVolume)
                    subShipments = shipmentBreakdown.breakDownShipmentsByWeight();// Breakdown theo weight
                else subShipments = shipmentBreakdown.breakDownShipmentsByVolume();     // Breakdown theo volume

                shipments.addAll(subShipments);
                shipments.remove(i);
                --i;
                --shipmentSize;
            }
        }
    }

    private void getRelativeTimeWindowsForBreakTimeVehicle(OCVehicle vehicle) throws OurException    {
        OCTimeWindow.changeToRelativeTimeInHour(vehicle._getBreakTime().timeWindows, timeAtRequest);
    }

    private void getRelativeTimeWindowsForShipment(OCShipment shipment, ArrayList<OCTimeWindow> pickUpTimeWindows, ArrayList<OCTimeWindow> deliveryTimeWindows) throws OurException{
        ArrayList<OCTimeWindow> timeWindows;

        // get pickupTimeWindows
        try {
            timeWindows = pickUpTimeWindows.size() != 0 ?
                OCTimeWindow.changeToRelativeTimeInHour(pickUpTimeWindows, timeAtRequest):
                OCTimeWindow.changeToRelativeTimeInHour(getTimeWindowsFromLocationCode(shipment.getPickupLocationCode()), timeAtRequest);
            shipment.setPickupTimeWindows(timeWindows);
        } catch (OurException e) {
            e.errorCode = 301;
            e.details  = "(shipments): Cannot find pickupTimeWindows for shipment " + shipment.getShipCode();
            throw e;
        }

        // get deliveryTimeWindows
        try {
            timeWindows = deliveryTimeWindows.size() != 0 ?
                OCTimeWindow.changeToRelativeTimeInHour(deliveryTimeWindows, timeAtRequest):
                OCTimeWindow.changeToRelativeTimeInHour(getTimeWindowsFromLocationCode(shipment.getDeliveryLocationCode()), timeAtRequest);
            shipment.setDeliveryTimeWindows(timeWindows);
        } catch (OurException e) {
            e.errorCode = 302;
            e.details  = "(shipments): Cannot find deliveryTimeWindows for shipment " + shipment.getShipCode();
            throw e;
        }
    }

}
