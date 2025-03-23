package com.evenly.took.feature.card.domain;

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
@Table(name = "folders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Folder extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;

	@Builder
	public Folder(LocalDateTime deletedAt, String name, User user) {
		this.deletedAt = deletedAt;
		this.name = name;
		this.user = user;
	}

	public void updateName(String name) {
		this.name = name;
	}

	public void softDelete() {
		this.deletedAt = LocalDateTime.now();
	}
}
