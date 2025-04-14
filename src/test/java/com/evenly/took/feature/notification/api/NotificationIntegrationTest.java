package com.evenly.took.feature.notification.api;

import static io.restassured.RestAssured.*;
import static org.assertj.core.api.AssertionsForClassTypes.*;

import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.evenly.took.feature.notification.domain.NotificationType;
import com.evenly.took.feature.notification.dto.NotificationResponse;
import com.evenly.took.global.integration.JwtMockIntegrationTest;

public class NotificationIntegrationTest extends JwtMockIntegrationTest {

	@Nested
	class 알림_목록_조회 {

		@Test
		void 알림_목록을_조회한다() {
			notificationFixture.creator()
				.user(mockUser)
				.type(NotificationType.MEMO)
				.create();

			List<NotificationResponse> response = given().log().all()
				.header("Authorization", authToken)
				.when().get("/api/notification")
				.then().log().all()
				.statusCode(200)
				.extract()
				.jsonPath()
				.getList("data.notifications", NotificationResponse.class);

			assertThat(response.get(0).title()).isEqualTo("오늘 공유한 명함을 특별하게 만들어 볼까요?");
			assertThat(response.get(0).body()).isEqualTo("다음 만남이 훨씬 자연스러워질 거예요");
			assertThat(response.get(0).link()).isEqualTo("/card-notes");
		}
	}
}
