package com.evenly.took.feature.notification.domain;

import java.time.LocalDateTime;

import com.evenly.took.feature.card.domain.ReceivedCard;
import com.evenly.took.feature.common.model.BaseTimeEntity;
import com.evenly.took.feature.user.domain.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "notifications")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Notification extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user; // TODO receivedCard.user와 중복 고려

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "received_card_id")
	private ReceivedCard receivedCard;

	@Column(name = "type", nullable = false)
	@Enumerated(EnumType.STRING)
	private NotificationType type;

	@Column(name = "will_send_at", nullable = false)
	private LocalDateTime willSendAt;

	@Column(name = "send_at")
	private LocalDateTime sendAt;

	@Builder
	public Notification(User user, ReceivedCard receivedCard, NotificationType type, LocalDateTime willSendAt) {
		this.user = user;
		this.receivedCard = receivedCard;
		this.type = type;
		this.willSendAt = willSendAt;
		this.sendAt = null;
	}
}
