package com.funkyquest.app;

import android.util.Log;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.funkyquest.app.dto.PlayerLocationDTO;
import com.funkyquest.app.dto.util.EventType;
import com.funkyquest.app.util.websockets.WebSocketClient;

import java.util.UUID;

/**
 * Created by BigBlackBug on 2/15/14.
 */
public class FQWebSocketClient {

	private final WebSocketClient socketClient;

	private final WebSocketClientListener socketListener;

	private final ObjectMapper objectMapper = new ObjectMapper();

	public FQWebSocketClient(WebSocketClient socketClient,
	                         WebSocketClientListener socketListener) {
		this.socketClient = socketClient;
		this.socketListener = socketListener;
	}

	public void setSocketLifecycleListener(
			WebSocketClientListener.SocketLifeCycleListener lifeCycleListener) {
		socketListener.setLifeCycleListener(lifeCycleListener);
	}

	public void setSubscriptionListener(
			WebSocketClientListener.FQMessageListener<UUID> subscriptionListener) {
		socketListener.setSubscriptionListener(subscriptionListener);
	}

	public <T> void addMessageListener(EventType eventType,
	                                   WebSocketClientListener.FQMessageListener<T> listener) {
		socketListener.addMessageListener(eventType, listener);
	}

	public void connect() {
		socketClient.connect();
	}

	public void sendLocation(PlayerLocationDTO location) {
		try {
			Log.i("SocketClient","sending location");
			String json = objectMapper.writeValueAsString(location);
			socketClient.send(json);
		} catch (JsonProcessingException e) {
			//never happens
		}
	}

	public boolean isConnected() {
		return socketClient.isConnected();
	}
}
