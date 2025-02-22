package com.evenly.took.feature.user.domain;

import com.evenly.took.feature.auth.domain.OAuthIdentifier;
import com.evenly.took.feature.common.model.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users", uniqueConstraints = {
	@UniqueConstraint(
		name = "oauth_identifier_unique",
		columnNames = {"oauth_id", "oauth_type"}
	)
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class User extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Embedded
	private OAuthIdentifier oauthIdentifier;

	@Column(name = "name")
	@NotNull
	private String name;

	@Builder
	public User(OAuthIdentifier oauthIdentifier, String name) {
		this.oauthIdentifier = oauthIdentifier;
		this.name = name;
	}
}
