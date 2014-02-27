package com.newresources.funkyquest.dto;

import java.util.List;

/**
 * Created by bigblackbug on 1/5/14.
 */
public class TaskDTO extends AbstractDTO{

	private String title;

	private String text;

	private Integer points;

	private Integer index;

	private Long searchArea;

	private AnswerDTO answer;

	private List<HintDTO> hints;

	public TaskDTO() {
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Integer getPoints() {
		return points;
	}

	public void setPoints(Integer points) {
		this.points = points;
	}

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

	public Long getSearchArea() {
		return searchArea;
	}

	public void setSearchArea(Long searchArea) {
		this.searchArea = searchArea;
	}

	public AnswerDTO getAnswer() {
		return answer;
	}

	public void setAnswer(AnswerDTO answer) {
		this.answer = answer;
	}

	public List<HintDTO> getHints() {
		return hints;
	}

	public void setHints(List<HintDTO> hints) {
		this.hints = hints;
	}
}