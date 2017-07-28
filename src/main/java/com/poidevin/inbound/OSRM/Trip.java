package com.poidevin.inbound.OSRM;

import java.util.List;

public class Trip {
	private String geometry;
	private List<Legs> legs;
	private String weight_name;
	private Double weight;
	private Double duration;
	private Double distance;
	
	public String getGeometry() {
		return geometry;
	}
	public void setGeometry(String geometry) {
		this.geometry = geometry;
	}
	public List<Legs> getLegs() {
		return legs;
	}
	public void setLegs(List<Legs> legs) {
		this.legs = legs;
	}
	public String getWeight_name() {
		return weight_name;
	}
	public void setWeight_name(String weight_name) {
		this.weight_name = weight_name;
	}
	public Double getWeight() {
		return weight;
	}
	public void setWeight(Double weight) {
		this.weight = weight;
	}
	public Double getDuration() {
		return duration;
	}
	public void setDuration(Double duration) {
		this.duration = duration;
	}
	public Double getDistance() {
		return distance;
	}
	public void setDistance(Double distance) {
		this.distance = distance;
	}
}
