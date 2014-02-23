package com.funkyquest.app.dto;

public class LocationDTO extends AbstractDTO {

	// идентификатор существющего объекта OpenStreetMap. Если пользователь сам создает объект, то null
	private Long placeId;

	private FQLocation json;

	public Long getPlaceId() {
		return placeId;
	}

	public FQLocation getJson() {

		return json;
	}
}
