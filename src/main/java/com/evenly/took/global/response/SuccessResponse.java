package com.evenly.took.global.response;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SuccessResponse<T> {

	private static final HttpStatus DEFAULT_HTTP_STATUS = HttpStatus.OK;
	private static final String DEFAULT_MESSAGE = "요청이 성공적으로 처리되었습니다.";

	private final HttpStatus status;
	private final String message;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private final LocalDateTime timestamp;
	private final T data;

	public static <T> SuccessResponse<T> of(T data) {
		return new SuccessResponse<>(DEFAULT_HTTP_STATUS, DEFAULT_MESSAGE, LocalDateTime.now(), data);
	}

	public static <T> SuccessResponse<T> of(HttpStatus status, T data) {
		return new SuccessResponse<>(status, DEFAULT_MESSAGE, LocalDateTime.now(), data);
	}

	public static SuccessResponse<Void> created(String message) {
		return new SuccessResponse<>(HttpStatus.CREATED, message, LocalDateTime.now(), null);
	}

	public static SuccessResponse<Void> ok(String message) {
		return new SuccessResponse<>(HttpStatus.OK, message, LocalDateTime.now(), null);
	}

	public static SuccessResponse<Void> deleted(String message) {
		return new SuccessResponse<>(HttpStatus.NO_CONTENT, message, LocalDateTime.now(), null);
	}
}
