package com.funkyquest.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.funkyquest.app.api.FQApiActions;
import com.funkyquest.app.api.FQServiceAPI;
import com.funkyquest.app.util.websockets.WebSocketClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Collections;
import java.util.Properties;

public class FunkyQuestApplication extends Application {

	public static final String DEFAULT_PROPERTIES_FILE =
			"properties.properties";

	private static Context context;

	private static String serverHost;

	private static int serverPort;

	private static FQServiceAPI serviceAPI;

	private static FQWebSocketClient socketClient;

	public static Context getGlobalApplicationContext() {
		return context;
	}

	public static int getServerPort() {
		return serverPort;
	}

	public static String getServerHost() {
		return serverHost;
	}

	public static FQWebSocketClient getSocketClient() {
		return socketClient;
	}

	public static FQServiceAPI getServiceAPI() {
		if (serviceAPI == null) {
			Properties properties = getDefaultProperties(context);
			serverHost = properties.getProperty("server_host");
			serverPort =
					Integer.parseInt(properties.getProperty("server_port"));
			serviceAPI = new FQServiceAPI(serverHost, serverPort);
		}
		return serviceAPI;
	}

	public static FQWebSocketClient getWebSocketClient() {
		if (socketClient == null) {
			URI uri = FQApiActions.CONNECT_TO_WEBSOCKET.createURI(serverHost,
			                                                      serverPort);
			WebSocketClientListener listener = new WebSocketClientListener();
			WebSocketClient webSocketClient = new WebSocketClient(uri, listener, Collections
					.<BasicNameValuePair>emptyList());
			socketClient = new FQWebSocketClient(webSocketClient,listener);
		}
		return socketClient;
	}

	public static Properties getProperties(String file, Context context) {
		Properties properties = new Properties();
		try {
			AssetManager assetManager = context.getAssets();
			InputStream inputStream = assetManager.open(file);
			properties.load(inputStream);
		} catch (IOException e) {
			Log.e("PropertyReader", e.toString());
		}
		return properties;
	}

	public static Properties getDefaultProperties(Context context) {
		return getProperties(DEFAULT_PROPERTIES_FILE, context);
	}

	public static void showToast(final Activity activity, final String text,
	                             final Duration duration) {
		activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(activity, text, duration.value).show();
			}
		});
	}

	public static Drawable scaleDrawable(Resources resources, Drawable source,
	                                     double scaleX, double scaleY) {
		Bitmap bitmap = ((BitmapDrawable) source).getBitmap();
		int bw = bitmap.getWidth();
		int bh = bitmap.getHeight();
		return new BitmapDrawable(resources,
		                          Bitmap.createScaledBitmap(bitmap,
		                                                    (int) (bw * scaleX),
		                                                    (int) (bh * scaleY),
		                                                    true));
	}

	public static void closeActivityAfterDelay(final Activity activity,
	                                           int delay) {
		postToMainThreadAfterDelay(new Runnable() {

			@Override
			public void run() {
				activity.finish();
			}
		}, delay);
	}

	public static void postToMainThreadAfterDelay(Runnable action, int delay) {
		new Handler(Looper.getMainLooper()).postDelayed(action, delay);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d("APP", "creating application");
		context = getApplicationContext();
	}

	public enum Duration {
		LONG(Toast.LENGTH_LONG),
		SHORT(Toast.LENGTH_SHORT);

		private int value;

		private Duration(int value) {
			this.value = value;
		}
	}

    public static void setViewState(boolean enabled, ViewGroup vg){
        for (int i = 0; i < vg.getChildCount(); i++){
            View child = vg.getChildAt(i);
            child.setEnabled(enabled);
            if (child instanceof ViewGroup){
                setViewState(enabled, (ViewGroup) child);
            }
        }
    }

}