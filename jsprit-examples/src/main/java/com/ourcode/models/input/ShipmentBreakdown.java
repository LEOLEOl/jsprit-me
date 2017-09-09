package com.ourcode.models.input;

import com.ourcode.models.input.OCShipment;

import java.util.ArrayList;

/**
 * Created by LEOLEOl on 5/4/2017.
 */
public interface ShipmentBreakdown {
    ArrayList<OCShipment> breakDownShipmentsByWeight();
    ArrayList<OCShipment> breakDownShipmentsByVolume();
}
