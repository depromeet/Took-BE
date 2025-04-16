package com.evenly.took.feature.user.dto.response;

import java.util.List;

import com.evenly.took.feature.user.domain.AllowPush;
import com.evenly.took.feature.user.dto.request.AllowPushContentMapper;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "알림 설정 목록")
public record AllowNotificationResponse(

	@Schema(description = "전체 알림 허용 여부")
	boolean isAllowPush,

	@Schema(description = "알림 허용 목록 (허용한 알림 목록만 반환)", example = "[\"흥미로운 명함 알림\", \"한 줄 메모 알림\", \"서비스 업데이트 알림\"]")
	List<String> allowPushContent
) {

	public AllowNotificationResponse(AllowPush allowPush) {
		this(allowPush.getAllowPushNotification(),
			allowPush.getAllowPushContent() != null ?
				AllowPushContentMapper.asResponses(allowPush.getAllowPushContent()) : List.of());
	}
}
