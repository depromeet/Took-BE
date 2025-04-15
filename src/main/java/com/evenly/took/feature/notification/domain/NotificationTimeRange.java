package com.evenly.took.feature.notification.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.evenly.took.feature.notification.exception.NotificationErrorCode;
import com.evenly.took.global.exception.TookException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NotificationTimeRange {

	private static final LocalTime SEND_TIME_10_00 = LocalTime.of(10, 0);
	private static final LocalTime SEND_TIME_22_00 = LocalTime.of(22, 0);
	private static final LocalTime THRESHOLD_06_00 = LocalTime.of(6, 0);
	private static final LocalTime THRESHOLD_21_00 = LocalTime.of(21, 0);

	private final LocalDate sendDate;
	private final LocalTime sendTime;

	public static NotificationTimeRange from(LocalDateTime sendAt) {
		return new NotificationTimeRange(sendAt.toLocalDate(), sendAt.toLocalTime());
	}

	/*
	1. 10시 알림: 전날 21:01 ~ 당일 05:59 에 받은 명함
	2. 22시 알림: 당일 06:00 ~ 당일 21:00 에 받은 명함
	 */

	public LocalDateTime startAt() {
		if (sendTime.equals(SEND_TIME_10_00)) {
			return LocalDateTime.of(sendDate.minusDays(1), THRESHOLD_21_00.plusMinutes(1));
		}
		if (sendTime.equals(SEND_TIME_22_00)) {
			return LocalDateTime.of(sendDate, THRESHOLD_06_00);
		}
		throw new TookException(NotificationErrorCode.INVALID_SEND_TIME);
	}

	public LocalDateTime endAt() {
		if (sendTime.equals(SEND_TIME_10_00)) {
			return LocalDateTime.of(sendDate, THRESHOLD_06_00.minusMinutes(1));
		}
		if (sendTime.equals(SEND_TIME_22_00)) {
			return LocalDateTime.of(sendDate, THRESHOLD_21_00);
		}
		throw new TookException(NotificationErrorCode.INVALID_SEND_TIME);
	}
}
