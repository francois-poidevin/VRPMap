package com.poidevin.inbound.OSRM;

import java.util.List;

public class GeoRoutingJson {
	private String code;
	private List<Route> routes;
	private List<Waypoint> waypoints;
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public List<Route> getRoutes() {
		return routes;
	}
	public void setRoutes(List<Route> routes) {
		this.routes = routes;
	}
	public List<Waypoint> getWaypoints() {
		return waypoints;
	}
	public void setWaypoints(List<Waypoint> waypoints) {
		this.waypoints = waypoints;
	}
}
