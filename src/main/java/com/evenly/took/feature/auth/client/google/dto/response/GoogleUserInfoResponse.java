package com.evenly.took.feature.auth.client.google.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GoogleUserInfoResponse(

	@JsonProperty("sub")
	String sub,

	@JsonProperty("name")
	String name
) {
}
