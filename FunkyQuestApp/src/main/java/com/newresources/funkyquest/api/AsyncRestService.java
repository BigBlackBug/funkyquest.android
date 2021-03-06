package com.newresources.funkyquest.api;

import android.os.AsyncTask;
import com.fasterxml.jackson.core.type.TypeReference;
import com.newresources.funkyquest.api.progress.WriteListener;
import com.newresources.funkyquest.api.utils.AsyncRequest;
import com.newresources.funkyquest.api.utils.Request;
import com.newresources.funkyquest.api.utils.Response;
import org.apache.http.client.HttpClient;
import org.apache.http.protocol.HttpContext;

import java.util.Map;

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
    public <T> void get(AsyncRequest<Map<String, String>, T> request) {
        new AsyncGetRequestTask<T>(restService).execute(request);
    }

    @SuppressWarnings("unchecked")
    public <Req, Resp> void post(AsyncRequest<Req, Resp> request) {
        new AsyncPostRequestTask<Req, Resp>(restService).execute(request);
    }

    @SuppressWarnings("unchecked")
    public <Resp> void upload(AsyncRequest<String, Resp> request,WriteListener writeListener) {
        new AsyncUploadRequestTask<Resp>(restService,writeListener).execute(request);
    }

    private static abstract class AsyncRequestTask<
            Req, Resp> extends AsyncTask<AsyncRequest<Req, Resp>, Void, Resp> {
        protected final RestService restService;
        private Exception exception;
        private NetworkCallback<Resp> callback;
        private TypeReference<Resp> responseType;
        private int statusCode;

        private AsyncRequestTask(RestService restService) {
            this.restService = restService;
        }

        @Override
        protected Resp doInBackground(AsyncRequest<Req, Resp>... params) {
            AsyncRequest<Req, Resp> request = params[0];
            this.callback = request.getCallback();
            this.responseType = request.getResponseType();
            try {
                Response response = executeMethod(request.toRequest());
                this.statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HTTP_OK && !response.getRawResponse().isEmpty()) {
                    return response.convertTo(responseType);
                }
                return null;
            } catch (Exception e) {
                exception = e;
                return null;
            }
        }

        protected abstract Response executeMethod(Request<Req> request) throws Exception;

        @Override
        protected void onPostExecute(Resp response) {
            callback.onPostExecute();
            if (exception != null) {
                callback.onException(exception);
            }else{
                if(statusCode == HTTP_OK){
                    callback.onSuccess(response);
                }else{
                    callback.onApplicationError(statusCode);
                }
            }

        }
    }

    private static final class AsyncPostRequestTask<Req, T> extends AsyncRequestTask<Req, T> {
        private AsyncPostRequestTask(RestService restService) {
            super(restService);
        }

        @Override
        protected Response executeMethod(Request<Req> request) throws Exception {
            return restService.post(request);
        }
    }

    private static final class AsyncUploadRequestTask<T> extends AsyncRequestTask<String, T> {

        private final WriteListener writeListener;

        private AsyncUploadRequestTask(RestService restService, WriteListener writeListener) {
            super(restService);
            this.writeListener = writeListener;
        }

        @Override
        protected Response executeMethod(Request<String> request) throws Exception {
            return restService.upload(request, writeListener);
        }
    }

    private static final class AsyncGetRequestTask<T> extends AsyncRequestTask<Map<String, String>, T> {

        private AsyncGetRequestTask(RestService restService) {
            super(restService);
        }

        @Override
        protected Response executeMethod(Request<Map<String, String>> request) throws Exception {
            return restService.get(request);
        }
    }

}
