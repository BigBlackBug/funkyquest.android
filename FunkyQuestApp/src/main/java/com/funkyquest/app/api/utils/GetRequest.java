package com.funkyquest.app.api.utils;

import java.net.URI;
import java.util.Map;

public class GetRequest {
    private final URI uri;
    private final Map<String, String> parameters;

    public URI getUri() {
        return uri;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public GetRequest(URI uri, Map<String, String> parameters) {

        this.uri = uri;
        this.parameters = parameters;
    }
}