package com.funkyquest.app.activities;

import android.app.Fragment;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
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
		b.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showNotification();
			}
		});
		dummyTextView.setText(text);
		return rootView;
	}

	public void showNotification(){
		NotificationCompat.Builder mBuilder =
				new NotificationCompat.Builder(getActivity())
						.setSmallIcon(R.drawable.notification_icon)
						.setContentTitle("My notification")
						.setContentText("Hello World!").setOngoing(true);
		NotificationManager mNotificationManager =
				(NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
		mNotificationManager.notify(123456, mBuilder.build());
	}
}
