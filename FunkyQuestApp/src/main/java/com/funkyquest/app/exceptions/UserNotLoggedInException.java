package com.funkyquest.app.exceptions;

/**
 * Created by BigBlackBug on 1/31/14.
 */
public class UserNotLoggedInException extends RuntimeException {
    public UserNotLoggedInException() {
    }

    public UserNotLoggedInException(String detailMessage) {
        super(detailMessage);
    }
}
