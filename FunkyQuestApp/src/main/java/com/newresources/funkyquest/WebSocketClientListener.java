package com.newresources.funkyquest;

import android.util.Log;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newresources.funkyquest.dto.*;
import com.newresources.funkyquest.dto.util.EventType;
import com.newresources.funkyquest.dto.util.MessageBody;
import com.newresources.funkyquest.util.MapOLists;
import com.newresources.funkyquest.util.websockets.WebSocketClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by BigBlackBug on 2/14/14.
 */
public class WebSocketClientListener implements WebSocketClient.Listener {

	private final MapOLists<EventType, FQMessageListener> listeners =
			new MapOLists<EventType, FQMessageListener>();
	private final Map<EventType,Class<?>> messageTypes = new HashMap<EventType, Class<?>>();

	private FQMessageListener<UUID> subscriptionListener;

	private final ObjectMapper objectMapper = new ObjectMapper();

	private SocketLifeCycleListener lifeCycleListener;

	public <T> void addMessageListener(EventType eventType, FQMessageListener<T> listener) {
		listeners.put(eventType, listener);
	}

	public void setSubscriptionListener(FQMessageListener<UUID> subscriptionListener) {
		this.subscriptionListener = subscriptionListener;
	}

	public void setLifeCycleListener(SocketLifeCycleListener lifeCycleListener) {
		this.lifeCycleListener = lifeCycleListener;
	}

	public WebSocketClientListener() {
		messageTypes.put(EventType.GAME_STATUS_CHANGED, GameStatusDTO.class);
		messageTypes.put(EventType.GAME_EDITED, GameDTO.class);
		messageTypes.put(EventType.TEAM_CREATED, TeamDTO.class);
		messageTypes.put(EventType.TEAM_CHANGED, TeamDTO.class);
		messageTypes.put(EventType.TEAM_DELETED, AbstractDTO.class);
		messageTypes.put(EventType.HINT_REQUESTED, HintDTO.class);
		messageTypes.put(EventType.ANSWER_ACCEPTED, InGameTaskDTO.class);
		messageTypes.put(EventType.ANSWER_REJECTED, Void.class);
		messageTypes.put(EventType.ANSWER_POSTED, Void.class);
		messageTypes.put(EventType.LOCATION_CHANGED, PlayerLocationDTO.class);
	}

	@Override
	public void onConnect() {
		if (lifeCycleListener != null) {
			lifeCycleListener.onConnect();
		}
	}

	private static final class ConnectionIDWrapper{
		private String connectionID;

		private ConnectionIDWrapper() {
		}

		@SuppressWarnings("unused")
		public String getConnectionID() {
			return connectionID;
		}

		private ConnectionIDWrapper(UUID connectionID) {
			this.connectionID = connectionID.toString();
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void onMessage(String message) {
		Log.i("SOCKET","hey, I got a message here "+message);
		MessageBody messageBody;
		try {
			messageBody = objectMapper.readValue(message, MessageBody.class);
		} catch (IOException e) {
			try {
				ConnectionIDWrapper connectionIDWrapper =
						objectMapper.readValue(message, ConnectionIDWrapper.class);
				subscriptionListener.onMessage(UUID.fromString(connectionIDWrapper.getConnectionID()));
				return;
			} catch (IOException ex) {
				throw new RuntimeException("NEVER OCCURS LOL", ex);
				//never happens
			}
		}
		Log.i("SOCKET","hey, serialization success "+messageBody);
//        Set<EventType> voidTypes = new HashSet<EventType>() {{
//            add(EventType.GAME_FINISHED);
//            add(EventType.GAME_CANCELLED);
//            add(EventType.ANSWER_ACCEPTED);
//            add(EventType.ANSWER_REJECTED);
//            add(EventType.ANSWER_POSTED);
//            add(EventType.BALANCE_RECHARGED);
//        }};
		EventType eventType = messageBody.getEventType();

		List<FQMessageListener> listenerList = listeners.get(eventType);
		Object body = null;
		Class<?> aClass = messageTypes.get(eventType);
		if(!aClass.equals(Void.TYPE)){
			body = objectMapper.convertValue(messageBody.getBody(),aClass);
		}
		for (FQMessageListener listener : listenerList) {
			try{
				listener.onMessage(body);
			}catch (Exception ex){
				Log.e("tAG","MSG",ex);
			}
		}
//        if (eventType == EventType.GAME_STATUS_CHANGED) {
//            GameStatusDTO dto = (GameStatusDTO) messageBody.getBody();
//        } else if (eventType == EventType.GAME_EDITED) {
//            GameDTO dto = (GameDTO) messageBody.getBody();
//        } else if (eventType == EventType.TEAM_CREATED) {
//            //void
//            TeamDTO dto = (TeamDTO) messageBody.getBody();
//        }else if(eventType == EventType.TEAM_CHANGED){
//            TeamDTO dto = (TeamDTO) messageBody.getBody();
//        }else if(eventType == EventType.TEAM_DELETED){
//            AbstractDTO dto = (AbstractDTO) messageBody.getBody();
//        }else if(eventType == EventType.HINT_REQUESTED){
//            HintDTO dto = (HintDTO) messageBody.getBody();
//        }else if(voidTypes.contains(eventType)){
//            listeners.get(eventType)
//        }

//        listeners.get(EventType.ANSWER_ACCEPTED).get(0).onMessage(new WebSocketClient(null, null, null));

	}

	@Override
	public void onMessage(byte[] data) {
	}

	@Override
	public void onDisconnect(Reason reason, String... message) {
		if (lifeCycleListener != null) {
			lifeCycleListener.onDisconnect(reason, message);
		}
	}

	@Override
	public void onError(ErrorType errorType, Exception exception) {
		if (lifeCycleListener != null) {
			lifeCycleListener.onError(errorType, exception);
		}
	}

	public static interface FQMessageListener<T> {

		public void onMessage(T message);
	}

	public static interface SocketLifeCycleListener {

		public void onError(ErrorType errorType, Exception exception);

		public void onDisconnect(Reason reason, String... message);

		public void onConnect();
	}
}
