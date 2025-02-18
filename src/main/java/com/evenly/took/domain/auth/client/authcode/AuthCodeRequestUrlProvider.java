package com.evenly.took.domain.auth.client.authcode;

import com.evenly.took.domain.auth.domain.OAuthType;

public interface AuthCodeRequestUrlProvider {

	OAuthType supportType();

	String provide();
}
