package com.evenly.took.global.domain;

import org.springframework.test.util.ReflectionTestUtils;

import com.evenly.took.domain.auth.domain.OAuthId;
import com.evenly.took.domain.auth.domain.OAuthType;
import com.evenly.took.domain.user.domain.User;

public class TestUserFactory {

	public static User createMockGoogleUser() {
		OAuthId oAuthId = OAuthId.builder()
			.oauthId("google-oauth-id")
			.oauthType(OAuthType.GOOGLE)
			.build();
		return createMockUser("testUser", oAuthId, "testProfile");
	}

	public static User createMockUser(String name, OAuthId oauthId, String profileImage) {
		User mcokUser = User.builder()
			.oauthId(oauthId)
			.name(name)
			.profileImage(profileImage)
			.build();
		ReflectionTestUtils.setField(mcokUser, "id", 1L);
		return mcokUser;
	}
}
