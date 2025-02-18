package com.evenly.took.domain.auth.client.authcode;

import static com.evenly.took.global.exception.auth.oauth.OAuthErrorCode.*;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.evenly.took.domain.auth.domain.OAuthType;
import com.evenly.took.global.exception.auth.oauth.OAuthTypeNotFoundException;

@Component
public class AuthCodeRequestUrlProviderComposite {

	private final Map<OAuthType, AuthCodeRequestUrlProvider> mapping;

	public AuthCodeRequestUrlProviderComposite(Set<AuthCodeRequestUrlProvider> providers) {
		mapping = providers.stream()
			.collect(Collectors.toMap(AuthCodeRequestUrlProvider::supportType, Function.identity()));
	}

	public String provide(OAuthType oauthType) {
		return Optional.ofNullable(mapping.get(oauthType))
			.orElseThrow(() -> new OAuthTypeNotFoundException(OAUTH_TYPE_NOT_FOUND))
			.provide();
	}
}
