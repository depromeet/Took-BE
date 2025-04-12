package com.evenly.took.feature.card.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.evenly.took.feature.card.dao.CardRepository;
import com.evenly.took.feature.card.domain.Card;
import com.evenly.took.feature.card.domain.Job;
import com.evenly.took.feature.card.domain.PreviewInfoType;
import com.evenly.took.feature.card.dto.request.AddCardRequest;
import com.evenly.took.feature.card.dto.request.CardDetailRequest;
import com.evenly.took.feature.card.dto.response.CardDetailResponse;
import com.evenly.took.feature.card.dto.response.CareersResponse;
import com.evenly.took.feature.card.exception.CardErrorCode;
import com.evenly.took.feature.user.domain.User;
import com.evenly.took.global.exception.TookException;
import com.evenly.took.global.service.ServiceTest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CardServiceTest extends ServiceTest {

	@Autowired
	CardService cardService;

	@Autowired
	CardRepository cardRepository;

	@Autowired
	ObjectMapper objectMapper;

	@Test
	void 디자인_직군에_해당하는_모든_커리어를_조회한다() {
		// given, when
		CareersResponse response = cardService.findCareers(Job.DESIGNER);

		// then
		assertThat(response.careers()).hasSize(6);
	}

	@Test
	void 개발_직군에_해당하는_모든_커리어를_조회한다() {
		// given, when
		CareersResponse response = cardService.findCareers(Job.DEVELOPER);

		// then
		assertThat(response.careers()).hasSize(11);
	}

	@Test
	void 카드_상세_정보_조회시_카드가_존재하면_정상적으로_반환한다() {
		// given
		User user = userFixture.create();
		Card card = cardFixture.creator()
			.user(user)
			.create();
		CardDetailRequest request = new CardDetailRequest(card.getId());

		// when
		CardDetailResponse response = cardService.findCardDetail(user.getId(), request);

		// then
		assertThat(response).isNotNull();
		assertThat(response.nickname()).isEqualTo(card.getNickname());
		assertThat(response.organization()).isEqualTo(card.getOrganization());
		assertThat(response.summary()).isEqualTo(card.getSummary());
	}

	@Test
	void 카드_상세_정보_조회시_빈_배열_필드는_JSON_응답에서_제외된다() throws JsonProcessingException {
		// given
		User user = userFixture.create();
		Card card = cardFixture.creator()
			.user(user)
			.sns(List.of())
			.contents(List.of())
			.projects(List.of())
			.create();
		CardDetailRequest request = new CardDetailRequest(card.getId());

		// when
		CardDetailResponse response = cardService.findCardDetail(user.getId(), request);
		String jsonResponse = objectMapper.writeValueAsString(response);

		// then
		assertThat(response).isNotNull();

		// JSON 변환 결과에서 빈 배열이 포함되지 않는지 확인
		assertThat(jsonResponse).doesNotContain("\"content\":[]");
		assertThat(jsonResponse).doesNotContain("\"project\":[]");
		assertThat(jsonResponse).doesNotContain("\"sns\":[]");

		// 값이 존재하는 필드는 포함되어야 함
		assertThat(jsonResponse).contains("\"nickname\"");
		assertThat(jsonResponse).contains("\"interestDomain\"");
	}

	@Test
	void 카드_상세_정보_조회시_데이터가_있는_배열은_JSON_응답에_포함된다() throws JsonProcessingException {
		// given
		User user = userFixture.create();
		Card card = cardFixture.creator()
			.user(user)
			.previewInfo(PreviewInfoType.CONTENT)
			.create();
		CardDetailRequest request = new CardDetailRequest(card.getId());

		// when
		CardDetailResponse response = cardService.findCardDetail(user.getId(), request);
		String jsonResponse = objectMapper.writeValueAsString(response);

		// then
		assertThat(response).isNotNull();

		// 데이터가 있는 배열은 JSON 응답에 포함되어야 함
		assertThat(jsonResponse).contains("content");
		assertThat(jsonResponse).contains(card.getContent().get(0).title());
	}

	@Test
	void 카드_상세_정보_조회시_카드가_존재하지_않으면_예외를_발생시킨다() {
		// given
		User user = userFixture.create();
		CardDetailRequest request = new CardDetailRequest(999999L);

		// when & then
		assertThatThrownBy(() -> cardService.findCardDetail(user.getId(), request))
			.isInstanceOf(TookException.class)
			.hasMessage(CardErrorCode.CARD_NOT_FOUND.getMessage());
	}

	// @Test
	// void 명함_개수가_최대치를_초과하면_예외를_반환한다() {
	// 	Long userId = 1L;
	//
	// 	// Create a list of mock cards
	// 	List<Card> mockCards = Arrays.asList(
	// 		mock(Card.class),
	// 		mock(Card.class),
	// 		mock(Card.class),
	// 		mock(Card.class)
	// 	);
	//
	// 	BDDMockito.given(cardRepository.countByUserIdAndDeletedAtIsNull(anyLong()))
	// 		.willReturn((long)mockCards.size());
	//
	// 	CreateCardRequest mockRequest = mock(CreateCardRequest.class);
	//
	// 	// when & then
	// 	TookException exception = assertThrows(TookException.class, () -> {
	// 		cardService.createCard(User.toEntity(userId), mockRequest, "test_image_key");
	// 	});
	//
	// 	assertThat(exception.getErrorCode()).isEqualTo(CardErrorCode.CARD_LIMIT_EXCEEDED);
	// }

	@Nested
	class 대표_명함_기능 {

		@Test
		void 대표_명함_삭제_시_남은_명함이_대표로_지정된다() {
			// given
			User user = userFixture.create();
			Card card1 = cardFixture.creator().user(user).isPrimary(true).create();
			Card card2 = cardFixture.creator().user(user).create();

			// when
			cardService.softDeleteMyCard(user.getId(), card1.getId());

			// then
			List<Card> cards = cardRepository.findAllByUserIdAndDeletedAtIsNull(user.getId());
			assertThat(cards).hasSize(1);
			assertThat(cards.get(0).isPrimary()).isTrue();
		}

		@Test
		void 특정_명함을_대표로_설정하면_다른_명함은_대표가_해제된다() {
			// given
			User user = userFixture.create();
			Card card1 = cardFixture.creator().user(user).isPrimary(true).create();
			Card card2 = cardFixture.creator().user(user).isPrimary(false).create();

			// when
			cardService.setPrimaryCard(user.getId(), card2.getId());

			// then
			Card updatedCard1 = cardRepository.findById(card1.getId()).get();
			Card updatedCard2 = cardRepository.findById(card2.getId()).get();

			assertThat(updatedCard1.isPrimary()).isFalse();
			assertThat(updatedCard2.isPrimary()).isTrue();
		}

		@Test
		void 최초_명함_생성시_자동으로_대표_명함으로_지정된다() {
			// given
			User user = userFixture.create();
			assertThat(cardRepository.countByUserIdAndDeletedAtIsNull(user.getId())).isZero();

			AddCardRequest request = new AddCardRequest(
				null,
				"테스트닉네임",
				1L,
				List.of("AI", "백엔드"),
				"요약입니다",
				"조직명",
				List.of(),
				"서울",
				"등산",
				"뉴스",
				List.of(),
				List.of(),
				PreviewInfoType.SNS
			);

			String profileImageKey = "test-image.jpg";

			// when
			cardService.createCard(user, request, profileImageKey);

			// then
			List<Card> cards = cardRepository.findAllByUserIdAndDeletedAtIsNull(user.getId());
			assertThat(cards).hasSize(1);
			assertThat(cards.get(0).isPrimary()).isTrue();
		}

		@Test
		void 기존_명함이_있을경우_새로_생성한_명함은_대표_명함이_아니다() {
			// given
			User user = userFixture.create();
			cardFixture.creator().user(user).isPrimary(true).create();

			AddCardRequest request = new AddCardRequest(
				null,
				"테스트닉네임",
				1L,
				List.of("AI", "백엔드"),
				"요약입니다",
				"조직명",
				List.of(),
				"서울",
				"등산",
				"뉴스",
				List.of(),
				List.of(),
				PreviewInfoType.SNS
			);
			String profileImageKey = "test-image-2.jpg";

			// when
			cardService.createCard(user, request, profileImageKey);

			// then
			List<Card> cards = cardRepository.findAllByUserIdAndDeletedAtIsNull(user.getId());
			assertThat(cards).hasSize(2);
			assertThat(cards.stream().filter(Card::isPrimary)).hasSize(1);
		}

		@Test
		void 명함_생성_후_대표_삭제_시_다른_명함이_대표로_지정된다() {
			// given
			User user = userFixture.create();
			String profileImageKey = "test-image.jpg";

			AddCardRequest request1 = new AddCardRequest(
				null,
				"테스트닉네임1",
				1L,
				List.of("AI", "백엔드"),
				"요약입니다",
				"조직명",
				List.of(),
				"서울",
				"등산",
				"뉴스",
				List.of(),
				List.of(),
				PreviewInfoType.SNS
			);

			cardService.createCard(user, request1, profileImageKey);

			AddCardRequest request2 = new AddCardRequest(
				null,
				"일반명함",
				1L,
				List.of("프론트"),
				"요약2",
				"회사2",
				List.of(),
				"부산",
				"독서",
				"뉴스",
				List.of(),
				List.of(),
				PreviewInfoType.SNS
			);
			cardService.createCard(user, request2, profileImageKey);

			List<Card> cards = cardRepository.findAllByUserIdAndDeletedAtIsNull(user.getId());
			Card primaryCard = cards.stream().filter(Card::isPrimary).findFirst().orElseThrow();
			Card nonPrimaryCard = cards.stream().filter(c -> !c.isPrimary()).findFirst().orElseThrow();

			// when
			cardService.softDeleteMyCard(user.getId(), primaryCard.getId());

			// then
			Card updated = cardRepository.findById(nonPrimaryCard.getId()).orElseThrow();
			assertThat(updated.isPrimary()).isTrue();
		}
	}
}
