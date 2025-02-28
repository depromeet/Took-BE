package com.evenly.took.feature.auth.client;

import com.evenly.took.feature.auth.domain.OAuthType;

public interface AuthCodeRequestUrlProvider {

	OAuthType supportType();

	String provide();
}
