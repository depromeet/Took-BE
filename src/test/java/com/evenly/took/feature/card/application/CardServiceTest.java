package com.evenly.took.feature.card.application;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.evenly.took.feature.card.dao.CardRepository;
import com.evenly.took.feature.card.domain.Card;
import com.evenly.took.feature.card.domain.Job;
import com.evenly.took.feature.card.domain.vo.Content;
import com.evenly.took.feature.card.dto.request.CardDetailRequest;
import com.evenly.took.feature.card.dto.response.CardDetailResponse;
import com.evenly.took.feature.card.dto.response.CareersResponse;
import com.evenly.took.feature.card.exception.CardErrorCode;
import com.evenly.took.feature.card.mapper.CardMapper;
import com.evenly.took.global.domain.TestCardFactory;
import com.evenly.took.global.exception.TookException;
import com.evenly.took.global.service.ServiceTest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CardServiceTest extends ServiceTest {

	@Autowired
	private CardService cardService;

	@MockitoBean
	private CardRepository cardRepository;

	@Autowired
	private CardMapper cardMapper;

	@Autowired
	private ObjectMapper objectMapper;

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
		Long userId = 1L;
		Long cardId = 1L;
		CardDetailRequest request = new CardDetailRequest(cardId);

		Card card = TestCardFactory.createDefaultCard();
		when(cardRepository.findByUserIdAndIdAndDeletedAtIsNull(userId, cardId))
			.thenReturn(Optional.of(card));

		// when
		CardDetailResponse response = cardService.findCardDetail(userId, request);

		// then
		assertThat(response).isNotNull();
		assertThat(response.nickname()).isEqualTo("개발자");
		assertThat(response.organization()).isEqualTo("Evenly");
		assertThat(response.summary()).isEqualTo("백엔드 개발자입니다");
		assertThat(response.interestDomain()).containsExactly("Spring", "Java");
	}

	@Test
	void 카드_상세_정보_조회시_빈_배열_필드는_JSON_응답에서_제외된다() throws JsonProcessingException {
		// given
		Long userId = 1L;
		Long cardId = 1L;
		CardDetailRequest request = new CardDetailRequest(cardId);

		Card card = TestCardFactory.createCard(builder -> {
			builder.project(new ArrayList<>());
			builder.content(new ArrayList<>());
			builder.sns(new ArrayList<>());
		});

		when(cardRepository.findByUserIdAndIdAndDeletedAtIsNull(userId, cardId))
			.thenReturn(Optional.of(card));

		// when
		CardDetailResponse response = cardService.findCardDetail(userId, request);
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
		Long userId = 1L;
		Long cardId = 1L;
		CardDetailRequest request = new CardDetailRequest(cardId);

		// 컨텐츠 정보 추가
		List<Content> contents = List.of(
			new Content("테스트 글", "https://blog.com/test", "test-blog-image.jpg", "테스트 글 설명")
		);
		Card card = TestCardFactory.createCardWithContents(contents);

		when(cardRepository.findByUserIdAndIdAndDeletedAtIsNull(userId, cardId))
			.thenReturn(Optional.of(card));

		// when
		CardDetailResponse response = cardService.findCardDetail(userId, request);
		String jsonResponse = objectMapper.writeValueAsString(response);

		// then
		assertThat(response).isNotNull();

		// 데이터가 있는 배열은 JSON 응답에 포함되어야 함
		assertThat(jsonResponse).contains("\"content\":");
		assertThat(jsonResponse).contains("\"테스트 글\"");
		assertThat(jsonResponse).contains("\"interestDomain\":");
		assertThat(jsonResponse).contains("\"Spring\"");
	}

	@Test
	void 카드_상세_정보_조회시_카드가_존재하지_않으면_예외를_발생시킨다() {
		// given
		Long userId = 1L;
		Long cardId = 999L;
		CardDetailRequest request = new CardDetailRequest(cardId);

		when(cardRepository.findByUserIdAndIdAndDeletedAtIsNull(userId, cardId))
			.thenReturn(Optional.empty());

		// when & then
		TookException exception = assertThrows(TookException.class, () -> {
			cardService.findCardDetail(userId, request);
		});

		assertThat(exception.getErrorCode()).isEqualTo(CardErrorCode.CARD_NOT_FOUND);
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
