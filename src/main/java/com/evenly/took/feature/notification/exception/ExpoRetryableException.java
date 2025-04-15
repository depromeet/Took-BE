package com.evenly.took.feature.notification.exception;

public class ExpoRetryableException extends RuntimeException {

	public ExpoRetryableException(Exception ex) {
		super(ex);
	}

	public ExpoRetryableException(String message) {
		super(message);
	}
}
