package com.evenly.took.global.health;

import static io.restassured.RestAssured.*;

import org.junit.jupiter.api.Test;

import com.evenly.took.global.integration.IntegrationTest;

class HealthControllerIntegrationTest extends IntegrationTest {

	@Test
	void Health_Check_통합_테스트_성공() {
		given().log().all()
			.when()
			.get("/api/health")
			.then()
			.log().all()
			.statusCode(200);
	}
}
