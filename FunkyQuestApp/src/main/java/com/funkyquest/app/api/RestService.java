package com.funkyquest.app.api;

import com.funkyquest.app.api.utils.GetRequest;
import com.funkyquest.app.api.utils.Response;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class RestService {

    public static final String APPLICATION_JSON = "application/json";

    private final HttpContext httpContext;
    private final HttpClient httpClient;

    public RestService(HttpContext httpContext, HttpClient httpClient) {
        this.httpContext = httpContext;
        this.httpClient = httpClient;
    }

    public HttpContext getHttpContext() {
        return httpContext;
    }

    public Response sendPost(URI uri, JSONObject body)
            throws IOException {
        HttpPost request = new HttpPost(uri);
        StringEntity entity = null;
        try {
            entity = new StringEntity(body.toString(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        request.setEntity(entity);
        request.addHeader("Content-Type", APPLICATION_JSON);
        request.addHeader("Accept", APPLICATION_JSON);
        HttpResponse response = httpClient.execute(request, httpContext);
        StatusLine statusLine = response.getStatusLine();
        ByteArrayOutputStream baos = new ByteArrayOutputStream(
                (int) response.getEntity().getContentLength());
        response.getEntity().writeTo(baos);
        return new Response(statusLine, new String(baos.toByteArray()));
    }


    public Response get(GetRequest requestData)
            throws IOException {
        String urlString = requestData.getUri().toString();
        if (!urlString.endsWith("?")) {
            urlString += "?";
        }
        List<NameValuePair> paramList = new ArrayList<NameValuePair>();
        for (Map.Entry<String, String> entry : requestData.getParameters().entrySet()) {
            paramList.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        urlString += URLEncodedUtils.format(paramList, "UTF-8");
        HttpGet request = new HttpGet(urlString);
        request.addHeader("Content-Type", APPLICATION_JSON);
        request.addHeader("Accept", APPLICATION_JSON);
        HttpResponse response = httpClient.execute(request, httpContext);
        StatusLine statusLine = response.getStatusLine();
        ByteArrayOutputStream baos = new ByteArrayOutputStream(
                (int) response.getEntity().getContentLength());
        response.getEntity().writeTo(baos);
        return new Response(statusLine, new String(baos.toByteArray()));
    }

}