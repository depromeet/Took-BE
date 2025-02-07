package com.evenly.blok.global.health;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import com.evenly.blok.global.integration.IntegrationTest;

class HealthControllerTest extends IntegrationTest {

	@Test
	void Health_Check_통합테스트_성공() throws Exception {

		// given, when
		ResultActions resultActions = requestHealthCheck();

		// then
		resultActions
			.andExpect(status().isOk());
	}

	private ResultActions requestHealthCheck() throws Exception {
		return mvc.perform(get("/api/health")
			.contentType(MediaType.APPLICATION_JSON));
	}
}