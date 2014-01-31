package com.funkyquest.app.dto;

import java.util.HashMap;
import java.util.Map;

public class LocationDTO extends AbstractDTO {

    // идентификатор существющего объекта OpenStreetMap. Если пользователь сам создает объект, то null
    private Long placeId;

    private Map<String, Object> json = new HashMap<String,Object>();
    public void setPlaceId(Long placeId) {
        this.placeId = placeId;
    }

    public Long getPlaceId() {
        return placeId;
    }

    public void setJson(Map<String, Object> json) {
        this.json = json;
    }

    public Map<String, Object> getJson() {
        return json;
    }

}
