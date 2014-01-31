package com.funkyquest.app.dto;

import java.util.ArrayList;
import java.util.List;

public class InGameTaskSequenceDTO extends AbstractDTO{

	private TeamDTO team;

	private List<InGameTaskDTO> tasks = new ArrayList<InGameTaskDTO>();

	public InGameTaskSequenceDTO() {
	}

	public TeamDTO getTeam() {
		return team;
	}

	public void setTeam(TeamDTO team) {
		this.team = team;
	}

	public List<InGameTaskDTO> getTasks() {
		return tasks;
	}

	public void setTasks(List<InGameTaskDTO> tasks) {
		this.tasks = tasks;
	}

}
