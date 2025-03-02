package com.evenly.took.feature.auth.client.kakao.dto;

public record KakaoUserResponse(
	Long id,
	KakaoUserAccount kakaoAccount
) {
}
