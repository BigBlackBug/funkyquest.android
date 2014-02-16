package com.funkyquest.app.activities;

import android.app.*;
import android.content.Intent;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.funkyquest.app.FQWebSocketClient;
import com.funkyquest.app.FunkyQuestApplication;
import com.funkyquest.app.WebSocketClientListener;
import com.funkyquest.app.activities.gps.FQLocationListener;
import com.funkyquest.app.activities.gps.GPSTracker;
import com.funkyquest.app.dto.GameDTO;
import com.funkyquest.app.dto.InGameTaskDTO;
import com.funkyquest.app.dto.InGameTaskSequenceDTO;
import com.funkyquest.app.dto.TeamDTO;
import com.funkyquest.app.util.websockets.WebSocketClient;
import com.qbix.funkyquest.R;

import java.io.IOException;
import java.util.Locale;
import java.util.Set;

public class GameActivity extends Activity implements ActionBar.TabListener {

    public static final int TAB_NUMBER = 4;

	public static final int RESULT_OK = 1;

	private final ObjectMapper mapper = new ObjectMapper();
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link android.support.v13.app.FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;
    /**
     * The {@link android.support.v4.view.ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    private long userID;
    private long teamID;
    private long gameID;
    private GameDTO gameDTO;
    private InGameTaskDTO currentTask;
	private FQWebSocketClient socketClient;
	private GPSTracker gpsTracker;

	private View enableTrackingLayout;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//
	    socketClient = FunkyQuestApplication.getWebSocketClient();
		socketClient.setSocketLifecycleListener(
			new WebSocketClientListener.SocketLifeCycleListener() {
				@Override
				public void onError(WebSocketClient.Listener.ErrorType errorType,
				                    Exception exception) {
					//TODO
				}

				@Override
				public void onDisconnect(WebSocketClient.Listener.Reason reason,
				                         String... message) {
					//TODO
				}

				@Override
				public void onConnect() {
					//TODO
				}
			});
		socketClient.connect();

        userID = getIntent().getLongExtra("userID", -1L);
        String currentGameJson = getIntent().getStringExtra("currentGame");
	    String currentTaskJson = getIntent().getStringExtra("currentTask");
        try {
            gameDTO = mapper.readValue(currentGameJson, GameDTO.class);
	        currentTask = mapper.readValue(currentTaskJson,InGameTaskDTO.class);
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

		enableTrackingLayout = findViewById(R.id.layout_enable_gps);
	    gpsTracker = new GPSTracker(getApplicationContext(),new FQLocationListener(socketClient));

	    boolean trackingEnabled = gpsTracker.startTracker();
		if(!trackingEnabled){
			enableTrackingLayout.setVisibility(View.VISIBLE);
		}
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

	    LinearLayout mainLayout = (LinearLayout) findViewById(R.id.layout_game_activity_main);
        mainLayout.addView(new GameStatsView(this,currentTask),0,
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));

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
                            .setTabListener(this));
        }
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if(requestCode == GPSTracker.REQUEST_CODE){
				enableTrackingLayout.setVisibility(View.GONE);
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
		gpsTracker.startTracker();
	}

	/* Remove the locationlistener updates when Activity is paused */
	@Override
	protected void onPause() {
		super.onPause();
		gpsTracker.stopTracker();
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

    /**
     * A {@link android.support.v13.app.FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private ChatFragment chatFragment;
        private CurrentTaskFragment currentTaskFragment;
        private GameInfoFragment gameInfoFragment;
        private MapFragment mapFragment;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            chatFragment = new ChatFragment("LOLCHAT");
            currentTaskFragment = new CurrentTaskFragment(gameID, currentTask,socketClient);
            gameInfoFragment = new GameInfoFragment("GAMEINFO");
            mapFragment = new MapFragment("MAP");
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
