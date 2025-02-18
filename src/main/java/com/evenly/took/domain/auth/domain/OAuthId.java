package com.evenly.took.domain.auth.domain;

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
public class OAuthId {

	@Column(name = "oauth_id", nullable = false)
	private String oauthId;

	@Enumerated(EnumType.STRING)
	@Column(name = "oauth_type", nullable = false)
	private OAuthType oauthType;

	@Builder
	public OAuthId(String oauthId, OAuthType oauthType) {
		this.oauthId = oauthId;
		this.oauthType = oauthType;
	}
}
