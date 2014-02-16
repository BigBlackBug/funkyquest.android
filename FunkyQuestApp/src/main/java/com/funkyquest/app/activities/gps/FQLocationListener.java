package com.funkyquest.app.activities.gps;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import com.funkyquest.app.FQWebSocketClient;
import com.funkyquest.app.dto.PlayerLocationDTO;

/**
 * Created by BigBlackBug on 2/15/14.
 */
public class FQLocationListener implements LocationListener {

	private final FQWebSocketClient socketClient;

	public FQLocationListener(FQWebSocketClient socketClient) {
		this.socketClient = socketClient;
	}

	@Override
	public void onLocationChanged(Location location) {
		socketClient.sendLocation(
				new PlayerLocationDTO(location.getLatitude(), location.getLongitude()));
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