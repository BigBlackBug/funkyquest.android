package com.funkyquest.app.exceptions;

/**
 * Created by BigBlackBug on 1/26/14.
 */
public class FunkyQuestException extends RuntimeException {

	public FunkyQuestException() {
	}

	public FunkyQuestException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public FunkyQuestException(String detailMessage) {
		super(detailMessage);
	}
}
