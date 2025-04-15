package com.evenly.took.global.integration;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.evenly.took.feature.auth.application.TokenProvider;
import com.evenly.took.feature.user.domain.User;
import com.evenly.took.global.auth.resolver.LoginUserArgumentResolver;

public abstract class JwtMockIntegrationTest extends IntegrationTest {

	@MockitoBean
	protected LoginUserArgumentResolver loginUserArgumentResolver;

	@MockitoBean
	protected TokenProvider tokenProvider;

	protected String authToken;
	protected User mockUser;

	// protected Card mockCard;

	@BeforeEach
	public void setUp() {
		super.setUp();

		mockUser = userFixture.create();

		String userId = mockUser.getId().toString();
		authToken = "Bearer token-for-user-" + userId;

		when(loginUserArgumentResolver.supportsParameter(any())).thenReturn(true);
		when(loginUserArgumentResolver.resolveArgument(any(), any(), any(), any()))
			.thenReturn(mockUser);

		doNothing().when(tokenProvider).validateAccessToken(anyString());

		when(tokenProvider.getUserIdFromAccessToken(anyString()))
			.thenAnswer(invocation -> {
				String token = invocation.getArgument(0, String.class);
				if (token.contains("token-for-user-")) {
					return token.replace("Bearer token-for-user-", "");
				}
				return mockUser.getId().toString();
			});
	}

	protected String generateTokenFor(User user) {
		String token = "Bearer token-for-user-" + user.getId();
		when(tokenProvider.getUserIdFromAccessToken(eq(token))).thenReturn(user.getId().toString());
		when(loginUserArgumentResolver.resolveArgument(any(), any(), any(), any())).thenReturn(user);
		return token;
	}
}
