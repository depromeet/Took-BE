package com.evenly.took.feature.notification.domain;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

class NotificationTimeRangeTest {

	@Test
	void 알림_전송_시각이_10시라면_전날_21시01분부터_당일_05시59분까지를_범위로_설정한다() {
		// given
		LocalDate date = LocalDate.now();
		LocalTime time = LocalTime.of(10, 0);
		NotificationTimeRange timeRange = NotificationTimeRange.from(LocalDateTime.of(date, time));

		// when & then
		assertThat(timeRange.startAt()).isEqualTo(LocalDateTime.of(date.minusDays(1), LocalTime.of(21, 1)));
		assertThat(timeRange.endAt()).isEqualTo(LocalDateTime.of(date, LocalTime.of(5, 59)));
	}

	@Test
	void 알림_전송_시각이_22시라면_당일_06시00분부터_당일_21시00분까지를_범위로_설정한다() {
		// given
		LocalDate date = LocalDate.now();
		LocalTime time = LocalTime.of(22, 0);
		NotificationTimeRange timeRange = NotificationTimeRange.from(LocalDateTime.of(date, time));

		// when & then
		assertThat(timeRange.startAt()).isEqualTo(LocalDateTime.of(date, LocalTime.of(6, 0)));
		assertThat(timeRange.endAt()).isEqualTo(LocalDateTime.of(date, LocalTime.of(21, 0)));
	}
}
