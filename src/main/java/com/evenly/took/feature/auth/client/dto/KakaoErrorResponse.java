package com.evenly.took.feature.auth.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoErrorResponse(

	@JsonProperty("error")
	String error,

	@JsonProperty("error_description")
	String errorDescription,

	@JsonProperty("error_code")
	String errorCode
) {
}
