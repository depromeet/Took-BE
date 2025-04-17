package com.evenly.took.feature.user.dto.response;

import java.util.List;

import com.evenly.took.feature.user.domain.AllowPush;
import com.evenly.took.feature.user.domain.AllowPushContent;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "알림 설정 목록")
public record AllowNotificationResponse(

	@Schema(description = "전체 알림 허용 여부")
	boolean isAllowPush,

	@Schema(description = "알림 허용 목록 (허용한 알림 목록만 반환)", example = "[\"MEMO\", \"SYSTEM\"]")
	List<String> allowPushContent
) {

	public static AllowNotificationResponse from(AllowPush allowPush) {
		return new AllowNotificationResponse(allowPush.getAllowPushNotification(),
			allowPushContent(allowPush.getAllowPushContent()));
	}

	private static List<String> allowPushContent(List<AllowPushContent> allowPushContents) {
		if (allowPushContents == null) {
			return List.of();
		}
		return allowPushContents.stream()
			.map(Enum::name)
			.toList();
	}
}
