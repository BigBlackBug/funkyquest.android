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
import android.widget.TextView;
import com.funkyquest.app.FQWebSocketClient;
import com.funkyquest.app.FunkyQuestApplication;
import com.funkyquest.app.WebSocketClientListener;
import com.funkyquest.app.api.FQServiceAPI;
import com.funkyquest.app.api.SimpleNetworkCallback;
import com.funkyquest.app.dto.*;
import com.funkyquest.app.dto.util.EventType;
import com.qbix.funkyquest.R;

/**
 * Created by BigBlackBug on 2/1/14.
 */
public class CurrentTaskFragment extends Fragment {

	private final FQWebSocketClient socketClient;
	private InGameTaskDTO taskDTO;
    private long gameID;
    private FQServiceAPI serviceAPI;
    private ViewGroup mainContainer;
    private boolean tookHints = false;

	private TextView taskTitleTV;

	private TextView taskDescriptionTV;

	private boolean hintRequested = false;

	public CurrentTaskFragment(Long gameID, InGameTaskDTO taskDTO,FQWebSocketClient socketClient) {
        this.gameID = gameID;
		this.socketClient = socketClient;
        this.taskDTO = taskDTO;
    }

    private void fillViews() {
	    TaskDTO originalTask = taskDTO.getOriginalTask();
	    taskTitleTV.setText(originalTask.getTitle());
	    taskDescriptionTV.setText(originalTask.getText());
	    //TODO add more info
    }

	private void addListeners(){
	//	    add(EventType.ANSWER_ACCEPTED);
//            add(EventType.ANSWER_REJECTED);
//            add(EventType.ANSWER_POSTED);
		//HINT_REQUESTED
		socketClient.addMessageListener(EventType.ANSWER_ACCEPTED,
		new WebSocketClientListener.FQMessageListener<Void>() {
			@Override
			public void onMessage(Void message) {
//				TODO unfreeze buttons
//				TODO get next task
			}
		});
		socketClient.addMessageListener(EventType.ANSWER_REJECTED,
		new WebSocketClientListener.FQMessageListener<Void>() {
			@Override
			public void onMessage(Void message) {
				//TODO show notif
//				TODO unfreeze buttons
			}
		});

		socketClient.addMessageListener(EventType.ANSWER_POSTED,
		new WebSocketClientListener.FQMessageListener<Void>() {
			@Override
			public void onMessage(Void message) {
				//TODO show notif
//				TODO freeze buttons
			}
		});
		socketClient.addMessageListener(EventType.HINT_REQUESTED,
		new WebSocketClientListener.FQMessageListener<HintDTO>() {
			@Override
			public void onMessage(HintDTO hintDTO) {
				if(!hintRequested){
					addHint(hintDTO);
				}
				hintRequested = false;
			}
		});
	}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
	    serviceAPI = FunkyQuestApplication.getServiceAPI();
	    final View rootView = inflater.inflate(R.layout.current_task_fragment, container, false);
	    Button takeHint = (Button) rootView.findViewById(R.id.button_take_hint);
	    this.mainContainer = (ViewGroup) rootView.findViewById(R.id.layout_current_task);
	    this.mainContainer = (ViewGroup) rootView.findViewById(R.id.layout_enable_gps);
	    taskTitleTV =
			    (TextView) mainContainer.findViewById(R.id.tv_task_task_title);
	    taskDescriptionTV =
			    (TextView) mainContainer.findViewById(R.id.tv_task_description);
	    fillViews();


	    takeHint.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
//                Activity activity = getActivity();
//                Resources resources = activity.getResources();
//                if (!tookHints) {
//                    mainContainer.addView(new SeparatorView(activity, resources.getString(R.string.hints)),
//                            new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
//                                    LinearLayout.LayoutParams.WRAP_CONTENT));
//                    tookHints = true;
//                }
//                HintDTO hintDTO = new HintDTO();
//                hintDTO.setName("ASD");//TODO getHint
//                TakenHintView takenHintView = new TakenHintView(activity, hintDTO);
//
//                mainContainer.addView(takenHintView,
//                        new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
//                                LinearLayout.LayoutParams.WRAP_CONTENT));
			hintRequested = true;
            serviceAPI.getNextHint(gameID, taskDTO.getId(), new SimpleNetworkCallback<HintDTO>() {
                @Override
                public void onSuccess(HintDTO hintDTO) {
                    addHint(hintDTO);
                }
            });
            //TODO exc
            }
        });
        Button answerButton = (Button) rootView.findViewById(R.id.button_answer);
        answerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

	private void addHint(HintDTO hintDTO) {
		Activity activity = getActivity();
		Resources resources = activity.getResources();
		LinearLayout.LayoutParams layoutParams =
				new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
				                              LinearLayout.LayoutParams.WRAP_CONTENT);
		if (!tookHints) {
			SeparatorView separatorView = new SeparatorView(activity, resources.getString(R.string.hints));
			mainContainer.addView(separatorView, layoutParams);
			tookHints = true;
		}
		TakenHintView takenHintView = new TakenHintView(activity, hintDTO);
		mainContainer.addView(takenHintView,layoutParams);
	}
}
