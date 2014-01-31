package com.funkyquest.app.dto;

import java.util.Date;
import java.util.List;
import java.util.Set;

public class InGameTaskDTO extends AbstractDTO {

	private TaskDTO originalTask;

	// read only
	private Set<Long> usedHintIds;

	private Integer index;

	// read only
	private Date lastCheckDate;

	// read only
	private Status status = Status.UNSOLVED;

	// read only
	private List<PlayerAnswerDTO> playerAnswers;

	public InGameTaskDTO() {
	}

	public TaskDTO getOriginalTask() {
		return originalTask;
	}

	public void setOriginalTask(TaskDTO originalTask) {
		this.originalTask = originalTask;
	}

	public Set<Long> getUsedHintIds() {
		return usedHintIds;
	}

	public void setUsedHintIds(Set<Long> usedHints) {
		this.usedHintIds = usedHints;
	}

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

	public Date getLastCheckDate() {
		return lastCheckDate;
	}

	public void setLastCheckDate(Date lastCheckDate) {
		this.lastCheckDate = lastCheckDate;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public List<PlayerAnswerDTO> getPlayerAnswers() {
		return playerAnswers;
	}

	public void setPlayerAnswers(List<PlayerAnswerDTO> playerAnswers) {
		this.playerAnswers = playerAnswers;
	}

}
