package com.evenly.took.feature.card.exception;

import org.springframework.http.HttpStatus;

import com.evenly.took.global.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FolderErrorCode implements ErrorCode {

	FOLDER_NOT_FOUND(HttpStatus.NOT_FOUND, "폴더를 찾을 수 없습니다."),
	FOLDER_ACCESS_DENIED(HttpStatus.FORBIDDEN, "해당 폴더에 대한 권한이 없습니다."),
	FOLDER_ALREADY_DELETED(HttpStatus.BAD_REQUEST, "이미 삭제된 폴더입니다."),
	;

	private final HttpStatus status;
	private final String message;
}
