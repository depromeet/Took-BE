package com.evenly.took.global.logging.dto;

public record PrivacySensitiveDataInfo(
	boolean logRequestBody,
	boolean logResponseBody
) {
}
