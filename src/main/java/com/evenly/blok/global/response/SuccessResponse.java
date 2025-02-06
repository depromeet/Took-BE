package com.evenly.blok.global.response;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SuccessResponse {

    private static final HttpStatus DEFAULT_HTTP_STATUS = HttpStatus.OK;
    private static final String DEFAULT_MESSAGE = "요청이 성공적으로 처리되었습니다.";

    private final HttpStatus status;
    private final String message;
    private final LocalDateTime timestamp;
    private final Object data;

    public static SuccessResponse of(Object data) {
        return new SuccessResponse(DEFAULT_HTTP_STATUS, DEFAULT_MESSAGE, LocalDateTime.now(), data);
    }
}
