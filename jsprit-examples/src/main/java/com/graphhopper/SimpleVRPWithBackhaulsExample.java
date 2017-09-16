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
package com.graphhopper;

import com.graphhopper.jsprit.analysis.toolbox.AlgorithmSearchProgressChartListener;
import com.graphhopper.jsprit.analysis.toolbox.GraphStreamViewer;
import com.graphhopper.jsprit.analysis.toolbox.Plotter;
import com.graphhopper.jsprit.analysis.toolbox.Plotter.Label;
import com.graphhopper.jsprit.core.algorithm.VehicleRoutingAlgorithm;
import com.graphhopper.jsprit.core.algorithm.box.Jsprit;
import com.graphhopper.jsprit.core.algorithm.selector.SelectBest;
import com.graphhopper.jsprit.core.algorithm.state.StateManager;
import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.constraint.ConstraintManager;
import com.graphhopper.jsprit.core.problem.constraint.ServiceDeliveriesFirstConstraint;
import com.graphhopper.jsprit.core.problem.job.Delivery;
import com.graphhopper.jsprit.core.problem.job.Pickup;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl.Builder;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleType;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleTypeImpl;
import com.graphhopper.jsprit.core.reporting.SolutionPrinter;
import com.graphhopper.jsprit.core.util.Solutions;

import com.graphhopper.jsprit.io.problem.VrpXMLReader;
import com.graphhopper.jsprit.io.problem.VrpXMLWriter;
import com.graphhopper.jsprit.util.Examples;

import java.io.File;
import java.util.Collection;


public class SimpleVRPWithBackhaulsExample {

    public static void main(String[] args) {
        /*
         * some preparation - create output folder
		 */
        Examples.createOutputFolder();

		/*
         * get a vehicle type-builder and build a type with the typeId "vehicleType" and a capacity of 2
		 */
        VehicleTypeImpl.Builder vehicleTypeBuilder = VehicleTypeImpl.Builder.newInstance("vehicleType").addCapacityDimension(0, 2);
        VehicleType vehicleType = vehicleTypeBuilder.build();

		/*
         * get a vehicle-builder and build a vehicle located at (10,10) with type "vehicleType"
		 */
        Builder vehicleBuilder = VehicleImpl.Builder.newInstance("vehicle");
        vehicleBuilder.setStartLocation(Location.newInstance(10, 10));
        vehicleBuilder.setType(vehicleType);
        VehicleImpl vehicle = vehicleBuilder.build();

		/*
         * build pickups and deliveries at the required locations, each with a capacity-demand of 1.
		 */
        Pickup pickup1 = Pickup.Builder.newInstance("1").addSizeDimension(0, 1).setLocation(Location.newInstance(5, 7)).build();
        Delivery delivery1 = Delivery.Builder.newInstance("2").addSizeDimension(0, 1).setLocation(Location.newInstance(5, 13)).build();

        Pickup pickup2 = Pickup.Builder.newInstance("3").addSizeDimension(0, 1).setLocation(Location.newInstance(15, 7)).build();
        Delivery delivery2 = Delivery.Builder.newInstance("4").addSizeDimension(0, 1).setLocation(Location.newInstance(15, 13)).build();


        VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
        vrpBuilder.addVehicle(vehicle);

        vrpBuilder.addJob(pickup1).addJob(pickup2).addJob(delivery1).addJob(delivery2);

        VehicleRoutingProblem problem = vrpBuilder.build();


        StateManager stateManager = new StateManager(problem);
        ConstraintManager constraintManager = new ConstraintManager(problem, stateManager);
        constraintManager.addConstraint(new ServiceDeliveriesFirstConstraint(), ConstraintManager.Priority.CRITICAL);

        VehicleRoutingAlgorithm algorithm = Jsprit.Builder.newInstance(problem).setStateAndConstraintManager(stateManager,constraintManager)
            .buildAlgorithm();

		/*
         * and search a solution
		 */
        Collection<VehicleRoutingProblemSolution> solutions = algorithm.searchSolutions();

		/*
         * get the best
		 */
        VehicleRoutingProblemSolution bestSolution = Solutions.bestOf(solutions);

        new VrpXMLWriter(problem, solutions).write("output/problem-with-solution.xml");

        SolutionPrinter.print(bestSolution);

		/*
         * plot
		 */
        Plotter plotter = new Plotter(problem, bestSolution);
        plotter.setLabel(Label.SIZE);
        plotter.plot("output/solution.png", "solution");

    }

    public static class SolomonExampleWithSpecifiedVehicleEndLocations {

        public static void main(String[] args) {
            /*
             * some preparation - create output folder
             */
            File dir = new File("output");
            // if the directory does not exist, create it
            if (!dir.exists()) {
                System.out.println("creating directory ./output");
                boolean result = dir.mkdir();
                if (result) System.out.println("./output created");
            }

            /*
             * Build the problem.
             *
             * But define a problem-builder first.
             */
            VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();

            /*
             * A solomonReader reads solomon-instance files, and stores the required information in the builder.
             */
            new VrpXMLReader(vrpBuilder).read("input/deliveries_solomon_specifiedVehicleEndLocations_c101.xml");

            /*
             * Finally, the problem can be built. By default, transportCosts are crowFlyDistances (as usually used for vrp-instances).
             */
            VehicleRoutingProblem vrp = vrpBuilder.build();

            Plotter pblmPlotter = new Plotter(vrp);
            pblmPlotter.plot("output/solomon_C101_specifiedVehicleEndLocations.png", "C101");

            /*
             * Define the required vehicle-routing algorithms to solve the above problem.
             *
             * The algorithm can be defined and configured in an xml-file.
             */
    //		VehicleRoutingAlgorithm vra = new SchrimpfFactory().createAlgorithm(vrp);
            VehicleRoutingAlgorithm vra = Jsprit.createAlgorithm(vrp);
            vra.setMaxIterations(20000);
    //		vra.setPrematureBreak(100);
            vra.getAlgorithmListeners().addListener(new AlgorithmSearchProgressChartListener("output/sol_progress.png"));
            /*
             * Solve the problem.
             *
             *
             */
            Collection<VehicleRoutingProblemSolution> solutions = vra.searchSolutions();

            /*
             * Retrieve best solution.
             */
            VehicleRoutingProblemSolution solution = new SelectBest().selectSolution(solutions);

            /*
             * print solution
             */
            SolutionPrinter.print(solution);

            /*
             * Plot solution.
             */
    //		SolutionPlotter.plotSolutionAsPNG(vrp, solution, "output/solomon_C101_specifiedVehicleEndLocations_solution.png","C101");
            Plotter solPlotter = new Plotter(vrp, solution);
            solPlotter.plot("output/solomon_C101_specifiedVehicleEndLocations_solution.png", "C101");


            new GraphStreamViewer(vrp, solution).setRenderDelay(50).labelWith(GraphStreamViewer.Label.ID).display();


        }

    }
}
