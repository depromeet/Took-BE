package com.evenly.blok.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BlokException extends RuntimeException { // TODO: 서비스명에 맞게 변경

    private final ErrorCode errorCode;
}
