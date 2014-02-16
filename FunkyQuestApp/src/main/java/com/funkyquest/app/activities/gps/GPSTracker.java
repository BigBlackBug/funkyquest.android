package com.funkyquest.app.activities.gps;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.util.Log;

public class GPSTracker {

	private static final long MIN_UPDATE_DISTANCE = 5;

	private static final long MIN_UPDATE_INTERVAL = 2000;

	public static final int REQUEST_CODE = 100500;

	private final Context context;

	private final LocationListener locationListener;

	// Declaring a Location Manager
	protected LocationManager locationManager;

	// flag for GPS status
	boolean isGPSEnabled = false;

	// flag for network status
	boolean isNetworkEnabled = false;

	// flag for GPS status
	boolean isTrackingEnabled = false;

	public GPSTracker(Context context, LocationListener locationListener) {
		this.context = context;
		this.locationListener = locationListener;
	}

	/**
	 * Function to show settings alert dialog
	 * On pressing Settings button will lauch Settings Options
	 */
	public static AlertDialog createGpsSettingsDialog(final Activity context) {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

		// Setting Dialog Title
		alertDialog.setTitle("GPS is settings");

		// Setting Dialog Message
		alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

		// On pressing Settings button
		alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				context.startActivityForResult(intent, REQUEST_CODE);
			}
		});

		// on pressing cancel button
		alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		return alertDialog.create();
	}

	public boolean startTracker() {
		locationManager = (LocationManager) context.getSystemService(Service.LOCATION_SERVICE);

		// getting GPS status
		isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

		// getting network status
		isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

		if (!isGPSEnabled && !isNetworkEnabled) {
			return false;
		} else {
			Location location = null;
			this.isTrackingEnabled = true;
			if (isNetworkEnabled) {
				locationManager.requestLocationUpdates(
						LocationManager.NETWORK_PROVIDER,
						MIN_UPDATE_INTERVAL,
						MIN_UPDATE_DISTANCE, locationListener);
				Log.d("Network", "Network");
				if (locationManager != null) {
					location = locationManager.getLastKnownLocation(
							LocationManager.NETWORK_PROVIDER);
					if (location != null) {
						locationListener.onLocationChanged(location);
					}
				}
			}
			// if GPS Enabled get lat/long using GPS Services
			if (isGPSEnabled) {
				if (location == null) {
					locationManager.requestLocationUpdates(
							LocationManager.GPS_PROVIDER,
							MIN_UPDATE_INTERVAL,
							MIN_UPDATE_DISTANCE, locationListener);
					Log.d("GPS Enabled", "GPS Enabled");
					if (locationManager != null) {
						location = locationManager
								.getLastKnownLocation(LocationManager.GPS_PROVIDER);
						if (location != null) {
							locationListener.onLocationChanged(location);
						}
					}
				}
			}
			return true;
		}
	}

	/**
	 * Stop using GPS listener
	 * Calling this function will stop using GPS in your app
	 */
	public void stopTracker() {
		if (locationManager != null) {
			locationManager.removeUpdates(locationListener);
		}
	}

	/**
	 * Function to check GPS/wifi enabled
	 *
	 * @return boolean
	 */
	public boolean isTrackingEnabled() {
		return this.isTrackingEnabled;
	}

}