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
@Table(name = "received_cards")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ReceivedCard extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "card_id")
	private Card card;

	@Column(name = "memo")
	private String memo;

	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;

	@Builder
	public ReceivedCard(User user, Card card, String memo, LocalDateTime deletedAt) {
		this.user = user;
		this.card = card;
		this.memo = memo;
		this.deletedAt = deletedAt;
	}

	public void updateMemo(String memo) {
		this.memo = memo;
	}

	public void softDelete() {
		this.deletedAt = LocalDateTime.now();
	}
}
