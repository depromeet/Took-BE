package com.evenly.took.global.security.client;

import com.evenly.took.feature.auth.domain.OAuthType;
import com.evenly.took.feature.user.domain.User;

public interface UserClient {

	OAuthType supportType();

	User fetch(String authCode);
}
