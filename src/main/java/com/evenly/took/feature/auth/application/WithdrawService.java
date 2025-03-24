package com.evenly.took.feature.auth.application;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.evenly.took.feature.auth.exception.AuthErrorCode;
import com.evenly.took.feature.card.dao.CardRepository;
import com.evenly.took.feature.card.dao.FolderRepository;
import com.evenly.took.feature.card.dao.ReceivedCardFolderRepository;
import com.evenly.took.feature.card.dao.ReceivedCardRepository;
import com.evenly.took.feature.user.dao.UserRepository;
import com.evenly.took.feature.user.domain.User;
import com.evenly.took.global.exception.TookException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WithdrawService {

	private final UserRepository userRepository;
	private final CardRepository cardRepository;
	private final FolderRepository folderRepository;
	private final ReceivedCardRepository receivedCardRepository;
	private final ReceivedCardFolderRepository receivedCardFolderRepository;
	private final TokenProvider tokenProvider;

	@Transactional
	public void withdraw(Long userId, String refreshToken) {
		LocalDateTime now = LocalDateTime.now();

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new TookException(AuthErrorCode.USER_NOT_FOUND));

		receivedCardFolderRepository.softDeleteAllByUserId(userId, now);
		receivedCardRepository.softDeleteAllByUserId(userId, now);
		folderRepository.softDeleteAllByUserId(userId, now);
		cardRepository.softDeleteAllByUserId(userId, now);

		user.withdraw();
		userRepository.save(user);

		tokenProvider.invalidateRefreshToken(refreshToken);
	}
}
