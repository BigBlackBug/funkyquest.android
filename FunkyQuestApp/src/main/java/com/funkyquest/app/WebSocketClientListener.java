package com.funkyquest.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.funkyquest.app.dto.util.EventType;
import com.funkyquest.app.dto.util.MessageBody;
import com.funkyquest.app.util.MapOLists;
import com.funkyquest.app.util.websockets.WebSocketClient;

import java.io.IOException;
import java.util.List;

/**
 * Created by BigBlackBug on 2/14/14.
 */
public class WebSocketClientListener implements WebSocketClient.Listener {

	private final MapOLists<EventType, FQMessageListener> listeners =
			new MapOLists<EventType, FQMessageListener>();

	private final ObjectMapper objectMapper = new ObjectMapper();

	private SocketLifeCycleListener lifeCycleListener;

	public <T> void addMessageListener(EventType eventType, FQMessageListener<T> listener) {
		listeners.put(eventType, listener);
	}

	public void setLifeCycleListener(SocketLifeCycleListener lifeCycleListener) {
		this.lifeCycleListener = lifeCycleListener;
	}

	@Override
	public void onConnect() {
		if (lifeCycleListener != null) {
			lifeCycleListener.onConnect();
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void onMessage(String message) {
		MessageBody messageBody;
		try {
			messageBody = objectMapper.readValue(message, MessageBody.class);
		} catch (IOException e) {
			return;       //never happens
		}
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
		for (FQMessageListener listener : listenerList) {
			listener.onMessage(messageBody.getBody());
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
