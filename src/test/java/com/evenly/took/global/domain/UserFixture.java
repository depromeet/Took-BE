package com.evenly.took.global.domain;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.evenly.took.feature.auth.domain.OAuthIdentifier;
import com.evenly.took.feature.auth.domain.OAuthType;
import com.evenly.took.feature.user.dao.UserRepository;
import com.evenly.took.feature.user.domain.User;

@Component
public class UserFixture extends UserBase {

	@Autowired
	UserRepository userRepository;

	public UserBase creator() {
		init();
		return this;
	}

	@Override
	public User create() {
		this.oauthIdentifier = OAuthIdentifier.builder()
			.oauthId(UUID.randomUUID().toString())
			.oauthType(OAuthType.GOOGLE)
			.build();
		User user = User.builder()
			.name(name)
			.email(email)
			.oauthIdentifier(oauthIdentifier)
			.deletedAt(deletedAt)
			.withdrawReasons(withdrawReasons)
			.allowPush(allowPush)
			.build();
		return userRepository.save(user);
	}
}
