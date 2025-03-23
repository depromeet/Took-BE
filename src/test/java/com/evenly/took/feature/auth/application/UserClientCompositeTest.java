package com.evenly.took.feature.auth.application;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.evenly.took.feature.auth.client.AuthContext;
import com.evenly.took.feature.auth.client.UserClient;
import com.evenly.took.feature.auth.client.UserClientComposite;
import com.evenly.took.feature.auth.domain.OAuthIdentifier;
import com.evenly.took.feature.auth.domain.OAuthType;
import com.evenly.took.feature.user.domain.User;
import com.evenly.took.global.exception.TookException;
import com.evenly.took.global.service.MockTest;

class UserClientCompositeTest extends MockTest {

	static User testUser;

	UserClientComposite composite;
	UserClient stubGoogleUserClient = new StubGoogleUserClient();

	@BeforeEach
	void setUp() {
		testUser = userFactory.create();
		composite = new UserClientComposite(Set.of(stubGoogleUserClient));
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
		TookException exception = assertThrows(TookException.class, () -> {
			composite.fetch(invalidOAuthType, "testAuthCode");
		});

		// then
		assertThat(exception.getErrorCode()).isNotNull();
	}

	static class StubGoogleUserClient implements UserClient {

		@Override
		public OAuthType supportType() {
			return OAuthType.GOOGLE;
		}

		@Override
		public User fetch(AuthContext authContext) {
			OAuthIdentifier oauthIdentifier = OAuthIdentifier.builder()
				.oauthId("test")
				.oauthType(OAuthType.GOOGLE)
				.build();
			return User.builder()
				.oauthIdentifier(oauthIdentifier)
				.name(testUser.getName())
				.email(testUser.getEmail())
				.build();
		}
	}
}
