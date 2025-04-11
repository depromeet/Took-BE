package com.evenly.took.feature.notification.domain;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class NotificationSendTimeTest {

	@CsvSource({"0,0", "2,30", "5,59"})
	@ParameterizedTest
	void 받은_시각이_00시부터_05시59분이라면_당일_10시를_반환한다(int hour, int minute) {
		// given
		LocalDate date = LocalDate.now();
		LocalTime time = LocalTime.of(hour, minute);
		NotificationSendTime sendTime = NotificationSendTime.from(LocalDateTime.of(date, time));

		// when
		LocalDateTime willSendAt = sendTime.willSendAt();

		// then
		assertThat(willSendAt).hasMonth(date.getMonth());
		assertThat(willSendAt).hasDayOfMonth(date.getDayOfMonth());
		assertThat(willSendAt).hasHour(10);
		assertThat(willSendAt).hasMinute(0);
	}

	@CsvSource({"6,0", "12,00", "20,59"})
	@ParameterizedTest
	void 받은_시각이_06시부터_20시59분이라면_당일_22시를_반환한다(int hour, int minute) {
		// given
		LocalDate date = LocalDate.now();
		LocalTime time = LocalTime.of(hour, minute);
		NotificationSendTime sendTime = NotificationSendTime.from(LocalDateTime.of(date, time));

		// when
		LocalDateTime willSendAt = sendTime.willSendAt();

		// then
		assertThat(willSendAt).hasMonth(date.getMonth());
		assertThat(willSendAt).hasDayOfMonth(date.getDayOfMonth());
		assertThat(willSendAt).hasHour(22);
		assertThat(willSendAt).hasMinute(0);
	}

	@CsvSource({"21,0", "23,00", "23,59"})
	@ParameterizedTest
	void 받은_시각이_21시부터_23시59분이라면_내일_10시를_반환한다(int hour, int minute) {
		// given
		LocalDate date = LocalDate.now();
		LocalTime time = LocalTime.of(hour, minute);
		NotificationSendTime sendTime = NotificationSendTime.from(LocalDateTime.of(date, time));

		// when
		LocalDateTime willSendAt = sendTime.willSendAt();

		// then
		assertThat(willSendAt).hasMonth(date.getMonth());
		assertThat(willSendAt).hasDayOfMonth(date.getDayOfMonth() + 1);
		assertThat(willSendAt).hasHour(10);
		assertThat(willSendAt).hasMinute(0);
	}
}
