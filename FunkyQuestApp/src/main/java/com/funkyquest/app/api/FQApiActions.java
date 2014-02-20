package com.funkyquest.app.api;

import com.funkyquest.app.dto.util.Subscription;
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
import java.util.UUID;

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
				paramList.add(new BasicNameValuePair("isHost",String.valueOf(false)));
				String paramString="?"+ URLEncodedUtils.format(paramList, "UTF-8");
				return URI.create("ws://"+host+":"+port+String.format(getPath())+paramString);
			}catch(IllegalArgumentException ex){
				throw new FunkyQuestException("URI error. Will never be thrown",ex);
			}
		}
	},
	ADD_SUBSCRIPTION("/addSubscription%s"){
		@Override
		public URI createURI(String host, int port, Object... args) {
			if (args.length != 2) {
				throw new RuntimeException("invalid arguments");
			}
			UUID connectionId = (UUID) args[0];
			Subscription subscription = (Subscription) args[1];
			List<NameValuePair> paramList = new ArrayList<NameValuePair>();
			Long gameID = subscription.getGameID();
			Long teamID = subscription.getTeamID();
			Long userID = subscription.getUserID();
			paramList.add(new BasicNameValuePair("gameId", String.valueOf(gameID)));
			paramList.add(new BasicNameValuePair("teamId",String.valueOf(teamID)));
			paramList.add(new BasicNameValuePair("userId",String.valueOf(userID)));
			paramList.add(new BasicNameValuePair("connectionId",connectionId.toString()));
			String paramString="?"+ URLEncodedUtils.format(paramList, "UTF-8");
			return super.createURI(host, port, paramString);
		}
	},
	GET_GAME_BY_ID("/api/games/%s"),
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
