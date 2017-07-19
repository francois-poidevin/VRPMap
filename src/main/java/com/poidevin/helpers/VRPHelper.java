package com.poidevin.helpers;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.graphhopper.jsprit.analysis.toolbox.Plotter;
import com.graphhopper.jsprit.core.algorithm.VehicleRoutingAlgorithm;
import com.graphhopper.jsprit.core.algorithm.box.Jsprit;
import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem.FleetSize;
import com.graphhopper.jsprit.core.problem.cost.VehicleRoutingTransportCosts;
import com.graphhopper.jsprit.core.problem.job.Service;
import com.graphhopper.jsprit.core.problem.job.Shipment;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.problem.solution.route.VehicleRoute;
import com.graphhopper.jsprit.core.problem.solution.route.activity.TourActivity;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleType;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleTypeImpl;
import com.graphhopper.jsprit.core.reporting.SolutionPrinter;
import com.graphhopper.jsprit.core.reporting.SolutionPrinter.Print;
import com.graphhopper.jsprit.core.util.Solutions;
import com.poidevin.controller.VRPController;
import com.poidevin.models.Spot;
import com.poidevin.models.SpotType;

public class VRPHelper {
	
	private final int WEIGHT_INDEX = 0;
	
	//Jsprit obj
	private VehicleType vehicleType;
	
	private List<Spot> lstSpot;
	
	private boolean bInfiniteFleet = true;
	

	public void setLstPoint(List<Spot> _lstSpot) {
		this.lstSpot = _lstSpot;
	}

	public void setisInfiniteFleet(boolean _IsInfiniteFleet) {
		this.bInfiniteFleet = _IsInfiniteFleet;
	}
	
	public List<Spot> solveProblem() throws IOException
	{
		List<Spot> lstOrderedSpot = new ArrayList<>();
		
		if( this.lstSpot != null &&  ! this.lstSpot.isEmpty() )
		{
			int inbParcel = 0;
			//calculate the total parcel number to pickup and deliverying
			for(Spot pt : this.lstSpot)
			{
				if(SpotType.DELIVERY.equals(pt.getType()) ||
				SpotType.PICKUP.equals(pt.getType()))
				{
					inbParcel += pt.getNbParcel();					
				}
			}
			
			/*
			 * get a vehicle type-builder and build a type with the typeId "vehicleType" and a capacity of total parcel number
			 */
			
			VehicleTypeImpl.Builder vehicleTypeBuilder = VehicleTypeImpl.Builder.newInstance("vehicleType")
					.addCapacityDimension(WEIGHT_INDEX,
											inbParcel);
			vehicleType = vehicleTypeBuilder.build();
			
			/*
			 * get a vehicle-builder and build a vehicle located at Depot/Vehicle location with type "vehicleType"
			 */
			VehicleImpl.Builder vehicleBuilder = VehicleImpl.Builder.newInstance("vehicle");
			VehicleImpl vehicle = null;
			List<Service> lstService = new ArrayList<>();
			List<Shipment> lstShipment = new ArrayList<>();
			
			//Define depot-Vehicule location and Service Location
			for( Spot pt : lstSpot)
			{
				
				if(vehicle == null && SpotType.DEPOT.equals(pt.getType()))
				{
					vehicleBuilder.setStartLocation(Location.newInstance(pt.getLat(), 
																		pt.getLon()));
					vehicleBuilder.setType(vehicleType);
					vehicle = vehicleBuilder.build();
					
				}
				else if ( SpotType.DELIVERY.equals(pt.getType()))
				{
					//TODO define here the FDS/EPG constraint
					Service service = Service.Builder
							.newInstance(String.valueOf(pt.getID())) //unique Service ID 
							.addSizeDimension(WEIGHT_INDEX,pt.getNbParcel()) //define parcels numbers per delivery depot
							.setLocation(Location.newInstance(pt.getLat(), 
																pt.getLon())) //location of the delivery point
							.setServiceTime(pt.getTaskDuration()).build(); //5*60=300 sec per delivery
					
					lstService.add(service);
				}
				else if( SpotType.PICKUP.equals(pt.getType()))
				{
					//nothing done yet
					Shipment shipment = Shipment.Builder
							.newInstance(String.valueOf(pt.getID()))
							.addSizeDimension(WEIGHT_INDEX,pt.getNbParcel())
							.setPickupLocation(Location.newInstance(pt.getLat(), 
																	pt.getLon()))
							.setPickupServiceTime(pt.getTaskDuration()).build();
					lstShipment.add(shipment);
				}
			}

			//create the OSRM route matrix cost
			OsrmVRPTransportCostMatrixHelper.Builder builder = 
					OsrmVRPTransportCostMatrixHelper.Builder.newInstance(new URL("http://osm.gls-france.com/osrm/table/v1/driving/") );
			/**
			 * Official public server, often done, so keep in case
			 * OsrmVRPTransportCostMatrixHelper.Builder.newInstance(new URL("http://router.project-osrm.org/table/v1/driving/") );
			 */
			OsrmVRPTransportCostMatrixHelper clsOSRMVRPCostMatrixHlp = new OsrmVRPTransportCostMatrixHelper();
			//feed the OSRM route matrix cost
			//add start location
			builder.addLocation(vehicle.getStartLocation());
			//add each job location
			for(Service srv : lstService)
			{
				builder.addLocation(srv.getLocation());
			}

			for(Shipment shp: lstShipment)
			{
				builder.addLocation(shp.getPickupLocation());
			}
			
			VehicleRoutingTransportCosts transportCosts = clsOSRMVRPCostMatrixHlp.build(builder);
			
			//create the vehicle routing problem
			VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
			vrpBuilder.addVehicle(vehicle);
			vrpBuilder.setRoutingCost(transportCosts); //set the route change cost from OSRM route matrix cost
			vrpBuilder.setFleetSize(FleetSize.FINITE); //only one vehicle per tour

			//add all service-delivery to the problem
			for(Service srv : lstService)
			{
				vrpBuilder.addJob(srv);				
			}

			//add all shipment-Pickup to the problem
			for(Shipment shp : lstShipment)
			{
				vrpBuilder.addJob(shp);				
			}
			
			/*
			 * build the problem
			 * by default, the problem is specified such that FleetSize is INFINITE, i.e. an infinite number of
			 * the defined vehicles can be used to solve the problem
			 * by default, transport costs are computed as Euclidean distances
			 */
			VehicleRoutingProblem problem = vrpBuilder.build();
			
			
			/*
			* get the algorithm out-of-the-box.
			*/
			VehicleRoutingAlgorithm algorithm = Jsprit.createAlgorithm(problem);

			/*
			* and search a solution which returns a collection of solutions (here only one solution is constructed)
			*/
			Collection<VehicleRoutingProblemSolution> solutions = algorithm.searchSolutions();

			/*
			 * use the static helper-method in the utility class Solutions to get the best solution (in terms of least costs)
			 */
			VehicleRoutingProblemSolution bestSolution = Solutions.bestOf(solutions);
			
			//add the TimeStart and TimeEnd to each point
			List<VehicleRoute> lstBestSolution = new ArrayList<>(bestSolution.getRoutes());
			
			StringWriter stringWriter = new StringWriter();
			PrintWriter prWri = new PrintWriter(stringWriter);
			
			//Display in logs the results
			SolutionPrinter.print(prWri, problem, bestSolution, Print.VERBOSE);
			
			VRPController.addInfo("\n" + stringWriter.toString());

			//Generate an result image 
			new Plotter(problem,bestSolution).invertCoordinates(true).setScalingFactor(16).plot(System.getProperty("java.io.tmpdir" )+"solution.png", "solution");
			VRPController.addInfo( "take a look in "+ System.getProperty("java.io.tmpdir" )+"solution.png" +" !" );
			
			//construct the ordered spot list 
			for(VehicleRoute vcRt : lstBestSolution)
			{
				for(TourActivity trAc : vcRt.getTourActivities().getActivities())
				{
					for(Spot spt : lstSpot)
					{
						if( spt.getID().equals(String.valueOf(trAc.getIndex() ) ) &&
							! SpotType.DEPOT.equals(spt.getType()))
						{
							lstOrderedSpot.add(spt);
						}
					}
				}
			}
			
		}else
		{
			VRPController.addInfo("VRP : Nothing to process");
		}
		
		return lstOrderedSpot;
		
	}

}
