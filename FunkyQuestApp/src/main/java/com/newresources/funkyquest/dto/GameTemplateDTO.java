package com.newresources.funkyquest.dto;

import java.util.List;

public class GameTemplateDTO extends AbstractDTO {

    private String name;

    /**
     * Описание
     */
    private String description;

    private Long owner;

    private Long img;

    private Long location;

    private Double rating;

    private List<TaskDTO> tasks;

    public GameTemplateDTO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getOwner() {
        return owner;
    }

    public void setOwner(Long owner) {
        this.owner = owner;
    }

    public Long getImg() {
        return img;
    }

    public void setImg(Long img) {
        this.img = img;
    }

    public Long getLocation() {
        return location;
    }

    public void setLocation(Long location) {
        this.location = location;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public List<TaskDTO> getTasks() {
        return tasks;
    }

    public void setTasks(List<TaskDTO> tasks) {
        this.tasks = tasks;
    }
}