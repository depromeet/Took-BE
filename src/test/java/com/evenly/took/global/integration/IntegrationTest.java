package com.evenly.took.global.integration;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import com.evenly.took.global.helper.DatabaseManager;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.restassured.RestAssured;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class IntegrationTest extends DatabaseManager {

	@LocalServerPort
	protected int port;

	@Autowired
	protected ObjectMapper objectMapper;

	@BeforeEach
	public void setUp() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = port;
	}
}
