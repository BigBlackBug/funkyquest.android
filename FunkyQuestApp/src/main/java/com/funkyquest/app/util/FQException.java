package com.funkyquest.app.util;

/**
 * Created by BigBlackBug on 2/17/14.
 */
public class FQException extends RuntimeException {

	public FQException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public FQException() {
	}

	public FQException(String detailMessage) {
		super(detailMessage);
	}
}
