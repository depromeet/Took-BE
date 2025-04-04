package com.evenly.took.feature.auth.client.google.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GoogleTokenResponse(

	@JsonProperty("access_token")
	String accessToken,

	@JsonProperty("expires_in")
	Integer expiresIn,

	@JsonProperty("scope")
	String scope,

	@JsonProperty("token_type")
	String tokenType,

	@JsonProperty("id_token")
	String idToken
) {
}
