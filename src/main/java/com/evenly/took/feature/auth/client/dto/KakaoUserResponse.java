package com.evenly.took.feature.auth.client.dto;

public record KakaoUserResponse(
	Long id,
	KakaoUserAccount kakaoAccount
) {
}
