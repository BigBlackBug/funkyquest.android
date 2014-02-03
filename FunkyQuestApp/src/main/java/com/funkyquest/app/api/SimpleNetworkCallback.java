package com.funkyquest.app.api;

import android.util.Log;

/**
 * Created by BigBlackBug on 2/3/14.
 */
public abstract class SimpleNetworkCallback<T> implements NetworkCallback<T> {
    private static final String TAG = "SimpleNetworkCallback";

    @Override
    public void onApplicationError(int errorCode) {
        //TODO какой запрос?
        Log.e(TAG, "Запрос вернул ошбку " + errorCode);
    }

    @Override
    public void onPostExecute() {
    }

    @Override
    public void onException(Exception ex) {

    }
}