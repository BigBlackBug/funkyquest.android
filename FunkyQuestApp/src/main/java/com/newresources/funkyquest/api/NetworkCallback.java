package com.newresources.funkyquest.api;

/**
 * Created by bigblackbug on 1/28/14.
 */
public interface NetworkCallback<T> {

    /**
     * Вызывается при успешном выполнении запроса.
     * Если ответ пришёл пустой, то arg будет null
     * @param arg
     */
    public void onSuccess(T arg);

    public void onException(Exception ex);

    public void onApplicationError(int errorCode);

    public void onPostExecute();
}
