package com.evenly.took.feature.card.application;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.evenly.took.feature.card.dao.CardRepository;
import com.evenly.took.feature.card.domain.Card;
import com.evenly.took.feature.card.domain.Job;
import com.evenly.took.feature.card.domain.PreviewInfoType;
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
			.content(List.of())
			.project(List.of())
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
}
