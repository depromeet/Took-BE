package com.evenly.took.feature.common.exception;

import com.evenly.took.global.exception.ErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TookException extends RuntimeException {

	private final ErrorCode errorCode;
}
