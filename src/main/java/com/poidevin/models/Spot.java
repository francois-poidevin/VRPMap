package com.poidevin.models;


public class Spot {

	private String ID; //Unique ID
	private SpotType type; //Point type (DEPOT | DELIVERY | PICKUP)
	private Double lat;
	private Double lon;
	private Integer timeStart; //Starting Processing time
	private Integer timeEnd; //Ending Processing time
	private Integer NbParcel;
	private Integer TaskDuration; 
	
	public Spot(String _ID, //Unique ID
				SpotType _type, //Point type (DEPOT | DELIVERY | PICKUP)
				Double _lat,
				Double _lon,
				Integer _timeStart, //Starting Processing time
				Integer _timeEnd, //Ending Processing time
				Integer _NbParcel,
				Integer _TaskDuration) {
		this.type = _type;
		this.ID = _ID;
		this.lat = _lat;
		this.lon = _lon;
		this.timeStart = _timeStart;
		this.timeEnd = _timeEnd;
		this.NbParcel = _NbParcel;
		this.TaskDuration = _TaskDuration;
		
	}

	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}

	public SpotType getType() {
		return type;
	}

	public void setType(SpotType type) {
		this.type = type;
	}

	public Double getLat() {
		return lat;
	}

	public void setLat(Double lat) {
		this.lat = lat;
	}

	public Double getLon() {
		return lon;
	}

	public void setLon(Double lon) {
		this.lon = lon;
	}

	public Integer getTimeStart() {
		return timeStart;
	}

	public void setTimeStart(Integer timeStart) {
		this.timeStart = timeStart;
	}

	public Integer getTimeEnd() {
		return timeEnd;
	}

	public void setTimeEnd(Integer timeEnd) {
		this.timeEnd = timeEnd;
	}

	public Integer getNbParcel() {
		return NbParcel;
	}

	public void setNbParcel(Integer nbParcel) {
		NbParcel = nbParcel;
	}

	public Integer getTaskDuration() {
		return TaskDuration;
	}

	public void setTaskDuration(Integer taskDuration) {
		TaskDuration = taskDuration;
	}
	
}
