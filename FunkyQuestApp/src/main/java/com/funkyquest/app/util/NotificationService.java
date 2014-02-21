package com.funkyquest.app.util;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import com.funkyquest.app.R;
import com.funkyquest.app.dto.util.EventType;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by BigBlackBug on 2/20/14.
 */
public class NotificationService {

	private final Context context;

	private final Random random = new Random();

	private final NotificationManager notificationManager;

	private final Map<EventType, Integer> notificationMap = new HashMap<EventType, Integer>();

	public NotificationService(Context context) {
		this.context = context;
		notificationManager =
				(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
	}

	public void showNotification(String title, String text, boolean isOngoing,
	                             EventType eventType) {
		NotificationCompat.Builder mBuilder =
				new NotificationCompat.Builder(context)
						.setSmallIcon(R.drawable.notification_icon)
						.setContentTitle(title)
						.setContentText(text).setOngoing(isOngoing).setAutoCancel(!isOngoing);
		Integer id = notificationMap.get(eventType);
		if (id == null) {
			id = random.nextInt();
			notificationMap.put(eventType, id);
		}
		notificationManager.notify(id, mBuilder.build());
	}

	public void closeNotification(EventType eventType) {
		Integer notificationID = notificationMap.get(eventType);
		if (notificationID != null) {
			notificationManager.cancel(notificationID);
		}
	}
}
