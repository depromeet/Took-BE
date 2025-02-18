package com.evenly.took.domain.user.domain;

import com.evenly.took.domain.auth.domain.OAuthId;
import com.evenly.took.domain.common.model.BaseTimeEntity;

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
		name = "oauth_id_unique",
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
	private OAuthId oauthId;

	@Column(name = "user_name")
	private String name;

	@Column(name = "user_profile_image")
	private String profileImage;

	@Builder
	public User(OAuthId oauthId, String name, String profileImage) {
		this.oauthId = oauthId;
		this.name = name;
		this.profileImage = profileImage;
	}
}
