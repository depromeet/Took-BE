package com.evenly.took.feature.auth.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.evenly.took.feature.auth.domain.OAuthType;
import com.evenly.took.global.response.SuccessResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "[1. OAuth]")
public interface OAuthApi {

	@Operation(
		summary = "소셜 로그인 인가 URL 리다이렉트",
		description = "지정된 OAuthType에 따른 소셜 로그인 인가 코드 요청 URL로 클라이언트를 리다이렉트합니다.")
	@ApiResponse(
		responseCode = "302",
		description = "리다이렉트 성공")
	@GetMapping("/{oauthType}")
	SuccessResponse redirectAuthRequestUrl(
		@Parameter(description = "소셜 공급자 타입 (예: GOOGLE, KAKAO, APPLE)", required = true, example = "GOOGLE")
		@PathVariable OAuthType oauthType);

	@Operation(
		summary = "소셜 로그인 및 토큰 발급",
		description = "소셜 로그인 인가 코드를 통해 토큰(Access Token & Refresh Token)을 발급받습니다.")
	@ApiResponse(
		responseCode = "200",
		description = "로그인 성공",
		headers = @Header(name = "Authorization", description = "Bearer [access token] [refresh token]", required = true),
		content = @Content(schema = @Schema(implementation = SuccessResponse.class)))
	@GetMapping("/login/{oauthType}")
	SuccessResponse login(
		@Parameter(description = "소셜 공급자 타입 (예: GOOGLE, KAKAO, APPLE)", required = true, example = "GOOGLE")
		@PathVariable OAuthType oauthType,
		@Parameter(description = "소셜 서버로부터 전달받은 인가 코드", required = true)
		@RequestParam String code);
}
