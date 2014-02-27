package com.newresources.funkyquest.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newresources.funkyquest.FunkyQuestApplication;
import com.newresources.funkyquest.R;
import com.newresources.funkyquest.api.FQServiceAPI;
import com.newresources.funkyquest.api.LoginCredentials;
import com.newresources.funkyquest.api.NetworkCallback;
import com.newresources.funkyquest.dto.GameDTO;
import com.newresources.funkyquest.dto.InGameTaskDTO;
import com.newresources.funkyquest.util.Constants;
import com.newresources.funkyquest.util.FQObjectMapper;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;

public class LoginActivity extends Activity {

    public static final String TAG = "TAG";

	public static final int BAD_REQUEST = 400;

	public static final String LAST_SUCCESSFUL_LOGIN = "last_successful_login";

	private final ObjectMapper mapper = new FQObjectMapper();

	private String mEmail;

	private EditText emailView;

	private EditText passwordView;

	private View loginFormView;

	private View loginStatusView;

	private ConnectivityManager connectivityManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "creating login activity");

        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        setContentView(R.layout.activity_login);
//        Properties properties = FunkyQuestApplication.getDefaultProperties(getApplicationContext());

        // Set up the login form.
//        mEmail = properties.get("default_user").toString();
	    SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
	    mEmail = sharedPref.getString(LAST_SUCCESSFUL_LOGIN,"");
        emailView = (EditText) findViewById(R.id.email);
        emailView.setText(mEmail);

        passwordView = (EditText) findViewById(R.id.password);
        passwordView.setOnEditorActionListener(
		        new TextView.OnEditorActionListener() {
			        @Override
			        public boolean onEditorAction(TextView textView, int id,
			                                      KeyEvent keyEvent) {
				        if (id == R.id.login || id == EditorInfo.IME_NULL) {
					        attemptLogin();
					        return true;
				        }
				        return false;
			        }
		        });

//        passwordView.setText(properties.get("default_pass").toString());
        loginFormView = findViewById(R.id.login_form);
        loginStatusView = findViewById(R.id.login_status);
//        mLoginStatusMessageView =
//                (TextView) findViewById(R.id.login_status_message);

        findViewById(R.id.sign_in_button)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        attemptLogin();
                    }
                });
    }

    private boolean isNetworkConnected() {
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    public void attemptLogin() {
        if(!isNetworkConnected()){
            FunkyQuestApplication.showToast(this, "Вы не подключены к сети", FunkyQuestApplication.Duration.LONG);
            return;
        }

        emailView.setError(null);
        passwordView.setError(null);

        mEmail = emailView.getText().toString();
	    String mPassword = passwordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(mPassword)) {
            passwordView.setError(getString(R.string.error_field_required));
            focusView = passwordView;
            cancel = true;
        } else {
            // Check for a valid email address.
            if (TextUtils.isEmpty(mEmail)) {
                emailView.setError(getString(R.string.error_field_required));
                focusView = emailView;
                cancel = true;
            } else if (!mEmail.contains("@")) {
                emailView.setError(getString(R.string.error_invalid_email));
                focusView = emailView;
                cancel = true;
            }
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
//            mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
            showProgress(true);
	        FQServiceAPI serviceAPI = FunkyQuestApplication.getServiceAPI();
	        serviceAPI.login(new LoginCallback(serviceAPI), new LoginCredentials(mEmail, mPassword));
        }
    }
	private void showProgress(final boolean show){
		animateVisibility(show, loginStatusView);
		animateVisibility(!show, loginFormView);
	}

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void animateVisibility(final boolean show, final View targetView) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources()
                    .getInteger(android.R.integer.config_mediumAnimTime);

	        targetView.setVisibility(View.VISIBLE);
	        targetView.animate()
                    .setDuration(shortAnimTime)
                    .alpha(show ? 1 : 0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
	                        targetView.setVisibility(
                                    show ? View.VISIBLE : View.GONE);
                        }
                    });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
	        targetView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

	@Override
	protected void onPause() {
		super.onPause();
		Log.i(TAG,"onPause");
		overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
	}

	private class StartGameActivity implements NetworkCallback<InGameTaskDTO> {

		private final Long userID;

		private final GameDTO currentGame;

		private StartGameActivity(Long userID, GameDTO currentGame) {
			this.userID = userID;
			this.currentGame = currentGame;
		}

		@Override
		public void onSuccess(final InGameTaskDTO currentTask) {
			if (currentTask == null) {
				gameEnded();
			} else {
				new AsyncTask<Void,Void,Intent>(){

					@Override
					protected Intent doInBackground(Void... params) {
						Log.i(TAG,"serializing entities");
						Intent intent = new Intent(LoginActivity.this, GameActivity.class);
						Bundle bundle = new Bundle();
						bundle.putLong("userID", userID);
						try {
							bundle.putString("currentGame", mapper.writeValueAsString(currentGame));
							bundle.putString("currentTask", mapper.writeValueAsString(currentTask));
						} catch (JsonProcessingException e) {
						}
						intent.putExtra(Constants.GAME_DATA_BUNDLE,bundle);
						return intent;
					}

					@Override
					protected void onPostExecute(Intent intent) {
						SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
						SharedPreferences.Editor editor = sharedPref.edit();
						editor.putString(LAST_SUCCESSFUL_LOGIN, mEmail);
						editor.commit();
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
						Log.i(TAG, "starting activity");
						startActivity(intent);
						finish();
					}
				}.execute();
			}
		}

		@Override
		public void onException(Exception ex) {

		}

		@Override
		public void onApplicationError(int errorCode) {

		}

		@Override
		public void onPostExecute() {
		}
		//TODO onexc
	}

	private void gameEnded() {
		Intent intent = new Intent(this, GameEndedActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
		Log.i(TAG, "starting endgameactivity");
		startActivity(intent);
		finish();
	}

	private class LoginCallback implements NetworkCallback<Long> {
        private FQServiceAPI serviceAPI;

        private LoginCallback(FQServiceAPI serviceAPI) {
            this.serviceAPI = serviceAPI;
        }

        @Override
        public void onSuccess(final Long userID) {
            FunkyQuestApplication.showToast(
                    LoginActivity.this, "Подключились!",
                    FunkyQuestApplication.Duration.SHORT);
//	        mLoginStatusMessageView.setText("Обработка данных");

            serviceAPI.getCurrentGame(new NetworkCallback<GameDTO>() {
                @Override
                public void onSuccess(final GameDTO currentGame) {
                    if (currentGame == null) {
	                    FunkyQuestApplication.showToast(LoginActivity.this,"Вы не в игре",
	                                                    FunkyQuestApplication.Duration.LONG);
	                    showProgress(false);
                        //TODO show list of available games
                    } else {
	                    FQServiceAPI serviceAPI =
			                    FunkyQuestApplication.getServiceAPI();
	                    serviceAPI.getCurrentTask(currentGame.getId(),
	                                              new StartGameActivity(userID,currentGame));
                    }
                }

	            @Override
	            public void onException(Exception ex) {
		            showProgress(false);
		            String message = "Неизвестная ошибка";
		            if(ex instanceof ConnectTimeoutException|| ex instanceof HttpHostConnectException){
			            message = "Сервер недоступен";
		            }
		            Log.e(TAG,"conn_error",ex);
		            FunkyQuestApplication.showToast(LoginActivity.this, message,
		                                            FunkyQuestApplication.Duration.LONG);
	            }

	            @Override
	            public void onApplicationError(int errorCode) {
		            showProgress(false);
		            if(errorCode == BAD_REQUEST){
			            passwordView.setError(getString(R.string.error_incorrect_password));
			            passwordView.requestFocus();
		            }else{
			            FunkyQuestApplication.showToast(LoginActivity.this, "Неизвестная ошибка",
			                                            FunkyQuestApplication.Duration.LONG);
		            }
	            }

                @Override
                public void onPostExecute() {
                }
            });
        }

        @Override
        public void onException(Exception ex) {
	        showProgress(false);
	        String message = "Неизвестная ошибка";
	        if(ex instanceof ConnectTimeoutException|| ex instanceof HttpHostConnectException){
		        message = "Сервер недоступен";
	        }
	        Log.e(TAG,"conn_error",ex);
	        FunkyQuestApplication.showToast(LoginActivity.this, message,
                    FunkyQuestApplication.Duration.LONG);
        }

        @Override
        public void onApplicationError(int errorCode) {
	        showProgress(false);
	        if(errorCode == 400){
		        passwordView.setError(getString(R.string.error_incorrect_password));
		        passwordView.requestFocus();
	        }else{
		        FunkyQuestApplication.showToast(LoginActivity.this, "Неизвестная ошибка",
		                                        FunkyQuestApplication.Duration.LONG);
	        }
        }

        @Override
        public void onPostExecute() {
        }
    }

}
