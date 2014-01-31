package com.funkyquest.app.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.funkyquest.app.api.utils.AsyncGetRequest;
import com.funkyquest.app.api.utils.NetworkCallback;
import com.funkyquest.app.dto.GameDTO;

import java.net.URI;
import java.util.Collections;
import java.util.Map;

public class FQServiceAPI {
    public static final Map<String,String> EMPTY_MAP = Collections.emptyMap();
    private final AsyncRestService restService;
    private final int serverPort;
    private final String serverAddress;

    private final ObjectMapper mapper;

    public FQServiceAPI(AsyncRestService restService, String serverAddress, int serverPort) {
        this.restService = restService;
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.mapper = new ObjectMapper();
    }

    public void getCurrentGame(NetworkCallback<GameDTO> callback) {
        URI uri = FQApiActions.ACTIVE_GAME.createURI(serverAddress, serverPort);
        restService.get(new AsyncGetRequest<GameDTO>(uri, EMPTY_MAP, new TypeReference<GameDTO>() {
        }, callback));
    }

}