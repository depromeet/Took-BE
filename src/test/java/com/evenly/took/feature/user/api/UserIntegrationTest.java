package com.evenly.took.feature.user.api;

import static io.restassured.RestAssured.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.evenly.took.feature.user.dto.request.AllowNotificationRequest;
import com.evenly.took.feature.user.dto.response.AllowNotificationResponse;
import com.evenly.took.global.integration.JwtMockIntegrationTest;

import io.restassured.http.ContentType;

public class UserIntegrationTest extends JwtMockIntegrationTest {

	@Nested
	class 알림_설정_조회 {

		@Test
		void 알림_설정을_조회한다() {
			given().log().all()
				.header("Authorization", authToken)
				.when().get("/api/user/notification-allow")
				.then().log().all()
				.statusCode(200);
		}
	}

	@Nested
	class 알림_설정_수정 {

		@Test
		void 알림_설정을_수정한다() {
			// given
			boolean isAllowPushNotification = true;
			List<String> allowPushContent = List.of("흥미로운 명함 알림", "한 줄 메모 알림");

			// when
			AllowNotificationRequest request = new AllowNotificationRequest(isAllowPushNotification, allowPushContent);
			given().log().all()
				.contentType(ContentType.JSON)
				.header("Authorization", authToken)
				.body(request)
				.when().put("/api/user/notification-allow")
				.then().log().all()
				.statusCode(200);

			// then
			AllowNotificationResponse response = given().log().all()
				.header("Authorization", authToken)
				.when().get("/api/user/notification-allow")
				.then().log().all()
				.statusCode(200)
				.extract()
				.jsonPath()
				.getObject("data", AllowNotificationResponse.class);
			assertThat(response.isAllowPushNotification()).isEqualTo(isAllowPushNotification);
			assertThat(response.allowPushContent()).isEqualTo(allowPushContent);
		}
	}
}
