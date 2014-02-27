package com.newresources.funkyquest.api.utils;

import android.os.SystemClock;
import android.util.Log;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newresources.funkyquest.util.FQObjectMapper;
import org.apache.http.StatusLine;

import java.io.IOException;

public class Response {

    private static final ObjectMapper mapper = new FQObjectMapper();
    private final StatusLine statusLine;
    private final String rawResponse;

    public Response(StatusLine statusLine, String rawResponse) {
        this.statusLine = statusLine;
        this.rawResponse = rawResponse;
    }

    public String getRawResponse() {
        return rawResponse;
    }

    public <T> T convertTo(TypeReference<T> type) throws IOException {
	    Log.i("Response", "Got response"+rawResponse);
	    Log.i("Response", "Starting deserialization to type "+type.getType());
	    long before = SystemClock.uptimeMillis();
	    T result = mapper.readValue(rawResponse, type);
	    Log.i("Response", "Deserializing response to type "+type.getType()+" took "+(SystemClock.uptimeMillis()-before)+"ms");
	    return result;
    }

    public StatusLine getStatusLine() {
        return statusLine;
    }
}