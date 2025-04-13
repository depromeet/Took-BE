package com.evenly.took.feature.auth.application;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.evenly.took.feature.auth.dto.request.WithdrawRequest;
import com.evenly.took.feature.card.application.CardService;
import com.evenly.took.feature.card.domain.vo.WithdrawReasons;
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
	public void withdraw(Long userId, WithdrawRequest request) {
		LocalDateTime now = LocalDateTime.now();

		User user = userService.findById(userId);

		cardService.softDeleteAllReceivedCardFolders(userId, now);
		cardService.softDeleteAllReceivedCards(userId, now);
		cardService.softDeleteAllFolders(userId, now);
		cardService.softDeleteAllCards(userId, now);

		WithdrawReasons withdrawReasons = request.toWithdrawReasons();
		user.withdraw(withdrawReasons);
		userService.save(user);

		tokenProvider.invalidateRefreshToken(request.refreshToken());
	}
}
