package com.evenly.took.feature.auth.dto.response;

import com.evenly.took.feature.user.domain.User;

public record UserResponse(
	Long id,
	String name
) {

	public UserResponse(User user) {
		this(user.getId(), user.getName());
	}
}
