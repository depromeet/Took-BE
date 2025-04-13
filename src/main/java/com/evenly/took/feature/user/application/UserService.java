package com.evenly.took.feature.user.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.evenly.took.feature.auth.exception.AuthErrorCode;
import com.evenly.took.feature.user.dao.UserRepository;
import com.evenly.took.feature.user.domain.AllowPush;
import com.evenly.took.feature.user.domain.User;
import com.evenly.took.feature.user.dto.request.AllowNotificationRequest;
import com.evenly.took.feature.user.dto.response.AllowNotificationResponse;
import com.evenly.took.global.exception.TookException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;

	@Transactional(readOnly = true)
	public User findById(Long id) {
		return userRepository.findById(id)
			.orElseThrow(() -> new TookException(AuthErrorCode.USER_NOT_FOUND));
	}

	@Transactional
	public void save(User user) {
		userRepository.save(user);
	}

	@Transactional(readOnly = true)
	public AllowNotificationResponse getAllowNotification(User user) {
		return new AllowNotificationResponse(user.getAllowPush());
	}

	@Transactional
	public void updateAllowNotification(Long userId, AllowNotificationRequest request) {
		User user = findById(userId);
		AllowPush allowPush = request.toDomain();
		user.updateAllowPush(allowPush);
	}
}
