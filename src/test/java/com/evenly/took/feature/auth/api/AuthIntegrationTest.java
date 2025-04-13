package com.evenly.took.feature.auth.api;

import static io.restassured.RestAssured.*;
import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.ArgumentMatchers.*;

import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.evenly.took.feature.auth.client.UserClientComposite;
import com.evenly.took.feature.auth.domain.OAuthType;
import com.evenly.took.feature.auth.dto.request.LoginRequest;
import com.evenly.took.feature.auth.dto.request.RefreshTokenRequest;
import com.evenly.took.feature.auth.dto.request.WithdrawRequest;
import com.evenly.took.feature.auth.dto.response.TokenResponse;
import com.evenly.took.feature.user.dao.UserRepository;
import com.evenly.took.feature.user.domain.User;
import com.evenly.took.global.integration.IntegrationTest;

import io.restassured.http.ContentType;

public class AuthIntegrationTest extends IntegrationTest {

	@MockitoBean
	UserClientComposite userClientComposite;

	@Autowired
	private UserRepository userRepository;

	@Nested
	class 인증_코드_경로_조회 {

		@Test
		void 카카오_인증_코드를_받기_위한_경로를_반환한다() {
			given().log().all()
				.when().get("/api/auth/KAKAO")
				.then().log().all()
				.statusCode(302);
		}

		@Test
		void 제공하지_않는_플랫폼_소셜로그인을_요청한_경우_400_예외를_반환한다() {
			given().log().all()
				.when().get("/api/auth/INVALID")
				.then().log().all()
				.statusCode(400);
		}
	}

	@Nested
	class 로그인 {

		@Test
		void 로그인시_사용자_정보를_반환한다() {
			User user = userFixture.create();
			BDDMockito.given(userClientComposite.fetch(any(OAuthType.class), anyString()))
				.willReturn(user);

			given().log().all()
				.contentType(ContentType.JSON)
				.when().post("/api/auth/login/KAKAO?code=code")
				.then().log().all()
				.statusCode(200);
		}

		@Test
		void 로그인시_첫_로그인_여부를_판단한다() {
			User user = userFixture.create();
			BDDMockito.given(userClientComposite.fetch(any(OAuthType.class), anyString()))
				.willReturn(user);

			LoginRequest request = new LoginRequest("expoToken");

			boolean isFirstLogin1 = given().log().all()
				.contentType(ContentType.JSON)
				.body(request)
				.contentType(ContentType.JSON)
				.when().post("/api/auth/login/KAKAO?code=code")
				.then().log().all()
				.statusCode(200)
				.extract()
				.jsonPath()
				.getBoolean("data.isFirstLogin");
			assertThat(isFirstLogin1).isTrue();

			boolean isFirstLogin2 = given().log().all()
				.contentType(ContentType.JSON)
				.body(request)
				.contentType(ContentType.JSON)
				.when().post("/api/auth/login/KAKAO?code=code")
				.then().log().all()
				.statusCode(200)
				.extract()
				.jsonPath()
				.getBoolean("data.isFirstLogin");
			assertThat(isFirstLogin2).isFalse();
		}

		@Test
		void 유효하지_않은_액세스_토큰으로_API를_요청할_경우_401_예외를_반환한다() {
			User user = userFixture.create();
			BDDMockito.given(userClientComposite.fetch(any(OAuthType.class), anyString()))
				.willReturn(user);

			given().log().all()
				.header("Authorization", "Bearer invalid.access.token")
				.when().get("/api/test")
				.then().log().all()
				.statusCode(401);
		}

		@Test
		void 토큰_갱신_요청시_유효한_리프레쉬_토큰일_경우_액세스_토큰을_갱신한다() {
			User user = userFixture.create();
			BDDMockito.given(userClientComposite.fetch(any(OAuthType.class), anyString()))
				.willReturn(user);

			TokenResponse tokens = given().log().all()
				.contentType(ContentType.JSON)
				.when().post("/api/auth/login/KAKAO?code=code")
				.then().log().all()
				.statusCode(200)
				.extract()
				.body()
				.jsonPath()
				.getObject("data.token", TokenResponse.class);

			RefreshTokenRequest request = new RefreshTokenRequest(tokens.refreshToken());
			given().log().all()
				.contentType(ContentType.JSON)
				.body(request)
				.when().post("/api/auth/refresh")
				.then().log().all()
				.statusCode(200);
		}

		@Test
		void 토큰_갱신_요청시_유효하지_않은_리프레쉬_토큰일_경우_401_예외를_반환한다() {
			User user = userFixture.create();
			BDDMockito.given(userClientComposite.fetch(any(OAuthType.class), anyString()))
				.willReturn(user);

			RefreshTokenRequest request = new RefreshTokenRequest("invalid");
			given().log().all()
				.contentType(ContentType.JSON)
				.body(request)
				.when().post("/api/auth/refresh")
				.then().log().all()
				.statusCode(401);
		}

		@Test
		void 유효한_액세스_토큰으로_API를_요청할_경우_예외를_반환하지_않는다() {
			User user = userFixture.create();
			BDDMockito.given(userClientComposite.fetch(any(OAuthType.class), anyString()))
				.willReturn(user);

			TokenResponse tokens = given().log().all()
				.contentType(ContentType.JSON)
				.when().post("/api/auth/login/KAKAO?code=code")
				.then().log().all()
				.statusCode(200)
				.extract()
				.body()
				.jsonPath()
				.getObject("data.token", TokenResponse.class);

			given().log().all()
				.header("Authorization", "Bearer %s".formatted(tokens.accessToken()))
				.when().get("/api/test")
				.then().log().all()
				.statusCode(200);
		}
	}

	@Nested
	class 로그아웃 {
		@Test
		void 로그아웃_요청시_성공적으로_처리된다() {
			// given
			User user = userFixture.create();
			BDDMockito.given(userClientComposite.fetch(any(OAuthType.class), anyString()))
				.willReturn(user);

			TokenResponse tokens = given().log().all()
				.contentType(ContentType.JSON)
				.when().post("/api/auth/login/KAKAO?code=code")
				.then().log().all()
				.statusCode(200)
				.extract()
				.body()
				.jsonPath()
				.getObject("data.token", TokenResponse.class);

			// when, then
			RefreshTokenRequest request = new RefreshTokenRequest(tokens.refreshToken());
			given().log().all()
				.contentType(ContentType.JSON)
				.header("Authorization", "Bearer " + tokens.accessToken())
				.body(request)
				.when().post("/api/auth/logout")
				.then().log().all()
				.statusCode(200);

			// then
			given().log().all()
				.contentType(ContentType.JSON)
				.body(request)
				.when().post("/api/auth/refresh")
				.then().log().all()
				.statusCode(401);
		}
	}

	@Nested
	class 회원탈퇴 {

		@Test
		void 회원탈퇴_요청시_성공적으로_처리된다() {
			// given
			User user = userFixture.create();
			BDDMockito.given(userClientComposite.fetch(any(OAuthType.class), anyString()))
				.willReturn(user);

			// 카드 생성
			cardFixture.creator().user(user).create();

			TokenResponse tokens = given().log().all()
				.contentType(ContentType.JSON)
				.when().post("/api/auth/login/KAKAO?code=code")
				.then().log().all()
				.statusCode(200)
				.extract()
				.body()
				.jsonPath()
				.getObject("data.token", TokenResponse.class);

			// when, then
			WithdrawRequest request = new WithdrawRequest(
				tokens.refreshToken(),
				List.of("서비스가 마음에 들지 않아요"),
				"사용성이 불편해요"
			);

			given().log().all()
				.contentType(ContentType.JSON)
				.header("Authorization", "Bearer " + tokens.accessToken())
				.body(request)
				.when().post("/api/auth/withdraw")
				.then().log().all()
				.statusCode(200);

			User withdrawUser = userRepository.findById(user.getId())
				.get();

			assertThat(withdrawUser.getWithdrawReasons()).isEqualTo(request.toWithdrawReasons());
		}

		@Test
		void 회원탈퇴_요청시_성공적으로_처리되고_사용자_정보에_탈퇴일자가_설정된다() {
			// given
			User user = userFixture.create();
			Long userId = user.getId();
			BDDMockito.given(userClientComposite.fetch(any(OAuthType.class), anyString()))
				.willReturn(user);

			// 카드 생성
			cardFixture.creator().user(user).create();

			TokenResponse tokens = given().log().all()
				.contentType(ContentType.JSON)
				.when().post("/api/auth/login/KAKAO?code=code")
				.then().log().all()
				.statusCode(200)
				.extract()
				.body()
				.jsonPath()
				.getObject("data.token", TokenResponse.class);

			// when
			WithdrawRequest request = new WithdrawRequest(
				tokens.refreshToken(),
				List.of("서비스가 마음에 들지 않아요"),
				"사용성이 불편해요"
			);
			given().log().all()
				.contentType(ContentType.JSON)
				.header("Authorization", "Bearer " + tokens.accessToken())
				.body(request)
				.when().post("/api/auth/withdraw")
				.then().log().all()
				.statusCode(200);

			// then
			User withdrawnUser = userRepository.findById(userId).orElseThrow();
			assertThat(withdrawnUser.getDeletedAt()).isNotNull();
			assertThat(withdrawnUser.getWithdrawReasons()).isNotNull();
			assertThat(withdrawnUser.getWithdrawReasons().reasons()).isNotNull();

			// 리프레시 토큰도 무효화 됐는지 확인
			RefreshTokenRequest refreshRequest = new RefreshTokenRequest(tokens.refreshToken());
			given().log().all()
				.contentType(ContentType.JSON)
				.body(refreshRequest)
				.when().post("/api/auth/refresh")
				.then().log().all()
				.statusCode(401);
		}
	}
}
