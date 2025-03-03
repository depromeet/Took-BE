package com.evenly.took.feature.auth.api;

import static io.restassured.RestAssured.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.evenly.took.feature.auth.client.UserClientComposite;
import com.evenly.took.feature.auth.domain.OAuthType;
import com.evenly.took.feature.user.domain.User;
import com.evenly.took.global.domain.TestUserFactory;
import com.evenly.took.global.integration.IntegrationTest;

class AppleAuthIntegrationTest extends IntegrationTest {

	@MockitoBean
	UserClientComposite userClientComposite;

	@Test
	void 애플_로그인시_사용자_정보를_반환한다() {
		// given
		User user = TestUserFactory.createMockUser("애플사용자");
		given(userClientComposite.fetch(any(OAuthType.class), anyString()))
				.willReturn(user);

		// when, then
		given().log().all()
				.when().post("/api/auth/login/APPLE?code=test_code")
				.then().log().all()
				.statusCode(200);
	}
}