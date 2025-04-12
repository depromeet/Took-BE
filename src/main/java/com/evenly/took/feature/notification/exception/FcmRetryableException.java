package com.evenly.took.feature.notification.exception;

public class FcmRetryableException extends RuntimeException {

	public FcmRetryableException(Exception ex) {
		super(ex);
	}
}
