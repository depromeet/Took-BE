package com.evenly.blok.global.exception;

import org.springframework.http.HttpStatus;

public interface ErrorCode {

    HttpStatus getStatus();

    ErrorResponse getResponse();
}
