package com.evenly.took.feature.auth.client.kakao.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoUserResponse(

	@JsonProperty("id")
	Long id,

	@JsonProperty("kakao_account")
	KakaoUserAccount kakaoAccount
) {
}
