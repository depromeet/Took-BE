package com.evenly.took.feature.auth.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OAuthIdentifier {

	@Column(name = "oauth_id")
	private String oauthId;

	@Column(name = "oauth_type")
	@Enumerated(EnumType.STRING)
	private OAuthType oauthType;

	@Builder
	public OAuthIdentifier(String oauthId, OAuthType oauthType) {
		this.oauthId = oauthId;
		this.oauthType = oauthType;
	}
}
