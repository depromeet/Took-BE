package com.evenly.took.feature.auth.api;

import static io.restassured.RestAssured.*;
import static org.mockito.ArgumentMatchers.*;

import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.evenly.took.feature.auth.domain.OAuthType;
import com.evenly.took.feature.auth.dto.response.AuthResponse;
import com.evenly.took.feature.user.domain.User;
import com.evenly.took.global.domain.TestUserFactory;
import com.evenly.took.global.integration.IntegrationTest;
import com.evenly.took.global.security.client.UserClientComposite;

public class OAuthIntegrationTest extends IntegrationTest {

	@MockitoBean
	UserClientComposite userClientComposite;

	@Test
	void 로그인시_사용자_정보를_반환한다() {
		User user = TestUserFactory.createMockUser("took");
		BDDMockito.given(userClientComposite.fetch(any(OAuthType.class), anyString()))
			.willReturn(user);

		given().log().all()
			.when().get("/api/oauth/login/KAKAO?code=code")
			.then().log().all()
			.statusCode(200);
	}

	@Test
	void 유효하지_않은_액세스_토큰과_유효하지_않은_리프레쉬_토큰일_경우_401_예외를_반환한다() {
		User user = TestUserFactory.createMockUser("took");
		BDDMockito.given(userClientComposite.fetch(any(OAuthType.class), anyString()))
			.willReturn(user);

		given().log().all()
			.header("Authorization", "Bearer invalid.access.token invalid-refresh-token")
			.when().get("/api/test")
			.then().log().all()
			.statusCode(401);
	}

	@Test
	void 유효하지_않은_액세스_토큰과_유효한_리프레쉬_토큰일_경우_액세스_토큰을_갱신한다() {
		User user = TestUserFactory.createMockUser("took");
		BDDMockito.given(userClientComposite.fetch(any(OAuthType.class), anyString()))
			.willReturn(user);

		AuthResponse response = given().log().all()
			.when().get("/api/oauth/login/KAKAO?code=code")
			.then().log().all()
			.statusCode(200)
			.extract()
			.body()
			.jsonPath()
			.getObject("data", AuthResponse.class);

		given().log().all()
			.header("Authorization", "Bearer %s %s".formatted("invalid", response.token().refreshToken()))
			.when().get("/api/test")
			.then().log().all()
			.statusCode(200);
	}

	@Test
	void 유효한_액세스_토큰과_유효한_리프레쉬_토큰일_경우_예외를_반환하지_않는다() {
		User user = TestUserFactory.createMockUser("took");
		BDDMockito.given(userClientComposite.fetch(any(OAuthType.class), anyString()))
			.willReturn(user);

		AuthResponse response = given().log().all()
			.when().get("/api/oauth/login/KAKAO?code=code")
			.then().log().all()
			.statusCode(200)
			.extract()
			.body()
			.jsonPath()
			.getObject("data", AuthResponse.class);

		given().log().all()
			.header("Authorization",
				"Bearer %s %s".formatted(response.token().accessToken(), response.token().refreshToken()))
			.when().get("/api/test")
			.then().log().all()
			.statusCode(200);
	}
}
