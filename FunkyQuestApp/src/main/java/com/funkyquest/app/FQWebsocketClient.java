package com.funkyquest.app;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.funkyquest.app.dto.PlayerLocationDTO;
import com.funkyquest.app.dto.util.EventType;
import com.funkyquest.app.util.websockets.WebSocketClient;

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

	public <T> void addMessageListener(EventType eventType,
	                                   WebSocketClientListener.FQMessageListener<T> listener) {
		socketListener.addMessageListener(eventType, listener);
	}

	public void connect() {
		socketClient.connect();
	}

	public void sendLocation(PlayerLocationDTO location) {
		try {
			String json = objectMapper.writeValueAsString(location);
			socketClient.send(json);
		} catch (JsonProcessingException e) {
			//never happens
		}
	}
}
