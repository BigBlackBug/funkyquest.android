package com.funkyquest.app.api;

import com.funkyquest.app.exceptions.FunkyQuestException;
import org.apache.http.StatusLine;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;

/**
 * Created by BigBlackBug on 1/26/14.
 */
public class FQApi {
    private final int serverPort;
    private final String serverAddress;
    private RestService restService;

    public FQApi(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        HttpContext httpContext = new BasicHttpContext();
        httpContext.setAttribute(ClientContext.COOKIE_STORE, new BasicCookieStore());
        this.restService = new RestService(httpContext, new DefaultHttpClient());
    }

    public static class LoginResult {
        private final long userID;
        private final FQServiceAPI fqServiceApi;

        public long getUserID() {
            return userID;
        }

        public FQServiceAPI getFqServiceApi() {
            return fqServiceApi;
        }

        public LoginResult(long userID, FQServiceAPI fqServiceApi) {
            this.userID = userID;
            this.fqServiceApi = fqServiceApi;
        }
    }

    private static FQServiceAPI fqServiceAPI = null;

    public static FQServiceAPI getFqServiceAPI() {
        return fqServiceAPI;
    }

    public LoginResult login(String email, String password) {
        JSONObject json = new JSONObject();
        try {
            json.put("password", password);
            json.put("email", email);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            URI uri = FQApiActions.LOGIN.createURI(serverAddress, serverPort);
            RestService.Response response = restService.sendPost(uri, json);
            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() == 200) {
                this.fqServiceAPI = new FQServiceAPI(restService, serverAddress, serverPort);
                return new LoginResult(Long.parseLong(response.getRawResponse()), fqServiceAPI);
            } else {
                throw new FunkyQuestException(
                        "Внезапная ошибка сервера: " +
                                statusLine.getStatusCode());
            }
        } catch (IOException e) {
            throw new FunkyQuestException("Ошибка выполнения запроса", e);
        }
    }

}
