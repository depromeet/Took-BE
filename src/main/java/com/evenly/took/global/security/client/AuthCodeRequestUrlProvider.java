package com.evenly.took.global.security.client;

import com.evenly.took.feature.auth.domain.OAuthType;

public interface AuthCodeRequestUrlProvider {

	OAuthType supportType();

	String provide();
}
