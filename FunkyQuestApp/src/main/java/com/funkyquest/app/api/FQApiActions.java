package com.funkyquest.app.api;

import com.funkyquest.app.exceptions.FunkyQuestException;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bigblackbug on 1/28/14.
 */
public enum FQApiActions {
	CONNECT_TO_WEBSOCKET("/subscribe") {
		@Override
		public URI createURI(String host, int port, Object... userID) {
			try{
				if (userID.length != 1) {
					throw new RuntimeException("invalid argument");
				}
				List<NameValuePair> paramList = new ArrayList<NameValuePair>();
				paramList.add(new BasicNameValuePair("userId", String.valueOf(userID[0])));
				String paramString="?"+ URLEncodedUtils.format(paramList, "UTF-8");
				return URI.create("ws://"+host+":"+port+String.format(getPath())+paramString);
			}catch(IllegalArgumentException ex){
				throw new FunkyQuestException("URI error. Will never be thrown",ex);
			}
		}
	},
	LOGIN("/api/login"),
    ACTIVE_GAME("/api/activeGames"),
    CURRENT_TASK("/api/activeGames/%s/task"),
    TAKE_NEXT_HINT("/api/activeGames/%s/tasks/%s/nextHint"),
    POST_ANSWER("/api/activeGames/%s/tasks/%s/playerAnswers"),
	UPLOAD_FILE("/api/images");
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
