package com.poidevin.inbound.OSRM;

import java.util.List;

public class Waypoint {
	private int waypoint_index;
	private int trips_index;
	private String hint;
	private String name;
	private List<Float> location;
	
	public int getWaypoint_index() {
		return waypoint_index;
	}
	public void setWaypoint_index(int waypoint_index) {
		this.waypoint_index = waypoint_index;
	}
	public int getTrips_index() {
		return trips_index;
	}
	public void setTrips_index(int trips_index) {
		this.trips_index = trips_index;
	}
	public String getHint() {
		return hint;
	}
	public void setHint(String hint) {
		this.hint = hint;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Float> getLocation() {
		return location;
	}
	public void setLocation(List<Float> location) {
		this.location = location;
	}

}
