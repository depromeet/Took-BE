package com.evenly.took.feature.auth.client.apple.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AppleErrorResponse(
	String error,

	@JsonProperty("error_description")
	String errorDescription
) {
}
