package com.funkyquest.app.api;

import android.os.AsyncTask;
import com.fasterxml.jackson.core.type.TypeReference;
import com.funkyquest.app.api.utils.AsyncGetRequest;
import com.funkyquest.app.api.utils.NetworkCallback;
import com.funkyquest.app.api.utils.Response;
import org.apache.http.client.HttpClient;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;

/**
 * Created by bigblackbug on 1/31/14.
 */
public class AsyncRestService {
    public static final int HTTP_OK = 200;
    private final RestService restService;

    public AsyncRestService(HttpClient httpClient, HttpContext httpContext) {
        this.restService = new RestService(httpContext, httpClient);
    }

    public AsyncRestService(RestService restService) {
        this.restService = restService;
    }

    @SuppressWarnings("unchecked")
    public <T> void get(AsyncGetRequest<T> request) {
        new AsyncGet<T>(restService).execute(request);
    }

    private static final class AsyncGet<T> extends AsyncTask<AsyncGetRequest<T>, Void, Response> {
        private final RestService restService;

        private Exception exception;
        private NetworkCallback<T> callback;
        private TypeReference<T> responseType;

        private AsyncGet(RestService restService) {
            this.restService = restService;
        }

        @Override
        protected Response doInBackground(AsyncGetRequest<T>... params) {
            AsyncGetRequest<T> request = params[0];
            this.callback = request.getCallback();
            this.responseType = request.getResponseType();
            try {
                return restService.get(request.toGetRequest());
            } catch (Exception e) {
                exception = e;
                return null;
            }
        }

        @Override
        protected void onPostExecute(Response response) {
            T responseValue = null;
            try {
                responseValue = response.convertTo(responseType);
            } catch (IOException e) {
                this.exception = e;
            }
            if (exception == null) {
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HTTP_OK) {
                    callback.onSuccess(responseValue);
                } else {
                    callback.onApplicationError(statusCode);
                }
            } else {
                callback.onException(exception);
            }
        }
    }
}
