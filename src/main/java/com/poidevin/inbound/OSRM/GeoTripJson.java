package com.poidevin.inbound.OSRM;

import java.util.List;


public class GeoTripJson {
	private String code;
	private List<Trip> trips;
	private List<Waypoint> waypoints;
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public List<Trip> getTrips() {
		return trips;
	}
	public void setTrips(List<Trip> trips) {
		this.trips = trips;
	}
	public List<Waypoint> getWaypoints() {
		return waypoints;
	}
	public void setWaypoints(List<Waypoint> waypoints) {
		this.waypoints = waypoints;
	}
}
