package com.evenly.took.feature.user.domain;

import java.time.LocalDateTime;

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

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "email", nullable = false)
	private String email;

	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;

	@Builder
	public User(OAuthIdentifier oauthIdentifier, String name, String email, LocalDateTime deletedAt) {
		this.oauthIdentifier = oauthIdentifier;
		this.name = name;
		this.email = email;
		this.deletedAt = deletedAt;
	}

	public static User toEntity(Long id) {
		User userEntity = new User();
		userEntity.id = id;
		return userEntity;
	}

	public void withdraw() {
		this.deletedAt = LocalDateTime.now();
	}
}
