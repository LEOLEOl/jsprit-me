/*
 * Licensed to GraphHopper GmbH under one or more contributor
 * license agreements. See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.
 *
 * GraphHopper GmbH licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.graphhopper.jsprit.examples;

import com.graphhopper.jsprit.analysis.toolbox.Plotter;
import com.graphhopper.jsprit.core.algorithm.VehicleRoutingAlgorithm;
import com.graphhopper.jsprit.core.algorithm.box.Jsprit;
import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem.FleetSize;
import com.graphhopper.jsprit.core.problem.cost.VehicleRoutingTransportCosts;
import com.graphhopper.jsprit.core.problem.job.Break;
import com.graphhopper.jsprit.core.problem.job.Pickup;
import com.graphhopper.jsprit.core.problem.job.Service;
import com.graphhopper.jsprit.core.problem.job.Shipment;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.problem.solution.route.VehicleRoute;
import com.graphhopper.jsprit.core.problem.solution.route.activity.TimeWindow;
import com.graphhopper.jsprit.core.problem.solution.route.activity.TourActivity;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleType;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleTypeImpl;
import com.graphhopper.jsprit.core.reporting.SolutionPrinter;
import com.graphhopper.jsprit.core.util.Solutions;
import com.graphhopper.jsprit.core.util.VehicleRoutingTransportCostsMatrix;
import com.graphhopper.jsprit.util.Examples;

import java.util.Collection;


/**
 * Illustrates how you can use jsprit with an already compiled distance and time matrix.
 *
 * @author schroeder
 */

//this is just a change for git to happen
public class My_CostMatrixExample {

    public static void main(String[] args) {
        /*
         * some preparation - create output folder
		 */
        Examples.createOutputFolder();

        Location loc0 = Location.newInstance("0");
        Location loc1 = Location.newInstance("1");
        Location loc2 = Location.newInstance("2");
        Location loc3 = Location.newInstance("3");
        Location loc4 = Location.newInstance("4");

        //////////
        VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance().setFleetSize(FleetSize.FINITE);

        VehicleType type1 = VehicleTypeImpl.Builder.newInstance("type1").addCapacityDimension(0, 5).addCapacityDimension(1, 50)
            .setCostPerDistance(1)
            .setCostPerServiceTime(0)
            .setCostPerTransportTime(1)
            .setCostPerWaitingTime(10)
            .setFixedCost(0)
            .build();
        VehicleType type2 = VehicleTypeImpl.Builder.newInstance("type2").addCapacityDimension(0, 5).addCapacityDimension(1, 50)
            .setCostPerDistance(1)
            .setCostPerServiceTime(0)
            .setCostPerTransportTime(1)
            .setCostPerWaitingTime(10)
            .setFixedCost(11)
            .build();
        // Shipment
        VehicleImpl vehicle1 = VehicleImpl.Builder.newInstance("1")
            .setStartLocation(loc2).setEndLocation(loc1)
            .setType(type1)
            .setBreak(Break.Builder.newInstance("First Break").setTimeWindow(TimeWindow.newInstance(4, 7)).setServiceTime(0.5).build())

            .build(),
        vehicle2 = VehicleImpl.Builder.newInstance("2")
            .setStartLocation(loc1).setEndLocation(loc2)
            .setBreak(Break.Builder.newInstance("Second Break").setTimeWindow(TimeWindow.newInstance(6, 7)).setServiceTime(0.5).build())
            .setType(type2)
            //.setBreak(Break.Builder.newInstance("First Break").setTimeWindow(TimeWindow.newInstance(5, 10)).setServiceTime(10).build())
            .build(),
        vehicle3 = VehicleImpl.Builder.newInstance("3")
            .setStartLocation(loc0).setEndLocation(loc3)
            .setType(type2)
            .build();

//        Shipment s1 = Shipment.Builder.newInstance("s1").addSizeDimension(0,1).addSizeDimension(1, 10)
//            .setPickupLocation(Location.newInstance("1")).setDeliveryLocation(Location.newInstance("3"))
//            .setPickupServiceTime(1).setDeliveryServiceTime(1)
//            .build();
        Shipment s1 = Shipment.Builder.newInstance("s1").addSizeDimension(0,1).addSizeDimension(1, 1)
            .setPickupLocation(loc0).setDeliveryLocation(loc1)
            .setPickupServiceTime(0).setDeliveryServiceTime(0)
            //.addDeliveryTimeWindow(TimeWindow.newInstance(0, 100))
            .build();
        Shipment s2 = Shipment.Builder.newInstance("s2").addSizeDimension(0,1).addSizeDimension(1, 2)
            .setPickupLocation(loc1).setDeliveryLocation(loc0)
            .setPickupServiceTime(0).setDeliveryServiceTime(0)
            .addDeliveryTimeWindow(TimeWindow.newInstance(0, 100))
            //.setPickupTimeWindow(new TimeWindow(0, 48)).setDeliveryTimeWindow(new TimeWindow(0, 48))
            //.addPickupTimeWindow(new TimeWindow(0, 48)).addDeliveryTimeWindow(0, 48)
            .build();

        Service b1 = Pickup.Builder.newInstance("b1").addSizeDimension(0, 0).addSizeDimension(1, 0)
            .setLocation(loc3)
            //.setTimeWindow(TimeWindow.newInstance(2.5, 2.5))
            .build();
        Service b2 = Pickup.Builder.newInstance("b2").addSizeDimension(0, 0).addSizeDimension(1, 0)
            .setLocation(loc4)
            //.setTimeWindow(TimeWindow.newInstance(5.0, 5.0))
            .build();

        vrpBuilder.addVehicle(vehicle1);//.addVehicle(vehicle2);//.addVehicle(vehicle3);
        vrpBuilder.addJob(s1);
        //vrpBuilder.addJob(s2);
        vrpBuilder.addJob(b1).addJob(b2);

        VehicleRoutingTransportCostsMatrix.Builder costMatrixBuilder = VehicleRoutingTransportCostsMatrix.Builder.newInstance(true);

        costMatrixBuilder.addTransportDistance("0", "1", 0.0);
        costMatrixBuilder.addTransportDistance("0", "2", 0.0);
        costMatrixBuilder.addTransportDistance("2", "1", 0.0);

        costMatrixBuilder.addTransportDistance("0", "2", 0.0);
        costMatrixBuilder.addTransportDistance("0", "3", 0.0);
        costMatrixBuilder.addTransportDistance("0", "4", 0.0);

        costMatrixBuilder.addTransportDistance("1", "2", 0.0);
        costMatrixBuilder.addTransportDistance("1", "3", 00.0);
        costMatrixBuilder.addTransportDistance("1", "4", 0.0);

        costMatrixBuilder.addTransportDistance("2", "3", 0.0);
        costMatrixBuilder.addTransportDistance("2", "4", 0.0);

        costMatrixBuilder.addTransportDistance("3", "4", 0.0);


        costMatrixBuilder.addTransportTime("0", "1", 10.0);
        costMatrixBuilder.addTransportTime("0", "2", 1.0);
        costMatrixBuilder.addTransportTime("0", "3", 2.5);
        costMatrixBuilder.addTransportTime("0", "4", 5.0);

        costMatrixBuilder.addTransportTime("1", "2", 20.0);
        costMatrixBuilder.addTransportTime("1", "3", 7.5);
        costMatrixBuilder.addTransportTime("1", "4", 5.0);

        costMatrixBuilder.addTransportTime("2", "3", 100.0);
        costMatrixBuilder.addTransportTime("2", "4", 100.0);

        costMatrixBuilder.addTransportTime("3", "4", 2.5);

        VehicleRoutingTransportCosts costMatrix = costMatrixBuilder.build();

        vrpBuilder.setRoutingCost(costMatrix);

        VehicleRoutingProblem vrp = vrpBuilder.build();

        VehicleRoutingAlgorithm vra = Jsprit.createAlgorithm(vrp);

        Collection<VehicleRoutingProblemSolution> solutions = vra.searchSolutions();

        SolutionPrinter.print(Solutions.bestOf(solutions));
        VehicleRoutingProblemSolution sol = Solutions.bestOf(solutions);
        System.out.print(sol.getCost());


        for (VehicleRoute route: sol.getRoutes()){
            System.out.printf(route.getStart().getLocation().getId());
            for ( TourActivity acti : route.getActivities()){
                System.out.print( acti.getArrTime() +",");
            }
        }

        new Plotter(vrp, Solutions.bestOf(solutions)).plot("output/yo.png", "po");

    }

}
