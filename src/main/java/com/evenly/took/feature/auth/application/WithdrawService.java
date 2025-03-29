package com.evenly.took.feature.auth.application;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.evenly.took.feature.card.application.CardService;
import com.evenly.took.feature.user.application.UserService;
import com.evenly.took.feature.user.domain.User;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WithdrawService {

	private final UserService userService;
	private final CardService cardService;
	private final TokenProvider tokenProvider;

	@Transactional
	public void withdraw(Long userId, String refreshToken) {
		LocalDateTime now = LocalDateTime.now();

		User user = userService.findById(userId);

		cardService.softDeleteAllReceivedCardFolders(userId, now);
		cardService.softDeleteAllReceivedCards(userId, now);
		cardService.softDeleteAllFolders(userId, now);
		cardService.softDeleteAllCards(userId, now);

		user.withdraw();
		userService.save(user);

		tokenProvider.invalidateRefreshToken(refreshToken);
	}
}
