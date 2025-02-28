package com.evenly.took.feature.auth.client;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.evenly.took.feature.auth.domain.OAuthType;
import com.evenly.took.feature.auth.exception.AuthErrorCode;
import com.evenly.took.feature.common.exception.TookException;

@Component
public class AuthCodeRequestUrlProviderComposite {

	private final Map<OAuthType, AuthCodeRequestUrlProvider> mapping;

	public AuthCodeRequestUrlProviderComposite(Set<AuthCodeRequestUrlProvider> providers) {
		this.mapping = providers.stream()
			.collect(Collectors.toUnmodifiableMap(AuthCodeRequestUrlProvider::supportType, Function.identity()));
	}

	public String provide(OAuthType oauthType) {
		return Optional.ofNullable(mapping.get(oauthType))
			.orElseThrow(() -> new TookException(AuthErrorCode.OAUTH_TYPE_NOT_FOUND))
			.provide();
	}
}
