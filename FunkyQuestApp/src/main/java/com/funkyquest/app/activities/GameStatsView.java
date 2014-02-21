package com.funkyquest.app.activities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.funkyquest.app.FunkyQuestApplication;
import com.funkyquest.app.R;
import com.funkyquest.app.dto.InGameTaskDTO;
import com.funkyquest.app.dto.TaskDTO;

import java.util.Date;

/**
 * Created by bigblackbug on 2/3/14.
 */
public class GameStatsView extends LinearLayout {

	private final Date gameStartDate;

	private final Runnable updateTimerMethod = new Runnable() {

		public void run() {
			long millis = System.currentTimeMillis() - gameStartDate.getTime();
			long seconds = (millis / 1000) % 60;
			long minutes = (millis / (1000 * 60)) % 60;
			long hours = (millis / (1000 * 60 * 60)) % 24;

			String timeString = context.getString(R.string.time_in_game,
			                                      String.format("%02dч:%02dм:%02dс", hours,
			                                                    minutes, seconds));
			timeInGameTV.setText(timeString);
			FunkyQuestApplication.postToMainThreadAfterDelay(this, 100);
		}
	};

	private TextView taskPriceTV;

	private TextView scoreTV;

	private TextView hintsUsedTV;

	private TextView taskIndexTV;

	private ViewGroup mainLayout;

	private int taskPrice;

	private int taskIndex = 1;

	private int score = 0;

	private int usedHintNumber = 0;

	private TextView timeInGameTV;

	private Context context;

	private int totalHintNumber = 0;

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		return true;
	}

	public GameStatsView(Context context, InGameTaskDTO taskDTO, Date gameStartDate) {
		super(context);
		this.context = context;
		this.gameStartDate = gameStartDate;
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ViewGroup mainLayout = (ViewGroup) inflater.inflate(R.layout.game_stats_view, this, true);
		this.mainLayout = mainLayout;
		timeInGameTV = (TextView) mainLayout.findViewById(R.id.tv_time_in_game);
		taskIndexTV = (TextView) mainLayout.findViewById(R.id.tv_task_index);
		hintsUsedTV = (TextView) mainLayout.findViewById(R.id.tv_hints_used);
		scoreTV = (TextView) mainLayout.findViewById(R.id.tv_score);
		taskPriceTV = (TextView) mainLayout.findViewById(R.id.tv_task_price);
		TaskDTO originalTask = taskDTO.getOriginalTask();
		setTaskPrice(originalTask.getPoints());
		setTaskIndex(1);
		setUsedHintNumber(taskDTO.getUsedHintIds().size(),
		                  originalTask.getHints().size());
		setScore(0);
		FunkyQuestApplication.postToMainThread(updateTimerMethod);
	}

	private void setScore(int score) {
		this.score = score;
		scoreTV.setText(context.getString(R.string.score, score));
	}

	public void increaseScore(int value) {
		setScore(score + value);
	}

	private void setTaskIndex(int taskIndex) {
		this.taskIndex = taskIndex;
		taskIndexTV.setText(context.getString(R.string.task_index, taskIndex));
	}

	public void increaseTaskIndex() {
		setTaskIndex(taskIndex + 1);
	}

	public void setTaskPrice(int taskPrice) {
		if (taskPrice < 0) {
			taskPrice = 0;
		}
		this.taskPrice = taskPrice;
		taskPriceTV.setText(context.getString(R.string.task_price, taskPrice));
	}

	public void increaseUsedHintNumber() {
		setUsedHintNumber(usedHintNumber + 1, totalHintNumber);
	}

	private void setUsedHintNumber(int usedHintNumber, int totalHintNumber) {
		this.usedHintNumber = usedHintNumber;
		this.totalHintNumber = totalHintNumber;
		hintsUsedTV
				.setText(context.getString(R.string.hints_used, usedHintNumber, totalHintNumber));
	}
}
