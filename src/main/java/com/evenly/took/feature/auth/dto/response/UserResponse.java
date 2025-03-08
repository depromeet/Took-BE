package com.evenly.took.feature.auth.dto.response;

import com.evenly.took.feature.user.domain.User;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "로그인 사용자 정보")
public record UserResponse(
	@Schema(description = "사용자 ID") Long id,
	@Schema(description = "사용자 이름") String name,
	@Schema(description = "사용자 이메일") String email
) {

	public UserResponse(User user) {
		this(user.getId(), user.getName(), user.getEmail());
	}
}
