package com.funkyquest.app.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.funkyquest.app.api.progress.WriteListener;
import com.funkyquest.app.api.utils.AsyncGetRequest;
import com.funkyquest.app.api.utils.AsyncRequest;
import com.funkyquest.app.dto.*;
import com.funkyquest.app.dto.util.Subscription;
import com.funkyquest.app.exceptions.UserNotLoggedInException;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.net.URI;
import java.util.*;

public class FQServiceAPI {

	private static final Map<String, String> EMPTY_MAP = Collections.emptyMap();

    private static final int CONNECTION_TIMEOUT = 5000;

    private static final int WAIT_RESPONSE_TIMEOUT = 5000;

    private final AsyncRestService restService;

	private final int serverPort;

	private final String serverAddress;

	private boolean isLoggedIn = false;

	public FQServiceAPI(String serverAddress, int serverPort) {
		HttpContext httpContext = new BasicHttpContext();
		httpContext.setAttribute(ClientContext.COOKIE_STORE, new BasicCookieStore());
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpParams httpParameters = httpClient.getParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters, CONNECTION_TIMEOUT);
        HttpConnectionParams.setSoTimeout(httpParameters, WAIT_RESPONSE_TIMEOUT);
        HttpConnectionParams.setTcpNoDelay(httpParameters, true);
        this.restService = new AsyncRestService(httpClient, httpContext);
		this.serverAddress = serverAddress;
		this.serverPort = serverPort;
	}

	public void getCurrentGame(NetworkCallback<GameDTO> callback) throws UserNotLoggedInException {
		checkLoginStatus();
		URI uri = FQApiActions.ACTIVE_GAME.createURI(serverAddress, serverPort);
		restService.get(new AsyncGetRequest<GameDTO>(uri, EMPTY_MAP, new TypeReference<GameDTO>() {
		}, callback));
	}

	public void getGameLocation(long locationID, NetworkCallback<LocationDTO> callback) throws UserNotLoggedInException {
		checkLoginStatus();
		URI uri = FQApiActions.GET_LOCATION.createURI(serverAddress, serverPort, locationID);
		restService.get(new AsyncGetRequest<LocationDTO>(uri, EMPTY_MAP, new TypeReference<LocationDTO>() {
		}, callback));
	}

	public void getTaskLocation(long locationID, NetworkCallback<LocationDTO> callback) throws UserNotLoggedInException {
		checkLoginStatus();
		URI uri = FQApiActions.GET_LOCATION.createURI(serverAddress, serverPort, locationID);
		restService.get(new AsyncGetRequest<LocationDTO>(uri, EMPTY_MAP, new TypeReference<LocationDTO>() {
		}, callback));
	}

	public void getCurrentTask(long gameID, NetworkCallback<InGameTaskDTO> callback)
			throws UserNotLoggedInException {
		checkLoginStatus();
		URI uri = FQApiActions.CURRENT_TASK.createURI(serverAddress, serverPort, gameID);
		restService.get(new AsyncGetRequest<InGameTaskDTO>(
				uri, EMPTY_MAP, new TypeReference<InGameTaskDTO>() {}, callback));
	}

	public void postAnswer(long gameID, long taskID, PlayerAnswerDTO answer,
	                       NetworkCallback<AnswerIdDTO> callback) throws UserNotLoggedInException {
		checkLoginStatus();
		URI uri = FQApiActions.POST_ANSWER.createURI(serverAddress, serverPort, gameID, taskID);
		restService.post(new AsyncRequest<PlayerAnswerDTO, AnswerIdDTO>(
				uri, answer,new TypeReference<AnswerIdDTO>() {}, callback));
	}

	public void addSubscription(UUID connectionID, Subscription subscription,
	                       NetworkCallback<Void> callback) throws UserNotLoggedInException {
		checkLoginStatus();
		URI uri = FQApiActions.ADD_SUBSCRIPTION.createURI(serverAddress, serverPort, connectionID, subscription);
		restService.get(new AsyncGetRequest<Void>(uri, toMap(subscription,connectionID),new TypeReference<Void>(){},callback));
	}

	public void addSubscriptions(UUID connectionID, List<Subscription> subscriptions,
	                            NetworkCallback<Void> callback) throws UserNotLoggedInException {
		checkLoginStatus();
		URI uri = FQApiActions.ADD_SUBSCRIPTIONS.createURI(serverAddress, serverPort, connectionID);
		restService.post(new AsyncRequest<List<Subscription>, Void>(uri, subscriptions,
		                                                            new TypeReference<Void>() {
		                                                            }, callback));
	}

	private Map<String, String> toMap(Subscription subscription,UUID connectionID) {
		Map<String, String> map = new HashMap<String,String>();
		Long gameID = subscription.getGameID();
		Long teamID = subscription.getTeamID();
		Long userID = subscription.getUserID();
		map.put("gameId", String.valueOf(gameID));
		map.put("teamId", String.valueOf(teamID));
		map.put("userId", String.valueOf(userID));
		map.put("connectionId", connectionID.toString());
		return map;
	}

	public void uploadFile(String pathToFile,NetworkCallback<MediaObjectDTO> callback,
	                       WriteListener writeListener) {
		checkLoginStatus();
		URI uri = FQApiActions.UPLOAD_FILE.createURI(serverAddress, serverPort);
		restService.upload(new AsyncRequest<String, MediaObjectDTO>(
				uri, pathToFile,new TypeReference<MediaObjectDTO>() {}, callback),writeListener);
	}

	private void checkLoginStatus() throws UserNotLoggedInException {
		if (!isLoggedIn) {
			throw new UserNotLoggedInException();
		}
	}

	public void login(final NetworkCallback<Long> callback, LoginCredentials credentials) {
		URI uri = FQApiActions.LOGIN.createURI(serverAddress, serverPort);
		NetworkCallback<Long> loginCallback = new NetworkCallback<Long>() {

			@Override
			public void onSuccess(Long arg) {
				isLoggedIn = true;
				callback.onSuccess(arg);
			}

			@Override
			public void onException(Exception ex) {
				callback.onException(ex);
			}

			@Override
			public void onApplicationError(int errorCode) {
				callback.onApplicationError(errorCode);
			}

			@Override
			public void onPostExecute() {
				callback.onPostExecute();

			}
		};
		restService.post(new AsyncRequest<LoginCredentials, Long>(uri,
		                                                          credentials,
		                                                          new TypeReference<Long>() {
		                                                          }, loginCallback));
	}

	public void getNextHint(long gameID, long taskID, NetworkCallback<HintDTO> callback)
			throws UserNotLoggedInException {
		checkLoginStatus();
		URI uri = FQApiActions.TAKE_NEXT_HINT.createURI(serverAddress, serverPort, gameID, taskID);
		restService.post(new AsyncRequest<Void, HintDTO>(uri, null, new TypeReference<HintDTO>() {
		}, callback));
	}
}