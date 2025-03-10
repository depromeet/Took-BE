package com.evenly.took.global.integration;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.evenly.took.feature.auth.application.TokenProvider;
import com.evenly.took.feature.user.domain.User;
import com.evenly.took.global.auth.resolver.LoginUserArgumentResolver;
import com.evenly.took.global.domain.TestUserFactory;

public abstract class JwtMockIntegrationTest extends IntegrationTest {

	@MockitoBean
	protected LoginUserArgumentResolver loginUserArgumentResolver;

	@MockitoBean
	protected TokenProvider tokenProvider;

	protected String authToken;
	protected User mockUser;

	@BeforeEach
	public void setUp() {
		super.setUp();

		// JWT Mocking
		mockUser = TestUserFactory.createMockGoogleUser();
		authToken = "Bearer test-token";
		when(loginUserArgumentResolver.supportsParameter(any())).thenReturn(true);
		when(loginUserArgumentResolver.resolveArgument(any(), any(), any(), any())).thenReturn(mockUser);
		doNothing().when(tokenProvider).validateAccessToken(anyString());
		when(tokenProvider.getUserIdFromAccessToken(anyString())).thenReturn(mockUser.getId().toString());
	}
}
