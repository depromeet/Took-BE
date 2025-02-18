package com.evenly.took.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TookException extends RuntimeException {

	private final ErrorCode errorCode;
}
