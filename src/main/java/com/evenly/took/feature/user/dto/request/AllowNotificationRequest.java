package com.evenly.took.feature.user.dto.request;

import java.util.List;

import com.evenly.took.feature.user.domain.AllowPush;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "알림 설정 수정을 위한 정보")
public record AllowNotificationRequest(

	@Schema(description = "전체 알림 허용 여부")
	boolean isAllowPushNotification,

	@Schema(description = "알림 허용 목록 (정확한 형식을 지켜 입력해주세요)", example = "[\"흥미로운 명함 알림\", \"한 줄 메모 알림\", \"서비스 업데이트 알림\"]")
	List<String> allowPushContent
) {

	public AllowPush toDomain() {
		return new AllowPush(isAllowPushNotification,
			AllowPushContentMapper.asAllowPushContents(allowPushContent));
	}
}
