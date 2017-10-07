package com.ourcode.models.process;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.graphhopper.jsprit.core.algorithm.VehicleRoutingAlgorithm;
import com.graphhopper.jsprit.core.algorithm.box.Jsprit;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.cost.VehicleRoutingTransportCosts;
import com.graphhopper.jsprit.core.problem.job.Break;
import com.graphhopper.jsprit.core.problem.job.Job;
import com.graphhopper.jsprit.core.problem.job.Shipment;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.problem.solution.route.VehicleRoute;
import com.graphhopper.jsprit.core.problem.solution.route.activity.*;
import com.graphhopper.jsprit.core.util.Solutions;
import com.graphhopper.jsprit.core.util.VehicleRoutingTransportCostsMatrix;
import com.ourcode.exceptions.OurException;
import com.ourcode.models.algorithms.Floyd;
import com.ourcode.models.configuration.Config;
import com.ourcode.models.input.*;
import com.ourcode.models.output.OCJobAtLocation;
import com.ourcode.models.output.OCJobRound;
import com.ourcode.models.output.OCOutput;
import com.ourcode.models.output.OCTransport;
import com.ourcode.models.utilities.Utilities;
import javafx.util.Pair;

import java.io.IOException;
import java.util.*;

/**
 * Created by LEOLEOl on 4/7/2017.
 */


public class OCVRP
{
    @SerializedName("timeAtRequest")
    private long timeAtRequest; // (in seconds)

    @SerializedName("shipments")
    private ArrayList<OCShipment> shipments;

    @SerializedName("costUnits")
    private ArrayList<OCCostUnit> costUnits;

    @SerializedName("vehicles")
    private ArrayList<OCVehicle> vehicles;

    @SerializedName("wayPoints")
    private ArrayList<WayPoint> wayPoints;

    // Another attributes

    private Hashtable<Pair, Pair> hashTableCosts;

    private Floyd floyd;

    @SerializedName("pickups")
    private ArrayList<OCServicePickup> pickups;


    // Getter and Setter method
    public ArrayList<OCShipment> _getShipments() {return shipments;}
    public ArrayList<OCServicePickup> _getPickups() {return pickups;}
    public ArrayList<OCCostUnit> _getCostUnits() {return costUnits;}
    public Hashtable _getHashTableCosts() {return hashTableCosts;}
    public ArrayList<OCVehicle> _getVehicles() {return vehicles;}
    public Floyd _getFloyd() {return floyd;}

    public void setTimeAtRequest(long timeAtRequest) {this.timeAtRequest = timeAtRequest;}
    public void _setShipments(ArrayList<OCShipment> shipments) {this.shipments = shipments;}
    public void _setPickups(ArrayList<OCServicePickup> pickups) {this.pickups = pickups;}
    public void _setVehicles(ArrayList<OCVehicle> vehicles) {this.vehicles = vehicles;}
    public void _setCostUnitsAndRunFloyd(ArrayList<OCCostUnit> costUnits, Set<String> locationCodeSet)
    {
        //this.costUnits = costUnits;
        // From now, we use hashTableCosts instead of costUnits
        hashTableCosts = new Hashtable<>();
        for (OCCostUnit costUnit: costUnits) // add costUnit to HashTableCosts
            hashTableCosts.put(new Pair(costUnit.getSrcLocationCode(), costUnit.getDesLocationCode()), new Pair(costUnit.getDistance(), costUnit.getTravelTime()));


        Hashtable<Pair, Double> floydHashTableCosts = new Hashtable<>(); // Create Floyd instance to optimize
        Set<Pair> keys = hashTableCosts.keySet(); // getKey set of hashTableCost to traverse in it
        for (Pair key: keys) // add TravelTime only
            floydHashTableCosts.put(key, ((Double) hashTableCosts.get(key).getValue())); // TravelTime is Value in the second pair

        floyd = new Floyd();
        floyd.loadGraph(floydHashTableCosts, locationCodeSet);
        floyd.runFloyd(); // Run Floyd Algorithm
        Hashtable<Pair, Pair> newHashTableCosts = new Hashtable<>();
        for (Pair key: keys)
            newHashTableCosts.put(key, new Pair(hashTableCosts.get(key).getKey(), floydHashTableCosts.get(key)));


        try {
            Config config = getConfig(Utilities.getAllTextUTF8("src/main/java/com/config/config.json"));
            if (config.floyd.equals("yes")) hashTableCosts = newHashTableCosts; // set new HashTableCosts
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addVehicle(OCVehicle ocVehicle)
    {
        vehicles.add(new OCVehicle(ocVehicle));
    }

    public OCVRP()
    {}

    public Object solveJson(String jsonInput) throws OurException {
        OCVRP ocvrp = buildVRPFromJson(jsonInput); // create our VRP

        // Push ocvrp to JSP and get the solution.
        VehicleRoutingProblemSolution vrpSol = pushToJSP(ocvrp);

        // Return OCOutput from the solution
        return getOCOutputFromSol(vrpSol, ocvrp);
    }

    private VehicleRoutingProblemSolution pushToJSP(OCVRP ocvrp) throws OurException {
        VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance()
            .setFleetSize(VehicleRoutingProblem.FleetSize.FINITE); // Create JSP vrpBuilder

        // add parameters from ocvrp to vrpBuilder and build
        addParametersToVRPBuilder(ocvrp, vrpBuilder); // add parameters (vehicles, shipment, costMatrix, ...) to vrpBuilder
        VehicleRoutingProblem vrp = vrpBuilder.build(); // Build JSP vrp

        // get solution from vrp
        return getSolutionFromVRPBuilder(vrp);
    }

    private VehicleRoutingProblemSolution getSolutionFromVRPBuilder(VehicleRoutingProblem vrp) {
        VehicleRoutingAlgorithm vrpAlg = Jsprit.createAlgorithm(vrp); // Create algorithm vrpAlg
        Collection<VehicleRoutingProblemSolution> solutions = vrpAlg.searchSolutions(); // Find solutions from vrpAlg
        return Solutions.bestOf(solutions); // get the best
    }

    private void addParametersToVRPBuilder(OCVRP ocvrp, VehicleRoutingProblem.Builder vrpBuilder) throws OurException {
        for (OCVehicle vehicle: ocvrp._getVehicles()) // Add vehicles to vrpBuilder
            vrpBuilder.addVehicle(vehicle.build()._getJ_vehicle());

        for (OCShipment shipment: ocvrp._getShipments()) // Add shipments to vrpBuilder
            vrpBuilder.addJob(shipment.build()._getJ_service());

        for (OCServicePickup pickup: ocvrp._getPickups()) // Add fake pickups, wayPoints to vrpBuilder
            vrpBuilder.addJob(pickup.build()._getJ_pickup());

        // Create costMatrixBuilder, not symmetric
        VehicleRoutingTransportCostsMatrix.Builder costMatrixBuilder = VehicleRoutingTransportCostsMatrix.Builder.newInstance(false);

        // Build costMatrix from costMatrixBuilder
        VehicleRoutingTransportCosts costMatrix = buildCostHashTable(ocvrp, costMatrixBuilder);

        vrpBuilder.setRoutingCost(costMatrix); // Set costMatrix to vrpBuilder
    }

    private Hashtable<Integer, Job> getJobs(VehicleRoutingProblemSolution solution)
    {
        // Job listJob[] = new Job[nShip - solution.getUnassignedJobs().size()];
        //Job listJob[] = new Job[nShip];
        Hashtable<Integer, Job> hashTableJob = new Hashtable<>();
        for (VehicleRoute vehicleRoute: solution.getRoutes()) {
            Collection<Job> jobs = vehicleRoute.getTourActivities().getJobs();
            for (Job job: jobs) hashTableJob.put(job.getIndex() - 1, job);
        }
        return hashTableJob;
    }

    private OCOutput getOCOutputFromSol(VehicleRoutingProblemSolution solution, OCVRP ocvrp) {
        OCOutput ocOutput = new OCOutput();

        // Get list jobs of vehicles.
        Hashtable<Integer, Job> hashTableJob = getJobs(solution);

        for (VehicleRoute vehicleRoute: solution.getRoutes()) {
            OCTransport ocTransport = new OCTransport(vehicleRoute.getVehicle().getId());


            addJobCode(vehicleRoute, ocTransport); // Add job code of each vehicle

            addJobRound(ocvrp, hashTableJob, vehicleRoute, ocTransport);  // Add job round of each vehicle

            ocOutput.addTransport(ocTransport);
        }
        return ocOutput;
    }

    private String getNormalizeJobCode(String jobCode)
    {
        int p = jobCode.indexOf("---");
        return p > -1 ? jobCode.substring(0, p) : jobCode;
    }

    private void addJobRound(OCVRP ocvrp, Hashtable<Integer, Job> hashTableJobs, VehicleRoute vehicleRoute, OCTransport ocTransport) {
        String prevLocation = vehicleRoute.getStart().getLocation().getId();
        String nextLocation = prevLocation; // to trick below function

        double departTime = 0 * 3600 + ocvrp.timeAtRequest,
            arriveTime = departTime; // to trick below function

        OCJobRound ocJobRound = new OCJobRound(prevLocation, nextLocation, (int)departTime, (int)arriveTime);

        OCJobAtLocation jobAtLocation;
        for (TourActivity tourActivity: vehicleRoute.getActivities()) {
            if (tourActivity instanceof PickupService) continue;

            nextLocation = tourActivity.getLocation().getId();
            arriveTime = tourActivity.getArrTime() * 3600 + ocvrp.timeAtRequest;
            //departTime = tourActivity.getTheoreticalEarliestOperationStartTime() * 3600 + ocvrp.timeAtRequest;

            Integer indexJob = (Integer) ((tourActivity.getIndex() - 1) / 2 + 1);
            Job job = hashTableJobs.get(indexJob-1); // If job == null, it means that job is a break activity

            if (!nextLocation.equals(prevLocation)) {
                if (ocJobRound.getJobsAtLocation().size() != 0)
                    addJobRoundsToTransport(ocvrp, ocTransport, ocJobRound);
                ocJobRound = new OCJobRound(prevLocation, nextLocation, (int)departTime, (int)arriveTime);
            }

            jobAtLocation = tourActivity instanceof BreakActivity ?
                new OCJobAtLocation("BREAK", "BREAK", -1, -1) :
                new OCJobAtLocation(getNormalizeJobCode(job.getId()), tourActivity.getName(), job.getSize().get(0) / 100, (double)job.getSize().get(1) / 100);

            ocJobRound.addJobAtLocation(jobAtLocation);
            prevLocation = nextLocation;
            departTime = arriveTime + tourActivity.getOperationTime();
        }
        // Add final ocJobRound
        addJobRoundsToTransport(ocvrp, ocTransport, ocJobRound);
    }

    private void addJobRoundsToTransport(OCVRP ocvrp, OCTransport ocTransport, OCJobRound ocJobRound) {
        ArrayList<OCJobRound> ocJobRounds = OCJobRound.getRealJobRounds(ocJobRound, ocvrp._getFloyd().getHashTableMatrix(), ocvrp._getFloyd().getHashTableTrace());
        for (OCJobRound jobRound: ocJobRounds) ocTransport.addJobRound(jobRound);
    }

    private void addJobCode(VehicleRoute vehicleRoute, OCTransport ocTransport) {
        Collection<Job> jobs = vehicleRoute.getTourActivities().getJobs();
        for (Job job: jobs)
            if (job instanceof Shipment || job instanceof Break)
                ocTransport.addJobCode(getNormalizeJobCode(job.getId()));
    }

    public OCVRP buildVRPFromJson(String jsonStringInput) throws OurException {
        OCInput input;
        try {
            input = parseJsonToOCInput(jsonStringInput);
        }
        catch (Exception e)
        {
            throw new OurException(0, "Wrong JSON Format");
        }
        return input.checkAndGetOCVRP();
    }

    public OCInput parseJsonToOCInput(String strInput) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gsonInput = gsonBuilder.create();
        return gsonInput.fromJson(strInput, OCInput.class);
    }

    public Config getConfig(String jsonStringInput) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gsonInput = gsonBuilder.create();
        return gsonInput.fromJson(jsonStringInput, Config.class);
    }

    public VehicleRoutingTransportCosts buildCostMatrix(ArrayList<OCCostUnit> ocCostUnits, VehicleRoutingTransportCostsMatrix.Builder costMatrixBuilder) throws OurException {
        try {
            for (OCCostUnit ocCostUnit: ocCostUnits) {
                costMatrixBuilder.addTransportDistance(ocCostUnit.getSrcLocationCode(), ocCostUnit.getDesLocationCode(), ocCostUnit.getDistance());
                costMatrixBuilder.addTransportTime(ocCostUnit.getSrcLocationCode(), ocCostUnit.getDesLocationCode(), ocCostUnit.getTravelTime());
            }
            VehicleRoutingTransportCosts costMatrix = costMatrixBuilder.build();
            return costMatrix;
        } catch (Exception e) {
            throw new OurException(-1, "Wrong CostMatrix");
        }
    }

    public VehicleRoutingTransportCosts buildCostHashTable(OCVRP ocvrp, VehicleRoutingTransportCostsMatrix.Builder costMatrixBuilder) throws OurException {
        Hashtable hashTableCosts = ocvrp._getHashTableCosts();
        try {
            Set<Pair> keys = hashTableCosts.keySet();
            for (Pair key: keys) {
                String srcLocationCode = ((String) key.getKey()), desLocationCode = ((String) key.getValue());
                Pair value = ((Pair) hashTableCosts.get(key));

                costMatrixBuilder.addTransportDistance(srcLocationCode, desLocationCode, ((double) value.getKey()));
                costMatrixBuilder.addTransportTime(srcLocationCode, desLocationCode, ((double) value.getValue()));
            }

            VehicleRoutingTransportCosts costMatrix = costMatrixBuilder.build();
            return costMatrix;
        } catch (Exception e) {
            throw new OurException(-1, "Wrong CostMatrix");
        }
    }

}
