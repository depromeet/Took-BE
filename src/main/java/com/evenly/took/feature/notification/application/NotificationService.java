package com.evenly.took.feature.notification.application;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.evenly.took.feature.card.dao.CardRepository;
import com.evenly.took.feature.card.dao.ReceivedCardRepository;
import com.evenly.took.feature.card.domain.Card;
import com.evenly.took.feature.card.domain.CardInterest;
import com.evenly.took.feature.card.domain.ReceivedCard;
import com.evenly.took.feature.notification.dao.NotificationRepository;
import com.evenly.took.feature.notification.domain.FcmNotification;
import com.evenly.took.feature.notification.domain.Notification;
import com.evenly.took.feature.notification.domain.NotificationCardsTimeRange;
import com.evenly.took.feature.notification.domain.NotificationType;
import com.evenly.took.feature.user.domain.User;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class NotificationService {

	private final NotificationRepository notificationRepository;
	private final ReceivedCardRepository receivedCardRepository;
	private final CardRepository cardRepository;
	private final FcmEventPublisher fcmEventPublisher;

	@Transactional
	public void sendNotification(LocalDateTime sendAt) {
		List<ReceivedCard> receivedCards = fetchReceivedCards(sendAt);
		if (receivedCards.isEmpty()) {
			return;
		}
		Map<User, List<Card>> receivedCardsGroupingByUser = groupReceivedCardByUser(receivedCards);
		sendFcmNotification(receivedCardsGroupingByUser, sendAt);
	}

	private List<ReceivedCard> fetchReceivedCards(LocalDateTime sendAt) {
		NotificationCardsTimeRange timeRange = NotificationCardsTimeRange.from(sendAt);
		LocalDateTime from = timeRange.startAt();
		LocalDateTime to = timeRange.endAt();
		return receivedCardRepository.findAllByCreatedAtAndDeletedAtIsNull(from, to);
	}

	private Map<User, List<Card>> groupReceivedCardByUser(List<ReceivedCard> receivedCards) {
		return receivedCards.stream()
			.collect(Collectors.groupingBy(
				ReceivedCard::getUser,
				Collectors.mapping(ReceivedCard::getCard, Collectors.toList())
			));
	}

	private void sendFcmNotification(Map<User, List<Card>> receivedCardsGroupingByUser, LocalDateTime sendAt) {
		List<FcmNotification> fcmNotifications = receivedCardsGroupingByUser.entrySet().stream()
			.filter(entry -> entry.getKey().isAllowPushNotification())
			.map(entry -> createFcmNotification(entry.getKey(), entry.getValue(), sendAt))
			.toList();
		fcmEventPublisher.publishFcmSendEvent(fcmNotifications);
	}

	private FcmNotification createFcmNotification(User user, List<Card> cards, LocalDateTime sendAt) {
		CardInterest myCard = new CardInterest("세부직군", "소속정보", List.of("도메인")); // TODO 대표명함 조회로 변경
		List<CardInterest> receivedCards = cards.stream()
			.map(CardInterest::from)
			.toList();
		NotificationType type = NotificationType.asNotificationType(myCard, receivedCards);
		notificationRepository.save(new Notification(user, type, sendAt));
		return new FcmNotification(user, type);
	}
}
