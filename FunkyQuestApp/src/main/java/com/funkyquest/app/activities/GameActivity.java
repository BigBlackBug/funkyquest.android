package com.funkyquest.app.activities;

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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.funkyquest.app.FQWebSocketClient;
import com.funkyquest.app.FunkyQuestApplication;
import com.funkyquest.app.R;
import com.funkyquest.app.WebSocketClientListener;
import com.funkyquest.app.activities.gps.FQLocationListener;
import com.funkyquest.app.activities.gps.GPSTracker;
import com.funkyquest.app.api.FQServiceAPI;
import com.funkyquest.app.api.SimpleNetworkCallback;
import com.funkyquest.app.dto.GameDTO;
import com.funkyquest.app.dto.InGameTaskDTO;
import com.funkyquest.app.dto.InGameTaskSequenceDTO;
import com.funkyquest.app.dto.TeamDTO;
import com.funkyquest.app.dto.util.Subscription;
import com.funkyquest.app.util.FQObjectMapper;
import com.funkyquest.app.util.RequestCodes;
import com.funkyquest.app.util.websockets.WebSocketClient;

import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public class GameActivity extends Activity implements ActionBar.TabListener {

    public static final int TAB_NUMBER = 4;

	private final ObjectMapper mapper = new FQObjectMapper();
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link android.support.v13.app.FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    /**
     * The {@link android.support.v4.view.ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private long userID;
    private long teamID;
    private long gameID;
	private UUID connectionID;
    private GameDTO gameDTO;
    private InGameTaskDTO currentTask;
	private FQWebSocketClient socketClient;
	private GPSTracker gpsTracker;

	private View enableTrackingLayout;

	private GameStatsView gameStatsView;

	private View pbLayout;

	private static final String ACTIVITY_TAG = "GAME_ACTIVITY";
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
		final GameActivity activity=this;

		new AsyncTask<Void,Void,Void>(){

			@Override
			protected Void doInBackground(Void... params) {
				userID = getIntent().getLongExtra("userID", -1L);
				String currentGameJson = getIntent().getStringExtra("currentGame");
				String currentTaskJson = getIntent().getStringExtra("currentTask");
				try {
					long before = SystemClock.uptimeMillis();
					gameDTO = mapper.readValue(currentGameJson, GameDTO.class);
					currentTask = mapper.readValue(currentTaskJson,InGameTaskDTO.class);
					Log.i("Response", "Deserializing dto's in GameActivity.onCreate took " +
							(SystemClock.uptimeMillis() - before) + "ms");
				} catch (IOException e) {
				}
				Set<InGameTaskSequenceDTO> teamTasks = gameDTO.getTeamTasks();
				for(InGameTaskSequenceDTO dto:teamTasks){
					TeamDTO team = dto.getTeam();
					if(team.getTeammates().contains(userID)){
						teamID = team.getId();
						break;
					}
				}
				gameID = gameDTO.getId();
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
								//TODO
							}

							@Override
							public void onDisconnect(WebSocketClient.Listener.Reason reason,
							                         String... message) {
								Log.i(TAG,"disconnected "+reason);
								//TODO
							}

							@Override
							public void onConnect() {
								Log.i(TAG,"connected");
								//TODO
							}
						});
				socketClient.setSubscriptionListener(new WebSocketClientListener.FQMessageListener<UUID>() {
					@Override
					public void onMessage(UUID connID) {
						Log.i(ACTIVITY_TAG,"onsubscrubed");
						connectionID = connID;
						addSubsriptions();
					}
				});
				socketClient.connect();
				gpsTracker = new GPSTracker(getApplicationContext(),new FQLocationListener(socketClient,userID));

				boolean trackingEnabled = gpsTracker.startTracker();
				if(!trackingEnabled){
					enableTrackingLayout.setVisibility(View.VISIBLE);
				}

				gameStatsView = new GameStatsView(activity, currentTask, gameDTO.getStartDate());
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
									.setTabListener(activity));
				}
				hideProgressBar();

			}
		}.execute();

    }

	public void addSubsriptions(){
		FQServiceAPI serviceAPI = FunkyQuestApplication.getServiceAPI();
		Subscription subscription = new Subscription();
		subscription.setTeamID(teamID);
		Subscription subscription2 = new Subscription();
		subscription2.setTeamID(teamID);
		subscription2.setGameID(gameID);
		serviceAPI.addSubscriptions(connectionID, Arrays.asList(subscription,subscription2), new SimpleNetworkCallback<Void>() {
			@Override
			public void onSuccess(Void arg){
				Log.i(ACTIVITY_TAG, "add subs success. ");
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
		super.onPause();
		if(gpsTracker!=null){
			gpsTracker.stopTracker();
		}
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
            chatFragment = new ChatFragment("LOLCHAT",socketClient);
            currentTaskFragment = new CurrentTaskFragment(GameActivity.this);
            gameInfoFragment = new GameInfoFragment("GAMEINFO");
            mapFragment = new ActiveMapFragment("MAP",gameDTO.getTemplate().getLocation(),userID,
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
