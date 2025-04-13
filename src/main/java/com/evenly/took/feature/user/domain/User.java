package com.evenly.took.feature.user.domain;

import java.time.LocalDateTime;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.evenly.took.feature.auth.domain.OAuthIdentifier;
import com.evenly.took.feature.card.domain.vo.WithdrawReasons;
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

	@Column(name = "withdraw_reasons")
	@JdbcTypeCode(SqlTypes.JSON)
	private WithdrawReasons withdrawReasons;

	@Embedded
	private AllowPush allowPush;

	@Builder
	public User(OAuthIdentifier oauthIdentifier, String name, String email, LocalDateTime deletedAt,
		WithdrawReasons withdrawReasons, AllowPush allowPush) {
		this.oauthIdentifier = oauthIdentifier;
		this.name = name;
		this.email = email;
		this.deletedAt = deletedAt;
		this.withdrawReasons = withdrawReasons;
		this.allowPush = allowPush;
	}

	public static User toEntity(Long id) {
		User userEntity = new User();
		userEntity.id = id;
		return userEntity;
	}

	public void withdraw(WithdrawReasons withdrawReasons) {
		this.deletedAt = LocalDateTime.now();
		this.oauthIdentifier = null;
		this.withdrawReasons = withdrawReasons;
	}

	public void updateAllowPush(AllowPush allowPush) {
		this.allowPush = allowPush;
	}

	public boolean isAllowPushNotification() {
		return allowPush.isAllowPushNotification();
	}
}
