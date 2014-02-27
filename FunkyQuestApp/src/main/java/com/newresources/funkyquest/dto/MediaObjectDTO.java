package com.newresources.funkyquest.dto;

import java.util.Date;

public class MediaObjectDTO extends AbstractDTO {

    private String name;

    private String mimeType;

    private String path;

    private String uuid;

    //read only
    private Date deleted;

    public MediaObjectDTO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

	public Date getDeleted() {
		return deleted;
	}
}
