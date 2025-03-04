package com.evenly.took.feature.auth.api;

import static io.restassured.RestAssured.*;
import static org.mockito.ArgumentMatchers.*;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.evenly.took.feature.auth.client.UserClientComposite;
import com.evenly.took.feature.auth.domain.OAuthType;
import com.evenly.took.feature.auth.dto.request.RefreshTokenRequest;
import com.evenly.took.feature.auth.dto.response.TokenResponse;
import com.evenly.took.feature.user.domain.User;
import com.evenly.took.global.domain.TestUserFactory;
import com.evenly.took.global.integration.IntegrationTest;

import io.restassured.http.ContentType;

public class AuthIntegrationTest extends IntegrationTest {

	@MockitoBean
	UserClientComposite userClientComposite;

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
			User user = TestUserFactory.createMockUser("took");
			BDDMockito.given(userClientComposite.fetch(any(OAuthType.class), anyString()))
				.willReturn(user);

			given().log().all()
				.when().post("/api/auth/login/KAKAO?code=code")
				.then().log().all()
				.statusCode(200);
		}

		@Test
		void 유효하지_않은_액세스_토큰으로_API를_요청할_경우_401_예외를_반환한다() {
			User user = TestUserFactory.createMockUser("took");
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
			User user = TestUserFactory.createMockUser("took");
			BDDMockito.given(userClientComposite.fetch(any(OAuthType.class), anyString()))
				.willReturn(user);

			TokenResponse tokens = given().log().all()
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
			User user = TestUserFactory.createMockUser("took");
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
			User user = TestUserFactory.createMockUser("took");
			BDDMockito.given(userClientComposite.fetch(any(OAuthType.class), anyString()))
				.willReturn(user);

			TokenResponse tokens = given().log().all()
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
}
