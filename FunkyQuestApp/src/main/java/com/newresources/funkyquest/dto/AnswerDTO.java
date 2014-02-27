package com.newresources.funkyquest.dto;

/**
 * Created by bigblackbug on 1/5/14.
 */
public class AnswerDTO extends AbstractDTO {

    private String text;

    private Long media;

    private AnswerType type;

    public Long getMedia() {
        return media;
    }

    public void setMedia(Long media) {
        this.media = media;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public AnswerType getType() {
        return type;
    }

    public void setType(AnswerType type) {
        this.type = type;
    }

}