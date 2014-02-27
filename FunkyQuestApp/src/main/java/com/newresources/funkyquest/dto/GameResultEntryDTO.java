package com.newresources.funkyquest.dto;

public class GameResultEntryDTO extends AbstractDTO {

    private Long timeSpent;

    private Long points;

    private Integer hintsUsed;

    public GameResultEntryDTO() {
    }

    public Long getTimeSpent() {
        return timeSpent;
    }

    public void setTimeSpent(Long timeSpend) {
        this.timeSpent = timeSpend;
    }

    public Long getPoints() {
        return points;
    }

    public void setPoints(Long points) {
        this.points = points;
    }

    public Integer getHintsUsed() {
        return hintsUsed;
    }

    public void setHintsUsed(Integer hintsUsed) {
        this.hintsUsed = hintsUsed;
    }

}
