package com.evenly.took.feature.auth.client.authcode;

import static com.evenly.took.feature.auth.client.authcode.AuthCodeRequestUrlProviderCompositeTest.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.evenly.took.feature.auth.domain.OAuthType;
import com.evenly.took.global.exception.auth.oauth.OAuthErrorCode;
import com.evenly.took.global.exception.auth.oauth.OAuthTypeNotFoundException;
import com.evenly.took.global.security.client.AuthCodeRequestUrlProvider;
import com.evenly.took.global.security.client.AuthCodeRequestUrlProviderComposite;
import com.evenly.took.global.service.MockTest;

class AuthCodeRequestUrlProviderCompositeTest extends MockTest {

	static final String TEST_URL = "https://oauth.test";

	private AuthCodeRequestUrlProviderComposite composite;
	private final AuthCodeRequestUrlProvider mockGoogleAuthRequestUrlProvider = new MockGoogleAuthCodeRequestUrlProvider();

	@BeforeEach
	void setUp() {
		composite = new AuthCodeRequestUrlProviderComposite(Set.of(mockGoogleAuthRequestUrlProvider));
	}

	@Test
	void 유효한OAuthType_URL_정상_반환() {
		// given, when
		String url = composite.provide(OAuthType.GOOGLE);

		// then
		assertThat(url).isEqualTo(TEST_URL);
	}

	@Test
	void 유효하지않은OAuthType_예외() {
		// given
		OAuthType invalidOAuthType = OAuthType.KAKAO;

		// when
		OAuthTypeNotFoundException exception = assertThrows(OAuthTypeNotFoundException.class, () -> {
			composite.provide(invalidOAuthType);
		});

		// then
		assertThat(exception.getErrorCode()).isEqualTo(OAuthErrorCode.OAUTH_TYPE_NOT_FOUND);
	}
}

class MockGoogleAuthCodeRequestUrlProvider implements AuthCodeRequestUrlProvider {
	@Override
	public OAuthType supportType() {
		return OAuthType.GOOGLE;
	}

	@Override
	public String provide() {
		return TEST_URL;
	}
}
