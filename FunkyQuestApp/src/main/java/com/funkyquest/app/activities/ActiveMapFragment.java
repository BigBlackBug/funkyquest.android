package com.funkyquest.app.activities;

import android.app.Fragment;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.funkyquest.app.FQWebSocketClient;
import com.funkyquest.app.R;
import com.funkyquest.app.WebSocketClientListener;
import com.funkyquest.app.activities.gps.GPSTracker;
import com.funkyquest.app.dto.PlayerLocationDTO;
import com.funkyquest.app.dto.util.EventType;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by BigBlackBug on 2/1/14.
 */
public class ActiveMapFragment extends Fragment {

	public static final int DEFAULT_RADUIS = 1;

	public static final int DEFAULT_ZOOM = 15;

	private final GameActivity gameActivity;

	private final FQWebSocketClient socketClient;

	private final GPSTracker gpsTracker;

	private String text;
	private final LatLng initialLocation;
	private GoogleMap map;

	public ActiveMapFragment(String text, LatLng initialLocation,GameActivity aGameActivity) {
		this.text = text;
		this.initialLocation = initialLocation;
		this.gameActivity = aGameActivity;
		gpsTracker = aGameActivity.getGpsTracker();

		this.socketClient = gameActivity.getSocketClient();
		socketClient.addMessageListener(EventType.LOCATION_CHANGED,
        new WebSocketClientListener.FQMessageListener<PlayerLocationDTO>() {
            @Override
            public void onMessage(PlayerLocationDTO locationDTO) {
                long userID = locationDTO.getUserID();
                final double latitude = locationDTO.getLatitude();
                final double longitude = locationDTO.getLongitude();
                Log.i(getTag(), "User " + userID + " coords: " + latitude + " " + longitude);
	            addCircle(latitude, longitude);
            }
        });
	}

	public void addCircle(final double latitude, final double longitude) {
		gameActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				map.addCircle(new CircleOptions().center(new LatLng(latitude,longitude)).radius(
						DEFAULT_RADUIS).strokeColor(
						Color.BLUE));
			}
		});
	}

	public void moveCamera(LatLng location,float zoom){
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, zoom));
	}

	private static View view;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (view != null) {
			ViewGroup parent = (ViewGroup) view.getParent();
			if (parent != null)
				parent.removeView(view);
		}
		try {
			view = inflater.inflate(R.layout.map_fragment, container, false);
			MapFragment fragmentById =
					(MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);
			map = fragmentById.getMap();
			moveCamera(initialLocation, DEFAULT_ZOOM);
			Location location = gpsTracker.getLastKnownLocation();
			addCircle(location.getLatitude(), location.getLongitude());
		} catch (InflateException e) {
		}
		return view;
	}
}
