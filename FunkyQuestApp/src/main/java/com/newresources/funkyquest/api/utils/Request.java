package com.newresources.funkyquest.api.utils;

import java.net.URI;

public class Request<T> {
    private final URI uri;
    private final T parameters;

    public URI getUri() {
        return uri;
    }

    public T getRequestData() {
        return parameters;
    }

    public Request(URI uri, T parameters) {
        this.uri = uri;
        this.parameters = parameters;
    }
}