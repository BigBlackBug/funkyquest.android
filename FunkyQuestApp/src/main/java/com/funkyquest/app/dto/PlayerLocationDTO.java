package com.funkyquest.app.dto;

public class PlayerLocationDTO {
    private double latitude;
    private double longitude;

    public PlayerLocationDTO() {

    }

	public PlayerLocationDTO(double latitude, double longitude) {
		
	}

	public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getdoubleutude() {
        return longitude;
    }

    public void setdoubleutude(double doubleutude) {
        this.longitude = doubleutude;
    }
}