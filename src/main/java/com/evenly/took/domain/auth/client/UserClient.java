package com.evenly.took.domain.auth.client;

import com.evenly.took.domain.auth.domain.OAuthType;
import com.evenly.took.domain.user.domain.User;

public interface UserClient {

	OAuthType supportType();

	User fetch(String authCode);
}
