package com.evenly.took.global.domain;

import org.springframework.test.util.ReflectionTestUtils;

import com.evenly.took.domain.auth.domain.OAuthIdentifier;
import com.evenly.took.domain.auth.domain.OAuthType;
import com.evenly.took.domain.user.domain.User;

public class TestUserFactory {

	public static User createMockGoogleUser() {
		OAuthIdentifier oauthIdentifier = OAuthIdentifier.builder()
			.oauthId("google-oauth-id")
			.oauthType(OAuthType.GOOGLE)
			.build();
		return createMockUser("testUser", oauthIdentifier);
	}

	public static User createMockUser(String name, OAuthIdentifier oauthIdentifier) {
		User mcokUser = User.builder()
			.oauthIdentifier(oauthIdentifier)
			.name(name)
			.build();
		ReflectionTestUtils.setField(mcokUser, "id", 1L);
		return mcokUser;
	}
}
