package com.evenly.took.domain.auth.client;

import static com.evenly.took.global.domain.TestUserFactory.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.evenly.took.domain.auth.domain.OAuthIdentifier;
import com.evenly.took.domain.auth.domain.OAuthType;
import com.evenly.took.domain.user.domain.User;
import com.evenly.took.global.exception.auth.oauth.OAuthTypeNotFoundException;
import com.evenly.took.global.service.MockTest;

class UserClientCompositeTest extends MockTest {

	private static User testUser;

	private UserClientComposite composite;
	private final UserClient mockGoogleUserClient = new MockGoogleUserClient();

	@BeforeEach
	void setUp() {
		testUser = createMockGoogleUser();
		composite = new UserClientComposite(Set.of(mockGoogleUserClient));
	}

	@Test
	void 유효한OAuthType_User_정상_반환() {
		// given, when
		User user = composite.fetch(OAuthType.GOOGLE, "testAuthCode");

		// then
		assertThat(user).isNotNull();
		assertThat(user.getName()).isEqualTo(testUser.getName());
	}

	@Test
	void 유효하지않은OAuthType_예외() {
		// given
		OAuthType invalidOAuthType = OAuthType.KAKAO;

		// when
		OAuthTypeNotFoundException exception = assertThrows(OAuthTypeNotFoundException.class, () -> {
			composite.fetch(invalidOAuthType, "testAuthCode");
		});

		// then
		assertThat(exception.getErrorCode()).isNotNull();
	}

	static class MockGoogleUserClient implements UserClient {

		@Override
		public OAuthType supportType() {
			return OAuthType.GOOGLE;
		}

		@Override
		public User fetch(String authCode) {
			OAuthIdentifier oauthIdentifier = OAuthIdentifier.builder()
				.oauthId("test")
				.oauthType(OAuthType.GOOGLE)
				.build();
			return User.builder()
				.oauthIdentifier(oauthIdentifier)
				.name(testUser.getName())
				.build();
		}
	}
}
