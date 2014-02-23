package com.funkyquest.app.activities;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.funkyquest.app.FQWebSocketClient;
import com.funkyquest.app.FunkyQuestApplication;
import com.funkyquest.app.R;
import com.funkyquest.app.WebSocketClientListener;
import com.funkyquest.app.api.FQServiceAPI;
import com.funkyquest.app.api.SimpleNetworkCallback;
import com.funkyquest.app.api.progress.WriteListener;
import com.funkyquest.app.dto.*;
import com.funkyquest.app.dto.util.EventType;
import com.funkyquest.app.util.NotificationService;
import com.funkyquest.app.util.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Set;

/**
 * Created by BigBlackBug on 2/1/14.
 */
public class CurrentTaskFragment extends Fragment {

	private final FQWebSocketClient socketClient;

	private final GameStatsView gameStatsView;

	private InGameTaskDTO taskDTO;

	private long gameID;

	private FQServiceAPI serviceAPI;

	private ViewGroup mainContainer;

	private boolean tookHints = false;

	private TextView taskTitleTV;

	private TextView taskDescriptionTV;

	private boolean hintRequested = false;

    private ViewGroup buttonsLayout;

	private int currentTaskPrice;

	private int hintsUsed = 0;

	private Button takeHint;

	private TaskDTO originalTask;

	private GameActivity gameActivity;

	private PhotoComponent photoComponent;

	private File imageFile;

	private NotificationService notificationService;

	private Button showOnMapButton;

//	public CurrentTaskFragment(Long gameID, InGameTaskDTO taskDTO, FQWebSocketClient socketClient,
//                               GameStatsView gameStatsView) {
//        this.gameID = gameID;
//	    this.gameStatsView = gameStatsView;
//		this.socketClient = socketClient;
//        setTaskDTO(taskDTO);
//    }

	public CurrentTaskFragment(GameActivity gameActivity) {
		this.gameActivity = gameActivity;
		this.gameID = gameActivity.getGameId();
		this.gameStatsView = gameActivity.getGameStatsView();
		this.socketClient = gameActivity.getSocketClient();
		this.notificationService = new NotificationService(gameActivity);
		setTaskDTO(gameActivity.getTaskDTO());
	}

	private void fillViews() {
	    takeHint.setEnabled(false);
	    if (originalTask.getHints().size() != 0) {
		    takeHint.setEnabled(true);
	    }
		for(PlayerAnswerDTO playerAnswerDTO:taskDTO.getPlayerAnswers()){
			if(playerAnswerDTO.getTask().equals(taskDTO.getId()) && playerAnswerDTO.getCheckedDate()==null){
				notificationService.showNotification("FunkyQuest", "Ответ принят", true,
				                                     EventType.ANSWER_POSTED);
				gameActivity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
					FunkyQuestApplication.setViewState(false, buttonsLayout);
					}
				});
			}
		}
	    taskTitleTV.setText(originalTask.getTitle());
	    taskDescriptionTV.setText(originalTask.getText());
		serviceAPI.getTaskLocation(originalTask.getSearchArea(),new SimpleNetworkCallback<LocationDTO>() {
			@Override
			public void onSuccess(LocationDTO location) {
				gameActivity.getSectionsAdapter().getMapFragment().setTaskLocation(location.getJson());
			}
		});
    }

	private void setTaskDTO(InGameTaskDTO taskDTO){
		this.taskDTO = taskDTO;
		this.originalTask = taskDTO.getOriginalTask();
		this.currentTaskPrice = originalTask.getPoints();
	}

	private void addListeners(){
		socketClient.addMessageListener(EventType.ANSWER_ACCEPTED,
		new WebSocketClientListener.FQMessageListener<Void>() {
			@Override
			public void onMessage(Void message) {
			gameStatsView.increaseScore(currentTaskPrice);
			gameStatsView.increaseTaskIndex();
			gameActivity.showProgressBar(gameActivity.getString(R.string.getting_new_task));
            serviceAPI.getCurrentTask(gameID,new SimpleNetworkCallback<InGameTaskDTO>() {
                @Override
                public void onSuccess(InGameTaskDTO taskDTO) {
                if(taskDTO == null){
	                //TODO yay no more tasks
	                return;
                }
                notificationService.closeNotification(EventType.ANSWER_POSTED);
                notificationService.showNotification("FunkyQuest", "Ответ правильный!", false,
                                                     EventType.ANSWER_REJECTED);
                setTaskDTO(taskDTO);
                gameActivity.hideProgressBar();
                fillViews();
                FunkyQuestApplication.setViewState(true, buttonsLayout);
                }
            });
			}
		});
		socketClient.addMessageListener(EventType.ANSWER_REJECTED,
		new WebSocketClientListener.FQMessageListener<Void>() {
			@Override
			public void onMessage(Void message) {
			Log.i(getTag(),"answer rejected");
			notificationService.closeNotification(EventType.ANSWER_POSTED);
			notificationService.showNotification("FunkyQuest", "Ответ неверный", false,
			                                     EventType.ANSWER_REJECTED);
			gameActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
				FunkyQuestApplication.setViewState(true, buttonsLayout);
				}
			});
			}
		});

		socketClient.addMessageListener(EventType.ANSWER_POSTED,
		new WebSocketClientListener.FQMessageListener<Void>() {
			@Override
			public void onMessage(Void message) {
			Log.i(getTag(),"answer posted notif");
			notificationService.showNotification("FunkyQuest", "Ответ принят", true,
			                                     EventType.ANSWER_POSTED);
			gameActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
				FunkyQuestApplication.setViewState(false,buttonsLayout);
				}
			});
			}
		});
		socketClient.addMessageListener(EventType.HINT_REQUESTED,
		new WebSocketClientListener.FQMessageListener<HintDTO>() {
			@Override
			public void onMessage(HintDTO hintDTO) {
			Log.i(getTag(),"hint requested");
            if(!hintRequested){
	            currentTaskPrice -= hintDTO.getPoints();
	            gameStatsView.setTaskPrice(currentTaskPrice);
	            gameStatsView.increaseUsedHintNumber();
                processHint(hintDTO);
            }
            hintRequested = false;
			}
		});
	}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
	    this.photoComponent = new PhotoComponent(gameActivity);
	    serviceAPI = FunkyQuestApplication.getServiceAPI();
	    final View rootView = inflater.inflate(R.layout.current_task_fragment, container, false);
	    takeHint = (Button) rootView.findViewById(R.id.button_take_hint);
	    this.mainContainer = (ViewGroup) rootView.findViewById(R.id.layout_current_task);
//	    this.mainContainer = (ViewGroup) rootView.findViewById(R.id.layout_enable_gps);
        buttonsLayout = (ViewGroup) rootView.findViewById(R.id.layout_buttons);
	    showOnMapButton= (Button) rootView.findViewById(R.id.button_show_on_map);
	    showOnMapButton.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
		    gameActivity.getViewPager().setCurrentItem(1);
		    gameActivity.getSectionsAdapter().getMapFragment().centerOnTask();
	     }
    });
        taskTitleTV =
			    (TextView) mainContainer.findViewById(R.id.tv_task_task_title);
	    taskDescriptionTV =
			    (TextView) mainContainer.findViewById(R.id.tv_task_description);
	    fillViews();
	    Set<Long> usedHintIds = taskDTO.getUsedHintIds();
	    for (HintDTO hintDTO:originalTask.getHints()){
		    if(usedHintIds.contains(hintDTO.getId())){
			    currentTaskPrice -= hintDTO.getPoints();
			    processHint(hintDTO);
		    }
	    }
	    gameStatsView.setTaskPrice(currentTaskPrice);
        addListeners();

	    takeHint.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
				hintRequested = true;
	            serviceAPI.getNextHint(gameID, taskDTO.getId(), new SimpleNetworkCallback<HintDTO>() {
	                @Override
	                public void onSuccess(HintDTO hintDTO) {
	                currentTaskPrice -= hintDTO.getPoints();
	                gameStatsView.setTaskPrice(currentTaskPrice);
	                gameStatsView.increaseUsedHintNumber();
	                processHint(hintDTO);
	                }
	            });
                //TODO exc
            }
        });
        Button answerButton = (Button) rootView.findViewById(R.id.button_answer);
        answerButton.setOnClickListener(new AnswerButtonClickListener());
	    return rootView;
    }

	private void processHint(final HintDTO hintDTO) {
        Resources resources = gameActivity.getResources();
        LinearLayout.LayoutParams layoutParams =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                              LinearLayout.LayoutParams.WRAP_CONTENT);
        if (!tookHints) {
            SeparatorView separatorView = new SeparatorView(gameActivity, resources.getString(R.string.hints));
            mainContainer.addView(separatorView, layoutParams);
            tookHints = true;
        }
        layoutParams.setMargins(30,5,30,5);
        TakenHintView takenHintView = new TakenHintView(gameActivity, hintDTO);
        mainContainer.addView(takenHintView, layoutParams);
        hintsUsed++;
        if(hintsUsed == originalTask.getHints().size()){
            takeHint.setEnabled(false);
        }
	}

	private final class ConfirmDialogListener implements View.OnClickListener {
		private final ProgressBar progressBar;
		private final AlertDialog dialog;

		private ConfirmDialogListener(ProgressBar progressBar, AlertDialog dialog) {
			this.progressBar = progressBar;
			this.dialog = dialog;
		}

		@Override
		public void onClick(View v) {
			serviceAPI.uploadFile(imageFile.getAbsolutePath(),new SimpleNetworkCallback<MediaObjectDTO>() {
                  @Override
                  public void onSuccess(MediaObjectDTO mediaObject) {
                      PlayerAnswerDTO dto = new PlayerAnswerDTO();
                      dto.setTask(taskDTO.getId());
                      dto.setMediaObj(mediaObject.getId());
                      serviceAPI.postAnswer(gameID,taskDTO.getId(),dto,new SimpleNetworkCallback<AnswerIdDTO>() {
	                      @Override
	                      public void onSuccess(AnswerIdDTO arg) {
		                      Log.i("TAG", "ANSWER POSTED");
		                      dialog.cancel();
	                      }
	                      @Override
	                      public void onApplicationError(int errorCode) {
		                      FunkyQuestApplication.showToast(gameActivity,"POST FAILED RETURN CODE "+errorCode,
		                                                      FunkyQuestApplication.Duration.LONG);
	                      }
                      });
                  }

                  @Override
                  public void onException(Exception ex) {
                      Log.e("TAG","error uploading file",ex);
	                  progressBar.setProgress(0);
                  }

                  @Override
                  public void onApplicationError(int errorCode) {
	                  progressBar.setProgress(0);
                      FunkyQuestApplication.showToast(
		                      gameActivity,"UPLOAD FAILED RETURN CODE "+errorCode,
                                                      FunkyQuestApplication.Duration.LONG);
                  }
                  //TODO exc
              },new WriteListener() {
                  private final Long imageSize = imageFile.length();
                  @Override
                  public void onWrite(long bytesWritten) {
                      int progress = (int) ((bytesWritten*100 / imageSize));
	                  progressBar.setProgress(progress);
                  }
              });
		}
	}

	public void showSendImageDialog() {
		int scaleFactor = 8;
		try {
			FileOutputStream os = new FileOutputStream(imageFile);
			Bitmap bitmap = Utils.readRotateAndScale(imageFile, scaleFactor/2, gameActivity);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
			scaleFactor = 2;
		} catch (FileNotFoundException e) {

		}
		LayoutInflater inflater = getActivity().getLayoutInflater();
		LinearLayout dialogLayout =
				(LinearLayout) inflater.inflate(R.layout.image_dialog_view, null);
		Button confirmDialogButton = (Button) dialogLayout.findViewById(R.id.button_confirm_dialog);
		Button cancelDialogButton = (Button) dialogLayout.findViewById(R.id.button_cancel_dialog);
		ProgressBar progressBar = (ProgressBar) dialogLayout.findViewById(R.id.progressbar);

		ImageView takenImageIV = (ImageView) dialogLayout.findViewById(R.id.iv_dialog_thumbnail);
		takenImageIV.setImageBitmap(Utils.readRotateAndScale(imageFile,scaleFactor, gameActivity));
		takenImageIV.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		final AlertDialog dialog = builder.setView(dialogLayout).create();
		confirmDialogButton.setOnClickListener(new ConfirmDialogListener(progressBar,dialog));
		cancelDialogButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.cancel();
			}
		});
		dialog.show();
	}

	private final class AnswerButtonClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			AnswerType type = originalTask.getAnswer().getType();
			switch (type) {
				case AUDIO: {
					//TODO звук
					break;
				}
				case IMAGE: {
					imageFile = photoComponent.dispatchTakePictureIntent();
					break;
				}
				case TEXT: {
					showTextInputDialog();
					break;
				}
				case VIDEO: {
					//TODO видос
					break;
				}
			}
		}

		private void showTextInputDialog() {
			AlertDialog.Builder builder = new AlertDialog.Builder(gameActivity);
			builder.setTitle(gameActivity.getString(R.string.enter_answer_dialog_title));

			final EditText input = new EditText(gameActivity);
			input.setInputType(
					InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
			builder.setView(input);

			builder.setPositiveButton(gameActivity.getString(R.string.answer_dialog_confirm),
			                          new SendTextAnswer(input));

			builder.setNegativeButton(gameActivity.getString(R.string.answer_dialog_cancel),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});

			builder.show();
		}

		private final class SendTextAnswer implements DialogInterface.OnClickListener {
			private final EditText input;

			private SendTextAnswer(EditText input) {
				this.input = input;
			}

			@Override
			public void onClick(DialogInterface dialog, int which) {
				String text = input.getText().toString();
				Long taskId = taskDTO.getId();
				PlayerAnswerDTO dto = new PlayerAnswerDTO();
				dto.setTask(taskId);
				dto.setText(text);
				serviceAPI.postAnswer(gameID,taskDTO.getId(),dto,new SimpleNetworkCallback<AnswerIdDTO>() {
					@Override
					public void onSuccess(AnswerIdDTO arg) {
						Log.i("TAG", "ANSWER POSTED");
					}
					@Override
					public void onApplicationError(int errorCode) {
						FunkyQuestApplication.showToast(gameActivity,"POST FAILED RETURN CODE "+errorCode,
						                                FunkyQuestApplication.Duration.LONG);
					}
				});
			}
		}
	}

}
