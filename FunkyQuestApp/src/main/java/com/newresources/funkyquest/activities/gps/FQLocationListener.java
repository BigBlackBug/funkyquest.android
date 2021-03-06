package com.newresources.funkyquest.activities.gps;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import com.newresources.funkyquest.FQWebSocketClient;
import com.newresources.funkyquest.dto.PlayerLocationDTO;

/**
 * Created by BigBlackBug on 2/15/14.
 */
public class FQLocationListener implements LocationListener {

	private final FQWebSocketClient socketClient;

	private final Long userID;

	public FQLocationListener(FQWebSocketClient socketClient, Long userID) {
		this.socketClient = socketClient;
		this.userID = userID;
	}

	@Override
	public void onLocationChanged(Location location) {
		if (socketClient.isConnected()) {
			socketClient.sendLocation(
					new PlayerLocationDTO(userID, location.getLatitude(), location.getLongitude()));
		}
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

	@Override
	public void onProviderEnabled(String provider) {
		//TODO onProviderEnabled
	}

	@Override
	public void onProviderDisabled(String provider) {
		//TODO onProviderDisabled
	}
}
