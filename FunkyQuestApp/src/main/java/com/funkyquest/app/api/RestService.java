package com.funkyquest.app.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.funkyquest.app.api.progress.MultipartEntityWithListener;
import com.funkyquest.app.api.progress.WriteListener;
import com.funkyquest.app.api.utils.Request;
import com.funkyquest.app.api.utils.Response;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class RestService {

    public static final String APPLICATION_JSON = "application/json";
    private final HttpContext httpContext;
    private final HttpClient httpClient;
    private final ObjectMapper mapper = new ObjectMapper();

    public RestService(HttpContext httpContext, HttpClient httpClient) {
        this.httpContext = httpContext;
        this.httpClient = httpClient;
    }

    public <T> Response post(Request<T> requestData)
            throws IOException {
        HttpPost request = new HttpPost(requestData.getUri());
        StringEntity entity = null;
        try {
            T reqData = requestData.getRequestData();
            String entityData;
            if (reqData == null) {
                entityData = "{}";
            } else {
                entityData = mapper.writeValueAsString(reqData);
            }
            entity = new StringEntity(entityData, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        request.setEntity(entity);
        request.addHeader("Content-Type", APPLICATION_JSON);
        request.addHeader("Accept", APPLICATION_JSON);
        HttpResponse response = httpClient.execute(request, httpContext);
        String responseString = responseToString(response);
        return new Response(response.getStatusLine(), responseString);
    }

	public Response upload(Request<String> requestData, WriteListener writeListener)
			throws IOException {
		HttpPost request = new HttpPost(requestData.getUri());

		File file = new File(requestData.getRequestData());
		MultipartEntityWithListener entity = new MultipartEntityWithListener(writeListener);
		entity.addPart("file", new FileBody(file));
		request.setEntity(entity);
		HttpResponse response = httpClient.execute(request, httpContext);
		String responseString = responseToString(response);
		return new Response(response.getStatusLine(), responseString);
	}

    public Response get(Request<Map<String, String>> requestData)
            throws IOException {
        String urlString = requestData.getUri().toString();
        List<NameValuePair> paramList = new ArrayList<NameValuePair>();
        for (Map.Entry<String, String> entry : requestData.getRequestData().entrySet()) {
            paramList.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        if (!paramList.isEmpty()) {
            urlString += "?";
        }
        urlString += URLEncodedUtils.format(paramList, "UTF-8");
        HttpGet request = new HttpGet(urlString);
        request.addHeader("Content-Type", APPLICATION_JSON);
        request.addHeader("Accept", APPLICATION_JSON);
        HttpResponse response = httpClient.execute(request, httpContext);
        String responseString = responseToString(response);
        return new Response(response.getStatusLine(), responseString);
    }

    private String responseToString(HttpResponse response) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(
                (int) response.getEntity().getContentLength());
        response.getEntity().writeTo(baos);
        return new String(baos.toByteArray());
    }

}