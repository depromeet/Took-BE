package com.evenly.took.feature.notification.domain;

import java.time.LocalDateTime;

import com.evenly.took.feature.common.model.BaseTimeEntity;
import com.evenly.took.feature.user.domain.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_devices")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class UserDevice extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@Column(name = "fcm_token", unique = true, nullable = false)
	private String fcmToken;

	@Column(name = "platform", nullable = false)
	private DevicePlatform platform;

	@Column(name = "allow_push_notification", nullable = false)
	private boolean allowPushNotification;

	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;

	@Builder
	public UserDevice(Long id, User user, String fcmToken, DevicePlatform platform) {
		this.id = id;
		this.user = user;
		this.fcmToken = fcmToken;
		this.platform = platform;
		this.allowPushNotification = false;
		this.deletedAt = null;
	}
}
