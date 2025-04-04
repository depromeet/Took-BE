package com.evenly.took.feature.auth.dto.request;

import java.util.List;

import com.evenly.took.feature.card.domain.vo.WithdrawReasons;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "회원탈퇴를 위한 Request", requiredMode = Schema.RequiredMode.REQUIRED)
public record WithdrawRequest(
	@NotNull
	@Schema(description = "리프레시 토큰", example = "ff8b4bed-46d7-4667-937d-0797580afe43")
	String refreshToken,

	@Schema(description = "탈퇴 이유 목록", example = "[\"서비스가 마음에 들지 않아요\", \"더 이상 필요하지 않아요\"]")
	List<String> reasons,

	@Schema(description = "직접 입력한 탈퇴 메시지", example = "UI/UX가 불편해요")
	String directMessage
) {
	public WithdrawReasons toWithdrawReasons() {
		return new WithdrawReasons(reasons, directMessage);
	}
}
