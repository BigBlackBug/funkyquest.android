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
import com.funkyquest.app.util.RequestCodes;

public class GPSTracker {

	private static final long MIN_UPDATE_DISTANCE = 5;

	private static final long MIN_UPDATE_INTERVAL = 2000;

	private final Context context;

	private final LocationListener locationListener;

	protected LocationManager locationManager;

	boolean isGPSEnabled = false;

	boolean isNetworkEnabled = false;

	boolean isTrackingEnabled = false;

	public GPSTracker(Context context, LocationListener locationListener) {
		this.context = context;
		this.locationListener = locationListener;
	}

	public static AlertDialog createGpsSettingsDialog(final Activity context) {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

		// Setting Dialog Title
		alertDialog.setTitle("Настройки местоположения");

		alertDialog.setMessage("Определение местоположения отключено. Хотите открыть меню настроек?");

		alertDialog.setPositiveButton("Настройки", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				context.startActivityForResult(intent, RequestCodes.OPEN_GPS_SETTINGS_REQUEST_CODE);
			}
		});

		alertDialog.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
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
			if (isGPSEnabled) {
				locationManager.requestLocationUpdates(
						LocationManager.GPS_PROVIDER,
						MIN_UPDATE_INTERVAL,
						MIN_UPDATE_DISTANCE, locationListener);
				Log.d("GPSTracker", "using gps");
				if (locationManager != null) {
					location = locationManager
							.getLastKnownLocation(LocationManager.GPS_PROVIDER);
					if (location != null) {
						locationListener.onLocationChanged(location);
					}
				}
			}

			if (isNetworkEnabled) {
				if(location == null){
					locationManager.requestLocationUpdates(
							LocationManager.NETWORK_PROVIDER,
							MIN_UPDATE_INTERVAL,
							MIN_UPDATE_DISTANCE, locationListener);
					Log.d("GPSTracker", "using network");
					if (locationManager != null) {
						location = locationManager.getLastKnownLocation(
								LocationManager.NETWORK_PROVIDER);
						if (location != null) {
							locationListener.onLocationChanged(location);
						}
					}
				}
			}

			return true;
		}
	}

	public void stopTracker() {
		if (locationManager != null) {
			locationManager.removeUpdates(locationListener);
		}
	}

	public Location getLastKnownLocation(){
		Location location = locationManager.getLastKnownLocation(
				LocationManager.GPS_PROVIDER);
		if(location == null){
			location = locationManager.getLastKnownLocation(
					LocationManager.NETWORK_PROVIDER);
			if(location == null){
				throw new RuntimeException("ERROR GETTING COORDINATES");
			}
		}
		return location;
	}

	public boolean isTrackingEnabled() {
		return this.isTrackingEnabled;
	}

}