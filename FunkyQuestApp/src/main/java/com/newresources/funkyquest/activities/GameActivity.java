package com.newresources.funkyquest.activities;

import android.app.*;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newresources.funkyquest.FQWebSocketClient;
import com.newresources.funkyquest.FunkyQuestApplication;
import com.newresources.funkyquest.R;
import com.newresources.funkyquest.WebSocketClientListener;
import com.newresources.funkyquest.activities.gps.FQLocationListener;
import com.newresources.funkyquest.activities.gps.GPSTracker;
import com.newresources.funkyquest.api.FQServiceAPI;
import com.newresources.funkyquest.api.SimpleNetworkCallback;
import com.newresources.funkyquest.dto.GameDTO;
import com.newresources.funkyquest.dto.InGameTaskDTO;
import com.newresources.funkyquest.dto.InGameTaskSequenceDTO;
import com.newresources.funkyquest.dto.TeamDTO;
import com.newresources.funkyquest.dto.util.Subscription;
import com.newresources.funkyquest.util.Constants;
import com.newresources.funkyquest.util.FQObjectMapper;
import com.newresources.funkyquest.util.NotificationService;
import com.newresources.funkyquest.util.RequestCodes;
import com.newresources.funkyquest.util.websockets.WebSocketClient;

import java.io.IOException;
import java.util.*;

public class GameActivity extends Activity implements ActionBar.TabListener {

    public static final int TAB_NUMBER = 4;

	private static final String TAG_ACTIVITY = "GameActivity";

	private final ObjectMapper mapper = new FQObjectMapper();

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;

	private FQWebSocketClient socketClient;

	private GPSTracker gpsTracker;

	private View enableTrackingLayout;

	private GameStatsView gameStatsView;

	private View pbLayout;

	private long userID;

	private long teamID;

	private long gameID;

	private UUID connectionID;

	private GameDTO currentGame;

	private InGameTaskDTO currentTask;

	private NotificationService notificationService;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		notificationService = new NotificationService(this);

        setContentView(R.layout.activity_main);
		enableTrackingLayout = findViewById(R.id.layout_enable_gps);
		Button enableTrackingButton =
				(Button) enableTrackingLayout.findViewById(R.id.button_enable_tracking);
		enableTrackingButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog gpsSettingsDialog =
						GPSTracker.createGpsSettingsDialog(GameActivity.this);
				gpsSettingsDialog.show();
			}
		});

		pbLayout = findViewById(R.id.preparing_activity_status);

		if(savedInstanceState == null){
			Bundle data = getIntent().getBundleExtra(Constants.GAME_DATA_BUNDLE);
			new GameInitializer().execute(data);
		} else {
			new GameInitializer().execute(savedInstanceState);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putLong("userID", userID);
		try {
			outState.putString("currentGame", mapper.writeValueAsString(currentGame));
			outState.putString("currentTask", mapper.writeValueAsString(currentTask));
		} catch (JsonProcessingException e) {
		}
		super.onSaveInstanceState(outState);
	}

	private final class GameInitializer extends AsyncTask<Bundle,Void,Void>{

		@Override
		protected Void doInBackground(Bundle... params) {
			userID = params[0].getLong("userID");
			String currentGameJson =params[0].getString("currentGame");
			String currentTaskJson = params[0].getString("currentTask");
			try {
				long before = SystemClock.uptimeMillis();
				currentGame = mapper.readValue(currentGameJson, GameDTO.class);
				currentTask = mapper.readValue(currentTaskJson,InGameTaskDTO.class);
				Log.i("Response", "Deserializing dto's in GameActivity.onCreate took " +
						(SystemClock.uptimeMillis() - before) + "ms");
			} catch (IOException e) {
			}
			Set<InGameTaskSequenceDTO> teamTasks = currentGame.getTeamTasks();
			for(InGameTaskSequenceDTO dto:teamTasks){
				TeamDTO team = dto.getTeam();
				if(team.getTeammates().contains(userID)){
					teamID = team.getId();
					break;
				}
			}
			gameID = currentGame.getId();
			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			socketClient = FunkyQuestApplication.getWebSocketClient(userID);
			socketClient.setSocketLifecycleListener(
				new WebSocketClientListener.SocketLifeCycleListener() {
					private static final String TAG = "websocketlistener";
					@Override
					public void onError(WebSocketClient.Listener.ErrorType errorType,
					                    Exception exception) {
						Log.i(TAG,"error "+errorType,exception);
						notificationService.showNotification("FunkyQuest","Потеряно соединение с сервером",false);
						//TODO click-reconnect. progbar
					}

					@Override
					public void onDisconnect(WebSocketClient.Listener.Reason reason,
					                         String... message) {
						Log.i(TAG,"disconnected "+reason);
						if(reason!= WebSocketClient.Listener.Reason.MANUAL){
							socketClient.connect();
						}
					}

					@Override
					public void onConnect() {
						Log.i(TAG,"connected");
					}
				});
			socketClient.setSubscriptionListener(new WebSocketClientListener.FQMessageListener<UUID>() {
				@Override
				public void onMessage(UUID connID) {
					Log.i(TAG_ACTIVITY,"onsubscrubed");
					connectionID = connID;
					addSubscriptions();
				}
			});
			socketClient.connect();
			gpsTracker = new GPSTracker(getApplicationContext(),new FQLocationListener(socketClient,userID));

			boolean trackingEnabled = gpsTracker.startTracker();
			if(!trackingEnabled){
				enableTrackingLayout.setVisibility(View.VISIBLE);
			}

			gameStatsView = new GameStatsView(GameActivity.this, currentTask, currentGame.getStartDate());
			LinearLayout mainLayout = (LinearLayout) findViewById(R.id.layout_game_activity_main);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			params.setMargins(3,0,3,0);
			mainLayout.addView(gameStatsView, 0,
			                   params);
			// Set up the action bar.
			final ActionBar actionBar = getActionBar();
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
			actionBar.setDisplayShowTitleEnabled(false);
			actionBar.setDisplayShowHomeEnabled(false);

			// Create the adapter that will return a fragment for each of the three
			// primary sections of the activity.
			mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

			// Set up the ViewPager with the sections adapter.
			mViewPager = (ViewPager) findViewById(R.id.pager);
			mViewPager.setAdapter(mSectionsPagerAdapter);
			mViewPager.setOffscreenPageLimit(TAB_NUMBER);

			// When swiping between different sections, select the corresponding
			// tab. We can also use ActionBar.Tab#select() to do this if we have
			// a reference to the Tab.
			mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
				@Override
				public void onPageSelected(int position) {
					actionBar.setSelectedNavigationItem(position);
				}
			});

			// For each of the sections in the app, add a tab to the action bar.
			for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
				// Create a tab with text corresponding to the page title defined by
				// the adapter. Also specify this Activity object, which implements
				// the TabListener interface, as the callback (listener) for when
				// this tab is selected.
				actionBar.addTab(
						actionBar.newTab()
								.setText(mSectionsPagerAdapter.getPageTitle(i))
								.setTabListener(GameActivity.this));
			}
			hideProgressBar();
		}
	}

	public void addSubscriptions(){
		FQServiceAPI serviceAPI = FunkyQuestApplication.getServiceAPI();
		Subscription subscription = new Subscription();
		subscription.setTeamID(teamID);
		Subscription subscription2 = new Subscription();
		subscription2.setTeamID(teamID);
		subscription2.setGameID(gameID);
		Subscription subscription3 = new Subscription();
		subscription3.setGameID(gameID);
		serviceAPI.addSubscriptions(connectionID,
	        Arrays.asList(subscription, subscription2, subscription3),
	        new SimpleNetworkCallback<Void>() {
	            @Override
	            public void onSuccess(Void arg) {
                Log.i(TAG_ACTIVITY, "addSubscriptions success");
	            }
	        });
	}

	long getGameId() {
		return gameID;
	}

	GameStatsView getGameStatsView() {
		return gameStatsView;
	}

	FQWebSocketClient getSocketClient() {
		return socketClient;
	}

	InGameTaskDTO getTaskDTO() {
		return currentTask;
	}
	void hideProgressBar() {
		pbLayout.setVisibility(View.GONE);
		mViewPager.setVisibility(View.VISIBLE);
	}

	void showProgressBar(String text){
		TextView tv = (TextView) pbLayout.findViewById(R.id.preparing_status_message);
		tv.setText(text);
		pbLayout.setVisibility(View.VISIBLE);
		mViewPager.setVisibility(View.GONE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == RequestCodes.OPEN_GPS_SETTINGS_REQUEST_CODE){
			boolean trackingEnabled = gpsTracker.startTracker();
			if(trackingEnabled){
				Location playersLocation = gpsTracker.getLastKnownLocation();
				if(playersLocation!=null){
					mSectionsPagerAdapter.getMapFragment().addPlayersLocation(userID, playersLocation.getLatitude(), playersLocation.getLongitude());
				}
			}
			enableTrackingLayout.setVisibility(trackingEnabled ? View.GONE : View.VISIBLE);
		} else if (requestCode == RequestCodes.TAKE_PICTURE_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
//				Bundle extras = data.getExtras();
//				Bitmap thumbnail = (Bitmap) extras.get("data");
				mSectionsPagerAdapter.currentTaskFragment.showSendImageDialog();
			}
		}
	}
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        switch (item.getItemId()) {
//            case R.id.action_settings:
//                return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

	@Override
	protected void onResume() {
		super.onResume();
		if(gpsTracker!=null){
			gpsTracker.startTracker();
		}
	}

	/* Remove the locationlistener updates when Activity is paused */
	@Override
	protected void onPause() {
		Log.i(TAG_ACTIVITY,"onPause");
		super.onPause();
		if(gpsTracker!=null){
			gpsTracker.stopTracker();
		}
	}

	@Override
	protected void onStart() {
		Log.i(TAG_ACTIVITY,"onStart");
		super.onStart();
	}

	@Override
	protected void onStop() {
		Log.i(TAG_ACTIVITY,"onStop");
		super.onStop();
	}

	@Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

	GPSTracker getGpsTracker() {
		return gpsTracker;
	}

	public ViewPager getViewPager() {
		return mViewPager;
	}

	public SectionsPagerAdapter getSectionsAdapter() {
		return mSectionsPagerAdapter;
	}

	/**
     * A {@link android.support.v13.app.FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private ChatFragment chatFragment;
        private CurrentTaskFragment currentTaskFragment;
        private GameInfoFragment gameInfoFragment;
        private ActiveMapFragment mapFragment;

		public ChatFragment getChatFragment() {
			return chatFragment;
		}

		public CurrentTaskFragment getCurrentTaskFragment() {
			return currentTaskFragment;
		}

		public GameInfoFragment getGameInfoFragment() {
			return gameInfoFragment;
		}

		public ActiveMapFragment getMapFragment() {
			return mapFragment;
		}

		public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            chatFragment = new ChatFragment(socketClient);
            currentTaskFragment = new CurrentTaskFragment(GameActivity.this);
            gameInfoFragment = new GameInfoFragment();
            mapFragment = new ActiveMapFragment("MAP", currentGame.getTemplate().getLocation(),userID,
                                                GameActivity.this);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return currentTaskFragment;
            } else if (position == 1) {
                return mapFragment;
            } else if (position == 2) {
                return gameInfoFragment;
            } else if (position == 3) {
                return chatFragment;
            } else {
                return null;
            }
        }

        @Override
        public int getCount() {
            return TAB_NUMBER;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.task_fragment_title).toUpperCase(l);
                case 1:
                    return getString(R.string.map_fragment_title).toUpperCase(l);
                case 2:
                    return getString(R.string.gameinfo_fragment_title).toUpperCase(l);
                case 3:
                    return getString(R.string.chat_gragment_title).toUpperCase(l);
            }
            return null;
        }
    }

}
