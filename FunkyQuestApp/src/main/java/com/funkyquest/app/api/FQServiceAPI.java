package com.funkyquest.app.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.funkyquest.app.api.utils.AsyncGetRequest;
import com.funkyquest.app.api.utils.AsyncRequest;
import com.funkyquest.app.dto.*;
import com.funkyquest.app.exceptions.UserNotLoggedInException;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.net.URI;
import java.util.Collections;
import java.util.Map;

public class FQServiceAPI {
    private static final Map<String, String> EMPTY_MAP = Collections.emptyMap();
    private final AsyncRestService restService;
    private final int serverPort;
    private final String serverAddress;
    private boolean isLoggedIn = false;

    public FQServiceAPI(String serverAddress, int serverPort) {
        HttpContext httpContext = new BasicHttpContext();
        httpContext.setAttribute(ClientContext.COOKIE_STORE, new BasicCookieStore());
        this.restService = new AsyncRestService(new DefaultHttpClient(), httpContext);
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    public void getCurrentGame(NetworkCallback<GameDTO> callback) throws UserNotLoggedInException {
        checkLoginStatus();
        URI uri = FQApiActions.ACTIVE_GAME.createURI(serverAddress, serverPort);
        restService.get(new AsyncGetRequest<GameDTO>(uri, EMPTY_MAP, new TypeReference<GameDTO>() {
        }, callback));
    }

    public void getCurrentTask(long gameID, NetworkCallback<InGameTaskDTO> callback) throws UserNotLoggedInException {
        checkLoginStatus();
        URI uri = FQApiActions.CURRENT_TASK.createURI(serverAddress, serverPort, gameID);
        restService.get(new AsyncGetRequest<InGameTaskDTO>(uri, EMPTY_MAP, new TypeReference<InGameTaskDTO>() {
        }, callback));
    }

    public void postAnswer(long gameID, long taskID, AnswerDTO answer, NetworkCallback<AnswerIdDTO> callback) throws UserNotLoggedInException {
        checkLoginStatus();
        URI uri = FQApiActions.POST_ANSWER.createURI(serverAddress, serverPort, gameID, taskID);
        restService.post(new AsyncRequest<AnswerDTO, AnswerIdDTO>(uri, answer, new TypeReference<AnswerIdDTO>() {
        }, callback));
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
                credentials, new TypeReference<Long>() {
        }, loginCallback));
    }

    //TODO какого хрена тут пост ваще?
    public void getNextHint(long gameID, long taskID, NetworkCallback<HintDTO> callback) throws UserNotLoggedInException {
        checkLoginStatus();
        URI uri = FQApiActions.TAKE_NEXT_HINT.createURI(serverAddress, serverPort, gameID, taskID);
        restService.post(new AsyncRequest<Void, HintDTO>(uri, null, new TypeReference<HintDTO>() {
        }, callback));
    }
}