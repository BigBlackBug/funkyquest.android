package com.funkyquest.app.activities;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.funkyquest.app.dto.InGameTaskDTO;
import com.funkyquest.app.R;

/**
 * Created by bigblackbug on 2/3/14.
 */
public class GameStatsView extends LinearLayout {
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

    public GameStatsView(Context context, InGameTaskDTO taskDTO) {
        super(context);
        this.context = context;
        this.taskPrice = taskDTO.getOriginalTask().getPoints();
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup mainLayout = (ViewGroup) inflater.inflate(R.layout.game_stats_view, this, true);
        this.mainLayout = mainLayout;
        timeInGameTV = (TextView) mainLayout.findViewById(R.id.tv_time_in_game);
        taskIndexTV = (TextView) mainLayout.findViewById(R.id.tv_task_index);
        hintsUsedTV = (TextView) mainLayout.findViewById(R.id.tv_hints_used);
        scoreTV = (TextView) mainLayout.findViewById(R.id.tv_score);
        taskPriceTV = (TextView) mainLayout.findViewById(R.id.tv_task_price);
    }

    public void setScore(int score) {
        this.score = score;
        scoreTV.setText(context.getString(R.string.score, score));
    }

    public void setTaskIndex(int taskIndex) {
        this.taskIndex = taskIndex;
        taskIndexTV.setText(context.getString(R.string.task_index, taskIndex));
    }

    public void setTaskPrice(int taskPrice) {
        this.taskPrice = taskPrice;
        taskPriceTV.setText(context.getString(R.string.task_price, taskPrice));
    }

    public void setUsedHintNumber(int usedHintNumber) {
        this.usedHintNumber = usedHintNumber;
        hintsUsedTV.setText(context.getString(R.string.hints_used, usedHintNumber));
    }

    public GameStatsView(Context context, AttributeSet attrs) {
        super(context, null);
    }

//    private Runnable updateTimerMethod = new Runnable() {
//
//        public void run() {
//            timeInMillies = SystemClock.uptimeMillis()–startTime;
//            finalTime = timeSwap + timeInMillies;
//
//            int seconds = (int) (finalTime / 1000);
//            int minutes = seconds / 60;
//            seconds = seconds % 60;
//            int milliseconds = (int) (finalTime % 1000);
//            textTimer.setText(“” + minutes +“:”
//            +String.format(“ % 02d”,seconds)+“:”
//            +String.format(“ % 03d”,milliseconds));
//            myHandler.postDelayed(this, 0);
//        }
//
//    };
}
