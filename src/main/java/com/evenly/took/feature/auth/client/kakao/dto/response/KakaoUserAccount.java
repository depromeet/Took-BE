package com.evenly.took.feature.auth.client.kakao.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoUserAccount(

	@JsonProperty("email")
	String email,

	@JsonProperty("profile")
	KakaoProfile profile
) {

	public String nickname() {
		return profile().nickname();
	}

	private record KakaoProfile(

		@JsonProperty("nickname")
		String nickname
	) {
	}
}
