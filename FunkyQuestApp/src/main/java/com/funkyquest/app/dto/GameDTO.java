package com.funkyquest.app.dto;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GameDTO extends AbstractDTO {

    private GameTemplateDTO template;

    private GameType gameType = GameType.PUBLIC;

    private GameStatus status = GameStatus.CREATED;

    private Date startDate = new Date();

    private Date finishDate = new Date();

    private PaymentDTO stake;

    private Set<InGameTaskSequenceDTO> teamTasks;

    private GameLimitationsDTO limitations;

    private TasksShuffleType tasksShuffleType = TasksShuffleType.SEQUENTIAL;

    private List<Map<String, Object>> gameResults;

    public GameDTO() {
    }

    public GameTemplateDTO getTemplate() {
        return template;
    }

    public void setTemplate(GameTemplateDTO template) {
        this.template = template;
    }

    public GameType getGameType() {
        return gameType;
    }

    public void setGameType(GameType gameType) {
        this.gameType = gameType;
    }

    public GameStatus getStatus() {
        return status;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(Date finishDate) {
        this.finishDate = finishDate;
    }

    public PaymentDTO getStake() {
        return stake;
    }

    public void setStake(PaymentDTO stake) {
        this.stake = stake;
    }

    public Set<InGameTaskSequenceDTO> getTeamTasks() {
        return teamTasks;
    }

    public void setTeamTasks(Set<InGameTaskSequenceDTO> teamTasks) {
        this.teamTasks = teamTasks;
    }

    public GameLimitationsDTO getLimitations() {
        return limitations;
    }

    public void setLimitations(GameLimitationsDTO limitations) {
        this.limitations = limitations;
    }

    public TasksShuffleType getTasksShuffleType() {
        return tasksShuffleType;
    }

    public void setTasksShuffleType(TasksShuffleType tasksShuffleType) {
        this.tasksShuffleType = tasksShuffleType;
    }

    public List<Map<String, Object>> getGameResults() {
        return gameResults;
    }

    public void setGameResults(List<Map<String, Object>> gameResults) {
        this.gameResults = gameResults;
    }

}
