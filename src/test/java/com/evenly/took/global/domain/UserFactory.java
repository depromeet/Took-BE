package com.evenly.took.global.domain;

import org.springframework.test.util.ReflectionTestUtils;

import com.evenly.took.feature.user.domain.User;

public class UserFactory extends UserBase {

	public UserBase creator() {
		return new UserFactory();
	}

	@Override
	public User create() {
		User user = User.builder()
			.name(name)
			.email(email)
			.oauthIdentifier(oauthIdentifier)
			.deletedAt(deletedAt)
			.build();
		ReflectionTestUtils.setField(user, "id", id);
		return user;
	}
}
