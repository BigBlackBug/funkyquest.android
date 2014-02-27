package com.newresources.funkyquest;

import android.util.Log;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newresources.funkyquest.dto.PlayerLocationDTO;
import com.newresources.funkyquest.dto.util.EventType;
import com.newresources.funkyquest.util.FQObjectMapper;
import com.newresources.funkyquest.util.websockets.WebSocketClient;

import java.util.UUID;

/**
 * Created by BigBlackBug on 2/15/14.
 */
public class FQWebSocketClient {

	private final WebSocketClient socketClient;

	private final WebSocketClientListener socketListener;

	private final ObjectMapper objectMapper = new FQObjectMapper();

	public FQWebSocketClient(WebSocketClient socketClient,
	                         WebSocketClientListener socketListener) {
		this.socketClient = socketClient;
		this.socketListener = socketListener;
	}

	public void setSocketLifecycleListener(WebSocketClientListener.SocketLifeCycleListener lifeCycleListener) {
		socketListener.setLifeCycleListener(lifeCycleListener);
	}

	public void setSubscriptionListener(
			com.newresources.funkyquest.WebSocketClientListener.FQMessageListener<UUID> subscriptionListener) {
		socketListener.setSubscriptionListener(subscriptionListener);
	}

	public <T> void addMessageListener(EventType eventType,
	                                   com.newresources.funkyquest.WebSocketClientListener.FQMessageListener<T> listener) {
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

	public void disconnect() {
		socketClient.disconnect();
	}
}
