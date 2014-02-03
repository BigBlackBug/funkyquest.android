package com.funkyquest.app.activities;

import android.app.Activity;
import android.app.Fragment;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import com.funkyquest.app.FunkyQuestApplication;
import com.funkyquest.app.api.FQServiceAPI;
import com.funkyquest.app.api.SimpleNetworkCallback;
import com.funkyquest.app.dto.*;
import com.qbix.funkyquest.R;

/**
 * Created by BigBlackBug on 2/1/14.
 */
public class CurrentTaskFragment extends Fragment {
    private InGameTaskDTO taskDTO;
    private long gameID;
    private FQServiceAPI serviceAPI;
    private ViewGroup mainContainer;
    private boolean tookHints = false;

    public CurrentTaskFragment(Long gameID, InGameTaskDTO taskDTO) {
        this.gameID = gameID;
        this.taskDTO = taskDTO;
    }

    private void fillViews() {
        //TODO fill view  with originaltask
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        serviceAPI = FunkyQuestApplication.getServiceAPI();
        final View rootView = inflater.inflate(R.layout.current_task_fragment, container, false);
        Button takeHint = (Button) rootView.findViewById(R.id.button_take_hint);
        this.mainContainer = (ViewGroup) rootView.findViewById(R.id.layout_current_task);
        fillViews();

        takeHint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = getActivity();
                Resources resources = activity.getResources();
                if (!tookHints) {
                    mainContainer.addView(new SeparatorView(activity, resources.getString(R.string.hints)),
                            new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT));
                    tookHints = true;
                }
                HintDTO hintDTO = new HintDTO();
                hintDTO.setName("ASD");
                TakenHintView takenHintView = new TakenHintView(activity, hintDTO);

                mainContainer.addView(takenHintView,
                        new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT));
//                serviceAPI.getNextHint(gameID, taskDTO.getId(), new SimpleNetworkCallback<HintDTO>() {
//                    @Override
//                    public void onSuccess(HintDTO arg) {
//                        Activity activity = getActivity();
//                        Resources resources = activity.getResources();
//                        if (!tookHints) {
//                            mainContainer.addView(new SeparatorView(activity, resources.getString(R.string.hints)),
//                                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
//                                            LinearLayout.LayoutParams.WRAP_CONTENT));
//                            tookHints = true;
//                        }
//                        TakenHintView takenHintView = new TakenHintView(activity, arg);
//
//                        mainContainer.addView(takenHintView,
//                                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
//                                        LinearLayout.LayoutParams.WRAP_CONTENT));
//                    }
//                });
                //TODO exc
            }
        });
        Button answerButton = (Button) rootView.findViewById(R.id.button_answer);
        answerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO а ничё, что у нас тут уже лежит ответ?
                TaskDTO originalTask = taskDTO.getOriginalTask();
                AnswerType type = originalTask.getAnswer().getType();
                switch (type) {
                    case AUDIO: {
                        //TODO звук
                        break;
                    }
                    case IMAGE: {
                        //TODO картинка
                        break;
                    }
                    case TEXT: {
                        //TODO диалог с текстом
                        break;
                    }
                    case VIDEO: {
                        //TODO видос
                        break;
                    }
                }
                serviceAPI.postAnswer(gameID, taskDTO.getId(),
                /*TODO create answer*/new AnswerDTO(), new SimpleNetworkCallback<AnswerIdDTO>() {
                    @Override
                    public void onSuccess(AnswerIdDTO arg) {
                        //TODO answer posted
                    }
                });
            }
        });
        return rootView;
    }
}
