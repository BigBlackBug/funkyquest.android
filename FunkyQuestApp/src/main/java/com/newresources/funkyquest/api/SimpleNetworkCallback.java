package com.newresources.funkyquest.api;

import android.util.Log;

/**
 * Created by BigBlackBug on 2/3/14.
 */
public abstract class SimpleNetworkCallback<T> implements NetworkCallback<T> {
    private static final String TAG = "SimpleNetworkCallback";

    @Override
    public void onApplicationError(int errorCode) {
        Log.e(TAG, "Запрос вернул ошбку " + errorCode);
    }

    @Override
    public void onPostExecute() {
    }

    @Override
    public void onException(Exception ex) {
	   Log.e(TAG,"ERROR",ex);
    }
}
