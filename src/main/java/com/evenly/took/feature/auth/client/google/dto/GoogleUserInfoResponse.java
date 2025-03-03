package com.evenly.took.feature.auth.client.google.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GoogleUserInfoResponse(

	@JsonProperty("sub")
	String sub,

	@JsonProperty("name")
	String name
) {
}
