package com.evenly.took.feature.auth.client;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import com.evenly.took.feature.auth.client.apple.AppleUserClient;
import com.evenly.took.feature.auth.domain.OAuthType;
import com.evenly.took.feature.user.domain.User;

class AppleUserClientTest {

	@InjectMocks
	private AppleUserClient appleUserClient;

	@Mock
	private RestTemplate restTemplate;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void supportType_애플_반환() {
		// when
		OAuthType oauthType = appleUserClient.supportType();

		// then
		assertThat(oauthType).isEqualTo(OAuthType.APPLE);
	}

	@Test
	void fetch_사용자정보_반환() {
		// given
		String authCode = "test_auth_code";
		AuthContext authContext = new AuthContext(authCode);

		// when
		User user = appleUserClient.fetch(authContext);

		// then
		assertThat(user).isNotNull();
		assertThat(user.getOauthIdentifier().getOauthType()).isEqualTo(OAuthType.APPLE);
	}
}
