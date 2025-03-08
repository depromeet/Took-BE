package com.evenly.took.feature.auth.client;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.evenly.took.feature.auth.domain.OAuthType;
import com.evenly.took.feature.auth.exception.AuthErrorCode;
import com.evenly.took.feature.user.domain.User;
import com.evenly.took.global.exception.TookException;

@Component
public class UserClientComposite {

	private final Map<OAuthType, UserClient> mapping;

	public UserClientComposite(Set<UserClient> clients) {
		mapping = clients.stream()
			.collect(Collectors.toMap(UserClient::supportType, Function.identity()));
	}

	public User fetch(OAuthType oauthType, AuthContext context) {
		return Optional.ofNullable(mapping.get(oauthType))
			.orElseThrow(() -> new TookException(AuthErrorCode.OAUTH_TYPE_NOT_FOUND))
			.fetch(context);
	}

	public User fetch(OAuthType oauthType, String authCode) {
		AuthContext context = new AuthContext(authCode);
		return fetch(oauthType, context);
	}
}
