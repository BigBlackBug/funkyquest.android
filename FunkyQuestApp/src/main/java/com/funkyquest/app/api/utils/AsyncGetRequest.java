package com.funkyquest.app.api.utils;

import com.fasterxml.jackson.core.type.TypeReference;

import java.net.URI;
import java.util.Map;

public class AsyncGetRequest<T> {
    private final URI uri;
    private final Map<String, String> parameters;
    private final TypeReference<T> responseType;
    private final NetworkCallback<T> callback;

    public AsyncGetRequest(URI uri, Map<String, String> parameters,
                           TypeReference<T> responseType, NetworkCallback<T> callback) {
        this.uri = uri;
        this.parameters = parameters;
        this.responseType = responseType;
        this.callback = callback;
    }

    public URI getUri() {
        return uri;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public TypeReference<T> getResponseType() {
        return responseType;
    }

    public NetworkCallback<T> getCallback() {
        return callback;
    }

    public GetRequest toGetRequest() {
        return new GetRequest(uri, parameters);
    }
}