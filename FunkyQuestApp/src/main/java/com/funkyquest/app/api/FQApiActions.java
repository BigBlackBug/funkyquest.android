package com.funkyquest.app.api;

import com.funkyquest.app.exceptions.FunkyQuestException;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Created by bigblackbug on 1/28/14.
 */
public enum FQApiActions {
    CONNECT_TO_WEBSOCKET(""/*TODO*/){
        @Override
        public URI createURI(String host, int port, Object... args) {
            return createURI("ws",host,port,args);
        }
    },
    LOGIN("/api/login"),
    ACTIVE_GAME("/api/activeGames"),
    CURRENT_TASK("/api/activeGames/%s/task"),
    TAKE_NEXT_HINT("/api/activeGames/%s/tasks/%s/nextHint"),
    POST_ANSWER("/api/activeGames/%s/tasks/%s/playerAnswers");
    private final String path;

    FQApiActions(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public URI createURI(String host, int port, Object... args){
        return createURI("http",host, port, args);
    }

    protected URI createURI(String protocol, String host, int port, Object... args) {
        try {
            return new URL(protocol, host, port, String.format(getPath(), args)).toURI();
        } catch (URISyntaxException e) {
            throw new FunkyQuestException("URI error. Will never be thrown",e);
        } catch (MalformedURLException e) {
            throw new FunkyQuestException("URI error. Will never be thrown",e);
        }
    }
}
