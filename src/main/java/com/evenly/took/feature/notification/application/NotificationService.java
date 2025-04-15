package com.evenly.took.feature.notification.application;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.evenly.took.feature.card.application.CardService;
import com.evenly.took.feature.card.domain.Card;
import com.evenly.took.feature.card.domain.ReceivedCard;
import com.evenly.took.feature.notification.dao.NotificationRepository;
import com.evenly.took.feature.notification.domain.Notification;
import com.evenly.took.feature.notification.domain.NotificationTimeRange;
import com.evenly.took.feature.notification.domain.NotificationType;
import com.evenly.took.feature.notification.domain.UserNotification;
import com.evenly.took.feature.notification.dto.NotificationsResponse;
import com.evenly.took.feature.user.dao.UserDeviceRepository;
import com.evenly.took.feature.user.domain.User;
import com.evenly.took.feature.user.domain.UserDevice;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class NotificationService {

	private final NotificationRepository notificationRepository;
	private final UserDeviceRepository userDeviceRepository;
	private final CardService cardService;
	private final NotificationEventPublisher notificationEventPublisher;

	public NotificationsResponse findNotifications(User user) {
		List<Notification> notifications = notificationRepository.findAllByUser(user);
		return NotificationsResponse.from(notifications);
	}

	@Transactional
	public void sendNotification(LocalDateTime sendAt) {
		List<ReceivedCard> receivedCards = fetchReceivedCards(sendAt);
		if (receivedCards.isEmpty()) {
			return;
		}
		List<UserNotification> notifications = sendExpoNotification(receivedCards, sendAt);
		notificationEventPublisher.publishSendNotificationEvent(notifications);
	}

	private List<ReceivedCard> fetchReceivedCards(LocalDateTime sendAt) {
		NotificationTimeRange timeRange = NotificationTimeRange.from(sendAt);
		LocalDateTime from = timeRange.startAt();
		LocalDateTime to = timeRange.endAt();
		return cardService.findReceivedCardsCreatedBetween(from, to);
	}

	private List<UserNotification> sendExpoNotification(List<ReceivedCard> receivedCards, LocalDateTime sendAt) {
		Map<User, List<Card>> cardsByUser = groupCardsByUser(receivedCards);
		return cardsByUser.entrySet().stream()
			.filter(entry -> entry.getKey().isAllowPushNotification())
			.flatMap(entry -> generateNotifications(entry.getKey(), entry.getValue(), sendAt).stream())
			.toList();
	}

	private Map<User, List<Card>> groupCardsByUser(List<ReceivedCard> receivedCards) {
		return receivedCards.stream()
			.collect(Collectors.groupingBy(
				ReceivedCard::getUser,
				Collectors.mapping(ReceivedCard::getCard, Collectors.toList())
			));
	}

	private List<UserNotification> generateNotifications(User user, List<Card> cards, LocalDateTime sendAt) {
		Card primaryCard = cardService.findPrimaryCard(user);
		NotificationType type = NotificationType.asNotificationType(primaryCard, cards);
		saveNotification(user, type, sendAt);
		List<UserDevice> userDevices = userDeviceRepository.findByUser(user);
		return userDevices.stream()
			.map(UserDevice::getExpoToken)
			.map(token -> new UserNotification(token, type))
			.toList();
	}

	private void saveNotification(User user, NotificationType type, LocalDateTime sendAt) {
		Notification notification = Notification.builder()
			.user(user)
			.type(type)
			.sendAt(sendAt)
			.build();
		notificationRepository.save(notification);
	}
}
