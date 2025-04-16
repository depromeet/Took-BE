package com.evenly.took.feature.notification.dto;

import java.time.LocalDateTime;

import com.evenly.took.feature.notification.domain.Notification;
import com.evenly.took.feature.notification.domain.NotificationData;
import com.evenly.took.feature.notification.domain.NotificationType;

import io.swagger.v3.oas.annotations.media.Schema;

public record NotificationResponse(

	@Schema(description = "알림 ID", example = "1")
	Long id,

	@Schema(description = "알림 유형", example = "MEMO")
	NotificationType type,

	@Schema(description = "알림 제목", example = "오늘 공유한 명함을 특별하게 만들어 볼까요?")
	String title,

	@Schema(description = "알림 본문", example = "다음 만남이 훨씬 자연스러워질 거예요")
	String body,

	@Schema(description = "클릭 시 이동시킬 링크", example = "/card-notes")
	String link,

	@Schema(description = "전송 시간")
	LocalDateTime sendAt
) {

	public static NotificationResponse from(Notification notification) {
		Long id = notification.getId();
		NotificationType type = notification.getType();
		NotificationData data = NotificationData.from(notification.getType());
		String title = data.getTitle();
		String body = data.getBody();
		String link = data.getLink();
		return new NotificationResponse(id, type, title, body, link, notification.getSendAt());
	}
}
