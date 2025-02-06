package com.evenly.blok.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommonErrorCode implements ErrorCode {

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 관리자에게 문의하세요."),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "유효하지 않은 요청값입니다."),
    ;

    private final HttpStatus status;
    private final String message;
}
