package com.evenly.took.global.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.evenly.took.feature.auth.domain.OAuthIdentifier;
import com.evenly.took.feature.auth.domain.OAuthType;
import com.evenly.took.feature.user.dao.UserRepository;
import com.evenly.took.feature.user.domain.User;

@Component
public class UserFixture {

	@Autowired
	UserRepository userRepository;

	public User createUser(String name) {
		return createUser(name, "email", "oauth-id", OAuthType.KAKAO);
	}

	private User createUser(String name, String email, String oauthId, OAuthType oauthType) {
		User user = User.builder()
			.name(name)
			.email(email)
			.oauthIdentifier(new OAuthIdentifier(oauthId, oauthType))
			.build();
		return userRepository.save(user);
	}
}
