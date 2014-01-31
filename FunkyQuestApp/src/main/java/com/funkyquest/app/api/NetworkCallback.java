package com.funkyquest.app.api;

/**
 * Created by bigblackbug on 1/28/14.
 */
public interface NetworkCallback<T> {

    public void onSuccess(T arg);

    public void onException(Exception ex);

    public void onApplicationError(int errorCode);

    public void onPostExecute();
}