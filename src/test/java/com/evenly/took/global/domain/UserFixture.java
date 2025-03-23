package com.evenly.took.global.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.evenly.took.feature.user.dao.UserRepository;
import com.evenly.took.feature.user.domain.User;

@Component
public class UserFixture extends UserBase {

	@Autowired
	ApplicationContext context;

	@Autowired
	UserRepository userRepository;

	public UserBase creator() {
		return context.getBean(UserFixture.class);
	}

	@Override
	public User create() {
		User user = User.builder()
			.name(name)
			.email(email)
			.oauthIdentifier(oauthIdentifier)
			.build();
		return userRepository.save(user);
	}
}
