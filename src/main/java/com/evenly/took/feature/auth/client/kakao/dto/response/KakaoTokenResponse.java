package com.evenly.took.feature.auth.client.kakao.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoTokenResponse(

	@JsonProperty("access_token")
	String accessToken,

	@JsonProperty("expires_in")
	Integer expiresIn,

	@JsonProperty("refresh_token")
	String refreshToken,

	@JsonProperty("refresh_token_expires_in")
	String refreshTokenExpiresIn
) {
}
