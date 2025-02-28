package com.evenly.took.feature.auth.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.evenly.took.feature.auth.domain.OAuthType;
import com.evenly.took.feature.auth.dto.request.RefreshTokenRequest;
import com.evenly.took.feature.auth.dto.response.AuthResponse;
import com.evenly.took.feature.auth.dto.response.OAuthUrlResponse;
import com.evenly.took.feature.auth.dto.response.TokenResponse;
import com.evenly.took.global.exception.dto.ErrorResponse;
import com.evenly.took.global.response.SuccessResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "[1. Auth]")
public interface AuthApi {

	@Operation(
		summary = "소셜 로그인 인가 URL 리다이렉트",
		description = "지정된 OAuthType에 따른 소셜 로그인 인가 코드 요청 URL로 클라이언트를 리다이렉트합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "302", description = "리다이렉트 성공")
	})
	@GetMapping("/api/auth/{oauthType}")
	SuccessResponse<OAuthUrlResponse> redirectAuthRequestUrl(
		@Parameter(description = "소셜 공급자 타입 (예: GOOGLE, KAKAO, APPLE)", required = true, example = "GOOGLE")
		@PathVariable OAuthType oauthType);

	@Operation(
		summary = "소셜 로그인 및 토큰 발급",
		description = "소셜 로그인 인가 코드를 통해 토큰(Access Token & Refresh Token)을 발급받습니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "로그인 성공")
	})
	@PostMapping("/api/auth/login/{oauthType}")
	SuccessResponse<AuthResponse> login( // TODO 에러 응답 추가
		@Parameter(description = "소셜 공급자 타입 (예: GOOGLE, KAKAO, APPLE)", required = true, example = "GOOGLE")
		@PathVariable OAuthType oauthType,
		@Parameter(description = "소셜 서버로부터 전달받은 인가 코드", required = true)
		@RequestParam String code);

	@Operation(
		summary = "토큰 재발급",
		description = "토큰(Access Token)을 재발급받습니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "토큰 재발급 성공"),
		@ApiResponse(responseCode = "401", description = "Refresh Token 만료", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	@PostMapping("/api/auth/refresh")
	SuccessResponse<TokenResponse> refreshToken(
		@RequestBody(description = "Access Token을 재발급 받기 위한 Refresh Token", required = true)
		@org.springframework.web.bind.annotation.RequestBody RefreshTokenRequest request);
}
