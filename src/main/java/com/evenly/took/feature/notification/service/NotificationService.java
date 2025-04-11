package com.evenly.took.feature.notification.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.evenly.took.feature.card.dao.CardRepository;
import com.evenly.took.feature.card.domain.Card;
import com.evenly.took.feature.card.domain.CardInterest;
import com.evenly.took.feature.card.domain.ReceivedCard;
import com.evenly.took.feature.notification.dao.NotificationRepository;
import com.evenly.took.feature.notification.domain.Notification;
import com.evenly.took.feature.notification.domain.NotificationSendTime;
import com.evenly.took.feature.notification.domain.NotificationType;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class NotificationService {

	private final NotificationRepository notificationRepository;
	private final CardRepository cardRepository;

	@Transactional
	public void reserveNotification(ReceivedCard receivedCard) {
		List<Card> myCards = cardRepository.findAllByUserIdAndDeletedAtIsNull(receivedCard.getUser().getId());
		List<CardInterest> myCardsInterest = myCards.stream()
			.map(CardInterest::from)
			.toList();
		// TODO: 대표명함 기능 추가되면 위 로직 대표명함으로 변경하기
		NotificationType type = NotificationType.asNotificationType(
			myCardsInterest,
			CardInterest.from(receivedCard.getCard()));
		LocalDateTime willSendAt = NotificationSendTime.from(receivedCard.getCreatedAt()).willSendAt();
		Notification notification = new Notification(receivedCard.getUser(), receivedCard, type, willSendAt);
		notificationRepository.save(notification);
	}
}
