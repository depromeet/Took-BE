package com.evenly.took.feature.user.domain;

import java.time.LocalDateTime;

import com.evenly.took.feature.common.model.BaseTimeEntity;

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

	@Column(name = "expo_token", unique = true, nullable = false)
	private String expoToken;

	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;

	@Builder
	public UserDevice(User user, String expoToken) {
		this.user = user;
		this.expoToken = expoToken;
		this.deletedAt = null;
	}
}
