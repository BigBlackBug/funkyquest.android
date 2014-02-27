package com.newresources.funkyquest.api.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.newresources.funkyquest.api.NetworkCallback;

import java.net.URI;

public class AsyncRequest<Req,Resp> {
    private final Request<Req> request;
    private final TypeReference<Resp> responseType;
    private final NetworkCallback<Resp> callback;

    public AsyncRequest(URI uri, Req parameters,
                        TypeReference<Resp> responseType, NetworkCallback<Resp> callback) {
        this.request = new Request<Req>(uri, parameters);
        this.responseType = responseType;
        this.callback = callback;
    }

    public URI getUri() {
        return request.getUri();
    }

    public Req getParameters() {
        return request.getRequestData();
    }

    public TypeReference<Resp> getResponseType() {
        return responseType;
    }

    public NetworkCallback<Resp> getCallback() {
        return callback;
    }

    public Request<Req> toRequest() {
        return request;
    }
}