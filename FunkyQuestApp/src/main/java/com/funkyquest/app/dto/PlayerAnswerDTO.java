package com.funkyquest.app.dto;

import java.util.Date;

public class PlayerAnswerDTO extends AbstractDTO {

    private Long player;

    private Long task;

    private String text;

    private Long mediaObj;

    private Date createdDate;

    //read only
    private Date checkedDate;

    public PlayerAnswerDTO() {
    }

    public Long getTask() {
        return task;
    }

    public void setTask(Long task) {
        this.task = task;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Long getMediaObj() {
        return mediaObj;
    }

    public void setMediaObj(Long mediaObj) {
        this.mediaObj = mediaObj;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getCheckedDate() {
        return checkedDate;
    }

    public void setCheckedDate(Date checkedDate) {
        this.checkedDate = checkedDate;
    }

    public void setPlayer(Long player) {
        this.player = player;
    }

    public Long getPlayer() {
        return player;
    }

}
