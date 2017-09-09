package com.graphhopper.jsprit.examples;

/**
 * Created by LEOLEOl on 4/14/2017.
 */
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.cost.VehicleRoutingTransportCosts;
import com.graphhopper.jsprit.core.util.VehicleRoutingTransportCostsMatrix;
import com.ourcode.exceptions.OurException;
import com.ourcode.models.input.OCCostUnit;
import com.ourcode.models.output.OCInput;
import com.ourcode.models.output.OCVRP;
import junit.framework.TestCase;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.*;

import java.io.FileReader;
import java.util.ArrayList;

/**
 * Created by LEOLEOl on 4/14/2017.
 */
public class OCVRPTest extends TestCase  {
    private ArrayList<OCCostUnit> costUnits;
    OCCostUnit ocCostUnitA;
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        System.out.println("Setting it up!");
        ocCostUnitA = new OCCostUnit("0", "1", 0.0, 1.0);


    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        System.out.println("Tearing it down!");
        ocCostUnitA = null;
        assertNull(ocCostUnitA);
    }

    @Test
    public void testBuildCostMatrix (){
        OCCostUnit ocCostUnit = new OCCostUnit("0", "1", 0.0, 1.0);
        //OCCostUnit ocCostUnit = new OCCostUnit("0", "1", 0.0, 1.0);

        ArrayList<OCCostUnit> ocCostUnits = new ArrayList<OCCostUnit>();
        ocCostUnits.add(ocCostUnit);

        VehicleRoutingTransportCostsMatrix.Builder builder = VehicleRoutingTransportCostsMatrix.Builder.newInstance(true);


        OCVRP problem = new OCVRP();
        VehicleRoutingTransportCosts costMatrix = null;
        try {
            costMatrix = problem.buildCostMatrix(ocCostUnits, builder);
        } catch (OurException e) {
            e.printStackTrace();
        }

        double resultTime = costMatrix.getTransportTime(Location.newInstance("0"), Location.newInstance("1"),0, null, null);
        assert (resultTime == 1.0);

        double resultDistance = costMatrix.getTransportCost(Location.newInstance("0"), Location.newInstance("1"),0, null, null);
        assert  (resultDistance == 0.0);

        double resultTime1 = costMatrix.getTransportTime(Location.newInstance("1"), Location.newInstance("0"),0, null, null);
        assert (resultTime == 1.0);
    }

    @Test
    public void testBuildCostMatrix2 (){
        OCCostUnit ocCostUnit = new OCCostUnit("0", "1", 5.0, 3.0);
        //OCCostUnit ocCostUnit = new OCCostUnit("0", "1", 0.0, 1.0);

        ArrayList<OCCostUnit> ocCostUnits = new ArrayList<OCCostUnit>();
        ocCostUnits.add(ocCostUnit);

        VehicleRoutingTransportCostsMatrix.Builder builder = VehicleRoutingTransportCostsMatrix.Builder.newInstance(true);


        OCVRP problem = new OCVRP();

        VehicleRoutingTransportCosts costMatrix = null;
        try {
            costMatrix = problem.buildCostMatrix(ocCostUnits, builder);
        } catch (OurException e) {
            e.printStackTrace();
        }

        double resultTime = costMatrix.getTransportTime(Location.newInstance("0"), Location.newInstance("1"),0, null, null);
        assert (resultTime == 3.0);

        double resultDistance = costMatrix.getTransportCost(Location.newInstance("0"), Location.newInstance("1"),0, null, null);
        assert  (resultDistance == 5.0);

        double resultTime1 = costMatrix.getTransportTime(Location.newInstance("1"), Location.newInstance("0"),0, null, null);
        assert (resultTime == 3.0);
    }

    @Test
    public void test1()
    {
        assert 1==1;
    }

    @Test
    public void testJSonStringFromFile()
    {

        try {
            Gson gson = new Gson();
            JsonReader reader = new JsonReader(new FileReader("src/main/test/java/com/graphhopper/jsprit/examples/sample2.json"));
            OCVRP problem = gson.fromJson(reader, OCVRP.class);

            //assert (problem._getCostUnits().size() == 6);
            boolean foundSrcLocation0=false, foundDestination3=false;
            for (OCCostUnit costUnit: problem._getCostUnits()) {
                if (costUnit.getSrcLocationCode().equals("0")) {
                    foundSrcLocation0 = true;
                    if(costUnit.getDesLocationCode().equals("3")){
                        foundDestination3 = true;

                        assert costUnit.getDistance() == 0.0;
                        assert costUnit.getTravelTime() == 100.0;
                    }
                }

            }
            assert foundSrcLocation0;
            assert foundDestination3;
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

//    @ZZZTest
//    public void testJsonString()
//    {
//        String sampleString = "{\r\n    \"locations\": [\r\n        {\r\n            \"locationCode\": \"1\",\r\n            \"locationName\": \"157 PHAN DANG LUU, P.01, Q.PHU NHUAN, TP.HO CHI MINH\",\r\n            \"timeWindows\": [\r\n                {\r\n                    \"start\": 1491760800,\r\n                    \"end\": 1491840000\r\n                },\r\n                {\r\n                    \"start\": 1491847200,\r\n                    \"end\": 1491926400\r\n                }\r\n            ]\r\n        }\r\n    ],\r\n    \"shipments\":\t[\r\n        {\r\n            \"shipCode\":\"s2\",\r\n            \"pickupServiceTime\":\"0.0\",\r\n            \"deliveryServiceTime\":\"0.0\",\r\n            \"weight\":\"1.0\",\r\n            \"volume\":\"1.0\",\r\n            \"pickupLocationCode\":\"2\",\r\n            \"deliveryLocationCode\":\"3\",\r\n            \"pickupTimeWindows\": [],\r\n            \"deliveryTimeWindows\": []\r\n        },\r\n        {\r\n            \"shipCode\":\"s1\",\r\n            \"pickupServiceTime\":\"0.0\",\r\n            \"deliveryServiceTime\":\"0.0\",\r\n            \"weight\":\"1.0\",\r\n            \"volume\":\"1.0\",\r\n            \"pickupLocationCode\":\"1\",\r\n            \"deliveryLocationCode\":\"3\",\r\n            \"pickupTimeWindows\": [],\r\n            \"deliveryTimeWindows\": []\r\n        },\r\n        {\r\n            \"shipCode\":\"s3\",\r\n            \"pickupServiceTime\":\"0.0\",\r\n            \"deliveryServiceTime\":\"0.0\",\r\n            \"weight\":\"1.0\",\r\n            \"volume\":\"1.0\",\r\n            \"pickupLocationCode\":\"1\",\r\n            \"deliveryLocationCode\":\"4\",\r\n            \"pickupTimeWindows\": [],\r\n            \"deliveryTimeWindows\": []\r\n        }\r\n    ],\r\n    \"costUnits\": [\r\n        {\r\n            \"srcLocationCode\": \"0\",\r\n            \"desLocationCode\": \"1\",\r\n            \"distance\": \"0.0\",\r\n            \"travelTime\": \"1.0\"\r\n        },\r\n        {\r\n            \"srcLocationCode\": \"0\",\r\n            \"desLocationCode\": \"2\",\r\n            \"distance\": \"0.0\",\r\n            \"travelTime\": \"3.0\"\r\n        },\r\n        {\r\n            \"srcLocationCode\": \"0\",\r\n            \"desLocationCode\": \"3\",\r\n            \"distance\": \"0.0\",\r\n            \"travelTime\": \"100.0\"\r\n        },\r\n        {\r\n            \"srcLocationCode\": \"0\",\r\n            \"desLocationCode\": \"4\",\r\n            \"distance\": \"0.0\",\r\n            \"travelTime\": \"100.0\"\r\n        },\r\n        {\r\n            \"srcLocationCode\": \"1\",\r\n            \"desLocationCode\": \"2\",\r\n            \"distance\": \"0.0\",\r\n            \"travelTime\": \"1.0\"\r\n        },\r\n        {\r\n            \"srcLocationCode\": \"1\",\r\n            \"desLocationCode\": \"3\",\r\n            \"distance\": \"0.0\",\r\n            \"travelTime\": \"10.0\"\r\n        },\r\n        {\r\n            \"srcLocationCode\": \"1\",\r\n            \"desLocationCode\": \"4\",\r\n            \"distance\": \"0.0\",\r\n            \"travelTime\": \"100.0\"\r\n        },\r\n        {\r\n            \"srcLocationCode\": \"2\",\r\n            \"desLocationCode\": \"3\",\r\n            \"distance\": \"0.0\",\r\n            \"travelTime\": \"10.0\"\r\n        },\r\n        {\r\n            \"srcLocationCode\": \"2\",\r\n            \"desLocationCode\": \"4\",\r\n            \"distance\": \"0.0\",\r\n            \"travelTime\": \"100.0\"\r\n        },\r\n        {\r\n            \"srcLocationCode\": \"3\",\r\n            \"desLocationCode\": \"4\",\r\n            \"distance\": \"0.0\",\r\n            \"travelTime\": \"20.0\"\r\n        }\r\n    ],\r\n    \"vehicles\": [\r\n        {\r\n            \"id\":\"1\",\r\n            \"locationCode\": \"0\",\r\n            \"endLocationCode\": \"3\",\r\n            \"maxWeight\": 5.0,\r\n            \"minWeight\": 0.0,\r\n            \"maxVolume\": 5.0,\r\n            \"minVolume\": 0.0\r\n        },\r\n        {\r\n            \"id\":\"2\",\r\n            \"locationCode\": \"0\",\r\n            \"endLocationCode\": \"3\",\r\n            \"maxWeight\": 5.0,\r\n            \"minWeight\": 0.0,\r\n            \"maxVolume\": 5.0,\r\n            \"minVolume\": 0.0\r\n        }\r\n    ]\r\n}\r\n";
//
//        OCVRP problem = OCVRP.buildVRPFromJson(sampleString);
//
//        assert (problem._getCostUnits().size() == 6);
//        boolean foundSrcLocation0=false, foundDestination3=false;
//        for (OCCostUnit costUnit: problem._getCostUnits()) {
//            if (costUnit.getSrcLocationCode().equals("0")) {
//                foundSrcLocation0 = true;
//                if(costUnit.getDesLocationCode().equals("3")){
//                    foundDestination3 = true;
//
//                    assert costUnit.getDistance() == 0.0;
//                    assert costUnit.getTravelTime() == 100.0;
//                }
//            }
//
//        }
//        assert foundSrcLocation0;
//        assert foundDestination3;
//    }

    public void testInputIsValid(String filePath) throws OurException
    {
        try {
            String jsonStringInput = new String(Files.readAllBytes(Paths.get(filePath)));
            OCInput ocInput;
            try {
                ocInput = OCVRP.getInput(jsonStringInput); // Get Input Json
            } catch (Exception e) {
                System.out.printf("Wrong Json Format");
                throw new OurException(-1, "Wrong JSON Format");
            }

            ocInput.checkAndGetOCVRP();

        } catch (IOException e) {
            System.out.printf("Cannot read file");
        }

    }
}


