package com.evenly.took.feature.user.dto.request;

import java.util.List;

import com.evenly.took.feature.user.domain.AllowPush;
import com.evenly.took.feature.user.domain.AllowPushContent;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "알림 설정 수정을 위한 정보")
public record AllowNotificationRequest(

	@Schema(description = "전체 알림 허용 여부")
	boolean isAllowPush,

	@Schema(description = "알림 허용 목록 (허용한 알림 목록만)", example = "[\"MEMO\", \"INTERESTING\"]")
	List<String> allowPushContent
) {

	public AllowPush toDomain() {
		if (!isAllowPush) {
			return new AllowPush(isAllowPush, List.of());
		}
		return new AllowPush(isAllowPush, allowPushContent.stream()
			.map(AllowPushContent::asContent)
			.toList());
	}
}
