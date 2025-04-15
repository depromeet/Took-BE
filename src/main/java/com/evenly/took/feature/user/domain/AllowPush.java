package com.evenly.took.feature.user.domain;

import java.util.List;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AllowPush {

	@ColumnDefault("false")
	@Column(name = "allow_push_notification", nullable = false)
	private boolean allowPushNotification;

	@Column(name = "allow_push_content")
	@JdbcTypeCode(SqlTypes.JSON)
	private List<AllowPushContent> allowPushContent;

	@Builder
	public AllowPush(boolean allowPushNotification, List<AllowPushContent> allowPushContent) {
		this.allowPushNotification = allowPushNotification;
		this.allowPushContent = allowPushContent;
	}
}
