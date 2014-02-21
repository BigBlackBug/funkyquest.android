package com.funkyquest.app.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import com.funkyquest.app.FunkyQuestApplication;
import com.funkyquest.app.R;
import com.funkyquest.app.api.FQServiceAPI;
import com.funkyquest.app.api.LoginCredentials;
import com.funkyquest.app.api.NetworkCallback;
import com.funkyquest.app.dto.GameDTO;
import com.funkyquest.app.dto.InGameTaskDTO;
import org.apache.http.conn.ConnectTimeoutException;

import java.util.Properties;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends Activity {

    /**
     * The default email to populate the email field with.
     */
//    public static final String EXTRA_EMAIL =
//            "com.example.android.authenticatordemo.extra.EMAIL";
    public static final String TAG = "TAG";
    private final ObjectMapper mapper = new ObjectMapper();
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    // Values for email and password at the time of the login attempt.
    private String mEmail;
    private String mPassword;
    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private View mLoginFormView;
    private View mLoginStatusView;
    private TextView mLoginStatusMessageView;
    private ConnectivityManager connectivityManager;

    //TODO придумать обработку истории и прочего
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "creating login activity");

        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        setContentView(R.layout.activity_login);
        Properties properties = FunkyQuestApplication.getDefaultProperties(getApplicationContext());

        // Set up the login form.
        mEmail = properties.get("default_user").toString();
        mEmailView = (EditText) findViewById(R.id.email);
        mEmailView.setText(mEmail);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(
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

        mPasswordView.setText(properties.get("default_pass").toString());
        mLoginFormView = findViewById(R.id.login_form);
        mLoginStatusView = findViewById(R.id.login_status);
        mLoginStatusMessageView =
                (TextView) findViewById(R.id.login_status_message);

        findViewById(R.id.sign_in_button)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        attemptLogin();
                    }
                });
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        super.onCreateOptionsMenu(menu);
//        getMenuInflater().inflate(R.menu.login, menu);
//        return true;
//    }

    private boolean isNetworkConnected() {
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    public void attemptLogin() {
        if(!isNetworkConnected()){
            FunkyQuestApplication.showToast(this, "Вы не подключены к сети", FunkyQuestApplication.Duration.LONG);
            return;
        }
	    // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        mEmail = mEmailView.getText().toString();
        mPassword = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password.
        if (TextUtils.isEmpty(mPassword)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else {
            // Check for a valid email address.
            if (TextUtils.isEmpty(mEmail)) {
                mEmailView.setError(getString(R.string.error_field_required));
                focusView = mEmailView;
                cancel = true;
            } else if (!mEmail.contains("@")) {
                mEmailView.setError(getString(R.string.error_invalid_email));
                focusView = mEmailView;
                cancel = true;
            }
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
            showProgress(true);
	        FQServiceAPI serviceAPI = FunkyQuestApplication.getServiceAPI();
	        serviceAPI.login(new LoginCallback(serviceAPI), new LoginCredentials(mEmail, mPassword));
        }
    }
	private void showProgress(final boolean show){
		animateVisibility(show, mLoginStatusView);
		animateVisibility(!show, mLoginFormView);
	}
    /**
     * Shows the progress UI and hides the login form.
     */
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
		public void onSuccess(InGameTaskDTO currentTask) {
			if (currentTask == null) {
				//TODO no more tasks, yay
			} else {
				Log.i(TAG,"serializing entities");
				Intent intent = new Intent(LoginActivity.this, GameActivity.class);
				intent.putExtra("userID", userID);
				try {
					intent.putExtra("currentGame", mapper.writeValueAsString(currentGame));
					intent.putExtra("currentTask", mapper.writeValueAsString(currentTask));
				} catch (JsonProcessingException e) {

				}

				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
				Log.i(TAG, "starting activity");
				startActivity(intent);
				finish();
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

            //here it will have all the needed cookies
            serviceAPI.getCurrentGame(new NetworkCallback<GameDTO>() {
                @Override
                public void onSuccess(final GameDTO currentGame) {
                    if (currentGame == null) {
	                    FunkyQuestApplication.showToast(LoginActivity.this,"GAME=null",
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
                    //TODO
                }

                @Override
                public void onApplicationError(int errorCode) {
                    showProgress(false);
                }

                @Override
                public void onPostExecute() {
                }
            });
        }

        @Override
        public void onException(Exception ex) {
	        showProgress(false);
	        //TODO analyze exception
	        String message = "Неизвестная ошибка";
	        if(ex instanceof ConnectTimeoutException){
		        message = "Сервер недоступен";
	        }
	        FunkyQuestApplication.showToast(LoginActivity.this, message,
                    FunkyQuestApplication.Duration.LONG);
        }

        @Override
        public void onApplicationError(int errorCode) {
	        showProgress(false);
	        Log.i("A",errorCode+"");
	        if(errorCode == 400){
		        mPasswordView
				        .setError(getString(R.string.error_incorrect_password));
		        mPasswordView.requestFocus();
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
