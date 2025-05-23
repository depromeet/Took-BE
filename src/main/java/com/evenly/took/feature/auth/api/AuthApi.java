package com.evenly.took.feature.auth.api;

import com.evenly.took.feature.auth.domain.OAuthType;
import com.evenly.took.feature.auth.dto.request.LoginRequest;
import com.evenly.took.feature.auth.dto.request.RefreshTokenRequest;
import com.evenly.took.feature.auth.dto.request.WithdrawRequest;
import com.evenly.took.feature.auth.dto.response.AuthResponse;
import com.evenly.took.feature.auth.dto.response.OAuthUrlResponse;
import com.evenly.took.feature.auth.dto.response.TokenResponse;
import com.evenly.took.feature.user.domain.User;
import com.evenly.took.global.exception.dto.ErrorResponse;
import com.evenly.took.global.response.SuccessResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "[1. Auth]")
public interface AuthApi {

	@SecurityRequirements
	@Operation(
		summary = "소셜 로그인 인가 URL 리다이렉트",
		description = "지정된 OAuthType에 따른 소셜 로그인 인가 코드 요청 URL로 클라이언트를 리다이렉트합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "302", description = "리다이렉트 성공")
	})
	SuccessResponse<OAuthUrlResponse> redirectAuthRequestUrl(
		@Parameter(description = "소셜 공급자 타입 (예: GOOGLE, KAKAO, APPLE)", required = true, example = "GOOGLE")
		OAuthType oauthType);

	@SecurityRequirements
	@Operation(
		summary = "소셜 로그인 및 토큰 발급",
		description = "소셜 로그인 인가 코드를 통해 토큰(Access Token & Refresh Token)을 발급받습니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "로그인 성공")
	})
	SuccessResponse<AuthResponse> login( // TODO 에러 응답 추가
		@Parameter(description = "소셜 공급자 타입 (예: GOOGLE, KAKAO, APPLE)", required = true, example = "GOOGLE")
		OAuthType oauthType,
		@Parameter(description = "소셜 서버로부터 전달받은 인가 코드", required = true)
		String code,
		@Parameter(description = "애플 최초 가입 시, 서버로부터 전달받는 사용자 이름", required = false)
		String name,
		LoginRequest request
	);

	@SecurityRequirements
	@Operation(
		summary = "토큰 재발급",
		description = "토큰(Access Token)을 재발급받습니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "토큰 재발급 성공"),
		@ApiResponse(responseCode = "401", description = "Refresh Token 만료", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	SuccessResponse<TokenResponse> refreshToken(RefreshTokenRequest request);

	@Operation(
		summary = "로그아웃",
		description = "사용자 로그아웃을 처리하고 Refresh Token을 무효화합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "로그아웃 성공"),
		@ApiResponse(responseCode = "401", description = "유효하지 않은 Refresh Token", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	SuccessResponse<Void> logout(RefreshTokenRequest request);

	@Operation(
		summary = "회원탈퇴",
		description = "사용자 계정을 비활성화하고 토큰을 무효화합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "회원탈퇴 성공"),
		@ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	SuccessResponse<Void> withdraw(User user, WithdrawRequest request);
}
