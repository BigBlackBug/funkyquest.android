package com.funkyquest.app.dto;

public class PlayerLocationDTO {

	private double latitude;

	private double longitude;

	private long userID;

	public PlayerLocationDTO() {

	}

	public PlayerLocationDTO(long userID, double latitude, double longitude) {
		this.userID = userID;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public long getUserID() {
		return userID;
	}

	public void setUserID(long userID) {
		this.userID = userID;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
}