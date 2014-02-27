package com.newresources.funkyquest.activities;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.newresources.funkyquest.FQWebSocketClient;
import com.newresources.funkyquest.R;

/**
 * Created by BigBlackBug on 2/1/14.
 */
public class ChatFragment extends Fragment {

	private final FQWebSocketClient socketClient;

	public ChatFragment(FQWebSocketClient socketClient) {
		this.socketClient = socketClient;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.dummy_fragment, container, false);
		return rootView;
	}

}
