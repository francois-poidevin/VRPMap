package com.poidevin.helpers;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.util.Coordinate;
import com.graphhopper.jsprit.core.util.VehicleRoutingTransportCostsMatrix;

public class OsrmVRPTransportCostMatrixHelper {

	
	public static class Builder {
		private List<Location> locations = new ArrayList<Location>();
		private URL osrmDistanceTableUrl;
		
		public Builder(URL osrmDistanceTableUrl) {
			this.osrmDistanceTableUrl = osrmDistanceTableUrl;
		}
		
		public static Builder newInstance(URL osrmDistanceTableUrl) {
			return new Builder(osrmDistanceTableUrl);
		}
		
		public Builder addLocation(Location location) {
			locations.add(location);
			return this;
		}
	}
	
	private VehicleRoutingTransportCostsMatrix.Builder costMatrixBuilder = null;
	
	/** constructor
	 * 
	 */
	public OsrmVRPTransportCostMatrixHelper() {
		costMatrixBuilder = VehicleRoutingTransportCostsMatrix.Builder.newInstance(true);
	}
	
	/**
	 * 
	 * @return VehicleRoutingTransportCostsMatrix
	 */
	public VehicleRoutingTransportCostsMatrix build(Builder builder) throws IOException
	{
		StringBuilder strbuilder = new StringBuilder();

		for (Location l : builder.locations) {
			Coordinate coord = l.getCoordinate();
			strbuilder
				.append(coord.getY())
				.append(',')
				.append(coord.getX())
				.append(';');
		}
		
		URL url = new URL(builder.osrmDistanceTableUrl.toString() + strbuilder.deleteCharAt(strbuilder.length()-1).toString());
		
		InputStream stream = url.openStream();
		JsonParser parser = new JsonParser();
		JsonElement root = parser.parse(new InputStreamReader(stream));
		JsonArray array = root.getAsJsonObject().get("durations").getAsJsonArray();
		
		for (int i = 0; i < array.size(); i++) {
			
			JsonArray innerArray = array.get(i).getAsJsonArray();
			
			for (int j = 1; j < innerArray.size(); j++) {
				
				costMatrixBuilder.addTransportDistance(builder.locations.get(i).getCoordinate().toString(), 
						builder.locations.get(j).getCoordinate().toString(), 
						innerArray.get(j).getAsInt());
				costMatrixBuilder.addTransportTime(builder.locations.get(i).getCoordinate().toString(), 
						builder.locations.get(j).getCoordinate().toString(), 
						innerArray.get(j).getAsInt());				
			}
		}
		return costMatrixBuilder.build();
	}
}
