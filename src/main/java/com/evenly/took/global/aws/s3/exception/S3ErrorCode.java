package com.evenly.took.global.aws.s3.exception;

import org.springframework.http.HttpStatus;

import com.evenly.took.global.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum S3ErrorCode implements ErrorCode {

	PRESIGNED_UPLOAD_URL_GENERATION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "PresignedUrl 업로드 URL 생성에 실패했습니다."),
	PRESIGNED_VIEW_URL_GENERATION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "PresignedUrl 조회 URL 생성에 실패했습니다."),
	FILE_UPLOAD_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다."),
	FILE_DELETE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "파일 삭제에 실패했습니다.");

	private final HttpStatus status;
	private final String message;
}
