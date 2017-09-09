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
public class VietCostMatrixExample {

    public static void main(String[] args) {
        /*
         * some preparation - create output folder
		 */
        Examples.createOutputFolder();

        Location loc1 = Location.newInstance("1");
        Location loc2 = Location.newInstance("2");
        Location loc3 = Location.newInstance("3");
        Location loc4 = Location.newInstance("4");

        //////////
        VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance().setFleetSize(FleetSize.FINITE);

        VehicleType type1 = VehicleTypeImpl.Builder.newInstance("type1").addCapacityDimension(0, 5).addCapacityDimension(1, 50)
            .setCostPerDistance(1).setCostPerServiceTime(0).setFixedCost(0).setCostPerTransportTime(0).setCostPerTransportTime(1)
            .build();

        VehicleImpl vehicle1 = VehicleImpl.Builder.newInstance("1")
            .setStartLocation(Location.newInstance("1")).setEndLocation(Location.newInstance("3")).setType(type1)
            //.setReturnToDepot(false)
            .build();

        Shipment s1 = Shipment.Builder.newInstance("s1").addSizeDimension(0,1).addSizeDimension(1, 10)
            .setPickupLocation(Location.newInstance("1")).setDeliveryLocation(Location.newInstance("3"))
            .setPickupServiceTime(1).setDeliveryServiceTime(1)
            .setPickupTimeWindow(new TimeWindow(3, 4)).setDeliveryTimeWindow(new TimeWindow(9, 10))
            .build();
//        Shipment s2 = Shipment.Builder.newInstance("s2").addSizeDimension(0,1).addSizeDimension(1, 10)
//            .setPickupLocation(Location.newInstance("2")).setDeliveryLocation(Location.newInstance("3"))
//            .setPickupServiceTime(1).setDeliveryServiceTime(1)
//            .build();
//        Shipment s3 = Shipment.Builder.newInstance("s3").addSizeDimension(0,1).addSizeDimension(1, 10)
//            .setPickupLocation(Location.newInstance("1")).setDeliveryLocation(Location.newInstance("4"))
//            .setPickupServiceTime(1).setDeliveryServiceTime(1)
//            .build();




        vrpBuilder.addVehicle(vehicle1);
        vrpBuilder.addJob(s1);

        VehicleRoutingTransportCostsMatrix.Builder costMatrixBuilder = VehicleRoutingTransportCostsMatrix.Builder.newInstance(true);

        costMatrixBuilder.addTransportDistance("1", "2", 0.0);
        costMatrixBuilder.addTransportDistance("1", "3", 0.0);
        costMatrixBuilder.addTransportDistance("2", "3", 0.0);
        costMatrixBuilder.addTransportTime("1", "2", 1.0);
        costMatrixBuilder.addTransportTime("1", "3", 1.0);
        costMatrixBuilder.addTransportTime("2", "3", 1.0);

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
