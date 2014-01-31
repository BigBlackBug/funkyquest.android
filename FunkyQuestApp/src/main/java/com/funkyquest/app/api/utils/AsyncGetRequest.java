package com.funkyquest.app.api.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.funkyquest.app.api.NetworkCallback;

import java.net.URI;
import java.util.Map;

public final class AsyncGetRequest<Resp> extends AsyncRequest<Map<String, String>, Resp> {

    public AsyncGetRequest(URI uri, Map<String, String> parameters, TypeReference<Resp> responseType, NetworkCallback<Resp> callback) {
        super(uri, parameters, responseType, callback);
    }
}