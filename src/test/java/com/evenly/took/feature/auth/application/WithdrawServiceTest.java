package com.evenly.took.feature.auth.application;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.evenly.took.feature.auth.dto.request.WithdrawRequest;
import com.evenly.took.feature.card.application.CardService;
import com.evenly.took.feature.user.application.UserService;
import com.evenly.took.feature.user.domain.User;
import com.evenly.took.global.domain.UserFixture;
import com.evenly.took.global.service.ServiceTest;

class WithdrawServiceTest extends ServiceTest {

	@MockitoBean
	private UserService userService;

	@MockitoBean
	private CardService cardService;

	@MockitoBean
	private TokenProvider tokenProvider;

	@Autowired
	private WithdrawService withdrawService;

	@Autowired
	private UserFixture userFixture;

	@Test
	void 회원탈퇴시_모든_사용자_데이터를_소프트삭제하고_토큰을_무효화한다() {
		// given
		Long userId = 1L;
		WithdrawRequest request = new WithdrawRequest(
			"test-refresh-token",
			Arrays.asList("서비스가 마음에 들지 않아요"),
			"사용성이 불편해요"
		);
		User user = userFixture.create();

		when(userService.findById(anyLong())).thenReturn(user);

		// when
		withdrawService.withdraw(userId, request);

		// then
		verify(userService).findById(userId);
		verify(cardService).softDeleteAllReceivedCardFolders(eq(userId), any(LocalDateTime.class));
		verify(cardService).softDeleteAllReceivedCards(eq(userId), any(LocalDateTime.class));
		verify(cardService).softDeleteAllFolders(eq(userId), any(LocalDateTime.class));
		verify(cardService).softDeleteAllCards(eq(userId), any(LocalDateTime.class));
		verify(userService).save(any(User.class));
		verify(tokenProvider).invalidateRefreshToken(request.refreshToken());
	}

	@Test
	void 회원탈퇴시_사용자_정보에_탈퇴일자가_설정된다() {
		// given
		Long userId = 1L;
		WithdrawRequest request = new WithdrawRequest(
			"test-refresh-token",
			List.of("서비스가 마음에 들지 않아요"),
			"사용성이 불편해요"
		);
		User user = userFixture.create();

		when(userService.findById(anyLong())).thenReturn(user);

		// when
		withdrawService.withdraw(userId, request);

		// then
		verify(userService).save(user);
		org.junit.jupiter.api.Assertions.assertNotNull(user.getDeletedAt(), "회원 탈퇴 후 deletedAt 필드가 설정되어야 합니다");
		org.junit.jupiter.api.Assertions.assertNotNull(user.getWithdrawReasons(),
			"회원 탈퇴 후 withdrawReasons 필드가 설정되어야 합니다");
	}

}
