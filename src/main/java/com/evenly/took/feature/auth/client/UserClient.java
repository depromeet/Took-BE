package com.evenly.took.feature.auth.client;

import com.evenly.took.feature.auth.domain.OAuthType;
import com.evenly.took.feature.user.domain.User;

public interface UserClient {

	OAuthType supportType();

	User fetch(String authCode);
}
