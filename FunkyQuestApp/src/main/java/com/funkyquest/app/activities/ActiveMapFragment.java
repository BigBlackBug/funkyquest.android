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
import com.funkyquest.app.FunkyQuestApplication;
import com.funkyquest.app.R;
import com.funkyquest.app.WebSocketClientListener;
import com.funkyquest.app.activities.gps.GPSTracker;
import com.funkyquest.app.api.FQServiceAPI;
import com.funkyquest.app.api.SimpleNetworkCallback;
import com.funkyquest.app.dto.FQLocation;
import com.funkyquest.app.dto.LocationDTO;
import com.funkyquest.app.dto.PlayerLocationDTO;
import com.funkyquest.app.dto.util.EventType;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by BigBlackBug on 2/1/14.
 */
public class ActiveMapFragment extends Fragment {

	public static final int DEFAULT_RADIUS = 3;

	public static final int DEFAULT_TASK_ZOOM = 15;

	public static final int DEFAULT_GAME_ZOOM = 10;

	private static View view;

	private final GameActivity gameActivity;

	private final FQWebSocketClient socketClient;

	private final GPSTracker gpsTracker;

	private final Long gameLocationID;

	private final Long userID;

	private String text;

	private GoogleMap map;

	private FQServiceAPI serviceAPI;

	private CircleOptions taskCircle;

	private FQLocation taskLocation;

	private Map<Long,PlayerLocationDTO> players = new HashMap<Long, PlayerLocationDTO>();

	private MarkerOptions taskMarker;

	public ActiveMapFragment(String text, Long gameLocationID, Long userID,GameActivity aGameActivity) {
		this.text = text;
		this.userID = userID;
		this.gameLocationID = gameLocationID;
		this.gameActivity = aGameActivity;
		gpsTracker = aGameActivity.getGpsTracker();
		serviceAPI = FunkyQuestApplication.getServiceAPI();
		this.socketClient = gameActivity.getSocketClient();
		socketClient.addMessageListener(EventType.LOCATION_CHANGED,
        new WebSocketClientListener.FQMessageListener<PlayerLocationDTO>() {
            @Override
            public void onMessage(PlayerLocationDTO locationDTO) {
            long userID = locationDTO.getUserID();
            final double latitude = locationDTO.getLatitude();
            final double longitude = locationDTO.getLongitude();
            Log.i(getTag(),
                  "User " + userID + " coords: " + latitude +
                          " " + longitude);
            addPlayersLocation(userID, latitude, longitude);
            }
        });
	}

	public void addPlayersLocation(long playerID, final double latitude, final double longitude) {
		players.put(playerID,new PlayerLocationDTO(playerID,latitude,longitude));
		updateMap();
	}

	public void moveCamera(LatLng location, float zoom) {
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, zoom));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		if (view != null) {
			ViewGroup parent = (ViewGroup) view.getParent();
			if (parent != null) {
				parent.removeView(view);
			}
		}
		try {
			view = inflater.inflate(R.layout.map_fragment, container, false);
			MapFragment fragmentById =
					(MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);
			map = fragmentById.getMap();
//			map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
//				@Override
//				public void onMapClick(LatLng latLng) {
//					if (taskCircle != null) {
//						  if(taskClicked(latLng)){
//							  Marker melbourne = map.addMarker(new MarkerOptions()
//									                                    .position(latLng)
//									                                    .title(taskLocation.getName()));
//							  melbourne.showInfoWindow();
//						  }
//					}
//				}
//			});
//			Location playersLocation = gpsTracker.getLastKnownLocation();
//			addPlayersLocation(userID, playersLocation.getLatitude(), playersLocation.getLongitude());
			serviceAPI.getGameLocation(gameLocationID, new SimpleNetworkCallback<LocationDTO>() {
				@Override
				public void onSuccess(LocationDTO location) {
					Log.i("MAP","got game location");
					FQLocation fqLocation = location.getJson();
					moveCamera(new LatLng(fqLocation.getLat(), fqLocation.getLon()),
					           DEFAULT_GAME_ZOOM);
				}
			});
		} catch (InflateException e) {
		}
		return view;
	}

	private boolean taskClicked(LatLng position) {
		LatLng center = taskCircle.getCenter();
		double radius = taskCircle.getRadius();
		float[] distance = new float[1];
		Location.distanceBetween(position.latitude, position.longitude, center.latitude, center.longitude, distance);
		return distance[0] < radius;
	}

	public void setTaskLocation(FQLocation location) {
		this.taskLocation = location;
		int fillColor = Color.parseColor("#7D499BFF");
		int strokeColor = Color.parseColor("#3251FF");
		LatLng center = new LatLng(location.getLat(), location.getLon());
		taskCircle = new CircleOptions().center(center).
		radius(location.getRadius()).strokeColor(strokeColor).strokeWidth(2.0f).
				fillColor(fillColor).zIndex(1.0f);
		taskMarker = new MarkerOptions().position(center).draggable(false);
		updateMap();
	}

	private void updateMap() {
		gameActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				map.clear();
				map.addCircle(taskCircle);
				map.addMarker(taskMarker).showInfoWindow();
				for(Map.Entry<Long,PlayerLocationDTO> entry:players.entrySet()){
					int color = Color.BLUE;
					if(entry.getKey().equals(userID)){
						color = Color.GREEN;
					}
					PlayerLocationDTO locationDTO = entry.getValue();
					map.addCircle(new CircleOptions().center(new LatLng(locationDTO.getLatitude(),
					                                                    locationDTO.getLongitude()))
							              .radius(DEFAULT_RADIUS).strokeColor(color));
				}

			}
		});
	}

	public void centerOnTask() {
		moveCamera(new LatLng(taskLocation.getLat(),taskLocation.getLon()), DEFAULT_TASK_ZOOM);
	}
}
