package com.evenly.took.feature.auth.dto;

public record TokenDto(
	String accessToken,
	String refreshToken
) {
}
