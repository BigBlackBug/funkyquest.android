package com.funkyquest.app.activities;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.funkyquest.app.FQWebSocketClient;
import com.funkyquest.app.R;

/**
 * Created by BigBlackBug on 2/1/14.
 */
public class ChatFragment extends Fragment {

	private final FQWebSocketClient socketClient;

	private String text;

	public ChatFragment(String text, FQWebSocketClient socketClient) {
		this.text = text;
		this
				.socketClient = socketClient;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.dummy_fragment, container, false);
		TextView dummyTextView = (TextView) rootView.findViewById(R.id.section_label);
		Button b = (Button) rootView.findViewById(R.id.dummy_button);
		dummyTextView.setText(text);
		return rootView;
	}

}
