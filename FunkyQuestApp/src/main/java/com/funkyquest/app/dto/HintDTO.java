package com.funkyquest.app.dto;

import java.math.BigDecimal;

public class HintDTO extends AbstractDTO {

    private String name;

    private String text;

    private BigDecimal price;

    private Integer points;

    private Long mediaObj;

    private Integer index;

    private Long taskId; //read only

    public HintDTO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public Long getMediaObj() {
        return mediaObj;
    }

    public void setMediaObj(Long mediaObj) {
        this.mediaObj = mediaObj;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

}
