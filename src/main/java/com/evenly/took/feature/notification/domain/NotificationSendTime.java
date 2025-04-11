package com.evenly.took.feature.notification.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NotificationSendTime {

	private static final LocalTime MORNING_THRESHOLD = LocalTime.of(6, 0);
	private static final LocalTime NIGHT_THRESHOLD = LocalTime.of(21, 0);
	private static final LocalTime MORNING_SEND_TIME = LocalTime.of(10, 0);
	private static final LocalTime NIGHT_SEND_TIME = LocalTime.of(22, 0);

	private final LocalDate receivedAtDate;
	private final LocalTime receivedAtTime;

	public static NotificationSendTime from(LocalDateTime receivedAt) {
		return new NotificationSendTime(receivedAt.toLocalDate(), receivedAt.toLocalTime());
	}

	/*
	1. 받은 시각이 00시 ~ 06시라면 → 오늘 10시에 전송
	2. 받은 시각이 06시 ~ 21시라면 → 오늘 22시에 전송
	3. 받은 시각이 21시 ~ 00시라면 → 내일 10시에 전송
	 */

	public LocalDateTime willSendAt() {
		if (receivedAtTime.isBefore(MORNING_THRESHOLD)) {
			return LocalDateTime.of(receivedAtDate, MORNING_SEND_TIME);
		}
		if (receivedAtTime.isAfter(NIGHT_THRESHOLD) || receivedAtTime.equals(NIGHT_THRESHOLD)) {
			return LocalDateTime.of(receivedAtDate.plusDays(1), MORNING_SEND_TIME);
		}
		return LocalDateTime.of(receivedAtDate, NIGHT_SEND_TIME);
	}
}
