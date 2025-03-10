package com.evenly.took.global.integration;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.evenly.took.feature.auth.application.TokenProvider;
import com.evenly.took.feature.user.domain.User;
import com.evenly.took.global.auth.resolver.LoginUserArgumentResolver;
import com.evenly.took.global.config.testcontainers.RedisTestConfig;
import com.evenly.took.global.domain.TestUserFactory;
import com.evenly.took.global.helper.RedisCleaner;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.restassured.RestAssured;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(RedisTestConfig.class)
public abstract class IntegrationTest {

	@LocalServerPort
	protected int port;

	@Autowired
	protected ObjectMapper objectMapper;

	@Autowired
	protected RedisCleaner redisCleaner;

	@MockitoBean
	protected LoginUserArgumentResolver loginUserArgumentResolver;

	@MockitoBean
	protected TokenProvider tokenProvider;

	protected String authToken;
	protected User mockUser;

	@BeforeEach
	public void setUp() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = port;

		// JWT Mocking
		mockUser = TestUserFactory.createMockGoogleUser();
		authToken = "Bearer test-token";
		when(loginUserArgumentResolver.supportsParameter(any())).thenReturn(true);
		when(loginUserArgumentResolver.resolveArgument(any(), any(), any(), any())).thenReturn(mockUser);
		doNothing().when(tokenProvider).validateAccessToken(anyString());
		when(tokenProvider.getUserIdFromAccessToken(anyString())).thenReturn(mockUser.getId().toString());
		redisCleaner.deleteAllKeys();
	}
}
