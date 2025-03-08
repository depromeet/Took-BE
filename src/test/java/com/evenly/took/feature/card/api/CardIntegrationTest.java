package com.evenly.took.feature.card.api;

import static io.restassured.RestAssured.*;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.evenly.took.global.integration.IntegrationTest;

public class CardIntegrationTest extends IntegrationTest {

	@Nested
	class 전체_커리어_조회 {

		@Test
		void 직군에_따른_전체_커리어를_조회한다() {
			given().log().all()
				.when().get("/api/card/register?job=DEVELOPER")
				.then().log().all()
				.statusCode(200);
		}

		@Test
		void 제공하지_않는_직군의_커리어를_요청한_경우_400_예외를_반환한다() {
			given().log().all()
				.when().get("/api/card/register?job=INVALID")
				.then().log().all()
				.statusCode(400);
		}
	}
}
