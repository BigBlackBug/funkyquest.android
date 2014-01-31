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
    LOGIN("/api/login"),
    ACTIVE_GAME("/api/activeGames");

    FQApiActions(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    private final String path;

    public URI createURI(String host, int port) {
        try {
            return new URL("http", host, port, getPath()).toURI();
        } catch (URISyntaxException e) {
            throw new FunkyQuestException("URI error. Will never be thrown");
        } catch (MalformedURLException e) {
            throw new FunkyQuestException("URI error. Will never be thrown");
        }
    }
}
