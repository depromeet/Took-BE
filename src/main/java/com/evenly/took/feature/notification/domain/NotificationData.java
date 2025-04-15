package com.evenly.took.feature.notification.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class NotificationData {

	private static final NotificationData MEMO_DATA = new NotificationData(
		"오늘 공유한 명함을 특별하게 만들어 볼까요?",
		"다음 만남이 훨씬 자연스러워질 거예요",
		"/card-notes"
	);
	private static final NotificationData INTERESTING_DATA = new NotificationData(
		"방금 나와 공통점이 같은 사람을 발견했어요!",
		"어떤 사람인지 살펴보세요",
		"/received/interesting"
	);

	private final String title;
	private final String body;
	private final String link;

	public static NotificationData from(NotificationType notificationType) {
		return switch (notificationType) {
			case MEMO -> MEMO_DATA;
			case INTERESTING -> INTERESTING_DATA;
		};
	}
}
