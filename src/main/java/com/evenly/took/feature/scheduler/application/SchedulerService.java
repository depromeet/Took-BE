package com.evenly.took.feature.scheduler.application;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.evenly.took.feature.notification.application.NotificationService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SchedulerService {

	private final NotificationService notificationService;

	@Scheduled(cron = "0 0 10 * * *")
	public void runAt10() {
		notificationService.sendNotification(LocalDateTime.of(LocalDate.now(), LocalTime.of(10, 0)));
	}

	@Scheduled(cron = "0 0 22 * * *")
	public void runAt22() {
		notificationService.sendNotification(LocalDateTime.of(LocalDate.now(), LocalTime.of(22, 0)));
	}
}
