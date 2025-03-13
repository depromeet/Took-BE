package com.evenly.took.feature.card.mapper;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.evenly.took.feature.card.domain.Card;
import com.evenly.took.feature.card.domain.PreviewInfoType;
import com.evenly.took.feature.card.domain.SNSType;
import com.evenly.took.feature.card.dto.response.MyCardListResponse;
import com.evenly.took.feature.card.dto.response.MyCardResponse;
import com.evenly.took.feature.card.dto.response.PreviewInfoResponse;
import com.evenly.took.global.domain.TestCardFactory;
import com.evenly.took.global.service.ServiceTest;

class CardMapperTest extends ServiceTest {

	@Autowired
	private CardMapper cardMapper;

	@Test
	void 카드를_MyCardResponse로_변환시_기본필드_매핑_검증() {
		// given
		Card card = TestCardFactory.createDefaultCard();

		// when
		MyCardResponse response = cardMapper.toMyCardResponse(card);

		// then
		assertThat(response).isNotNull();
		assertThat(response.id()).isEqualTo(card.getId());
		assertThat(response.nickname()).isEqualTo(card.getNickname());
		assertThat(response.organization()).isEqualTo(card.getOrganization());
		assertThat(response.job()).isEqualTo(card.getCareer().getJob());
		assertThat(response.detailJob()).isEqualTo(card.getCareer().getDetailJobEn());
		assertThat(response.summary()).isEqualTo(card.getSummary());
		assertThat(response.interestDomain()).isEqualTo(card.getInterestDomain());
		assertThat(response.previewInfoType()).isEqualTo(card.getPreviewInfo());
		assertThat(response.imagePath()).isEqualTo(card.getImagePath());
	}

	@Test
	void 카드리스트를_MyCardResponse리스트로_변환() {
		// given
		List<Card> cards = Arrays.asList(
			TestCardFactory.createDefaultCard(),
			TestCardFactory.createDefaultCard()
		);

		// when
		MyCardListResponse responses = cardMapper.toMyCardListResponse(cards);

		// then
		assertThat(responses.cards()).isNotNull();
		assertThat(responses.cards()).hasSize(2);
	}

	@Test
	void 미리보기정보_PROJECT_변환검증() {
		// given
		Card card = TestCardFactory.createCardWithPreviewType(PreviewInfoType.PROJECT);

		// when
		MyCardResponse response = cardMapper.toMyCardResponse(card);

		// then
		assertThat(response.previewInfo()).isNotNull();
		assertThat(response.previewInfo().getProject()).isNotNull();
		assertThat(response.previewInfo().getProject().title()).isEqualTo("테스트 프로젝트");
		assertThat(response.previewInfo().getProject().link()).isEqualTo("https://github.com/test");
		assertThat(response.previewInfo().getProject().imageUrl()).isEqualTo("test-image.jpg");
		assertThat(response.previewInfo().getProject().description()).isEqualTo("테스트 프로젝트 설명");
	}

	@Test
	void 미리보기정보_CONTENT_변환검증() {
		// given
		Card card = TestCardFactory.createCardWithPreviewType(PreviewInfoType.CONTENT);

		// when
		MyCardResponse response = cardMapper.toMyCardResponse(card);

		// then
		assertThat(response.previewInfo()).isNotNull();
		assertThat(response.previewInfo().getContent()).isNotNull();
		assertThat(response.previewInfo().getContent().title()).isEqualTo("테스트 글");
		assertThat(response.previewInfo().getContent().link()).isEqualTo("https://blog.com/test");
		assertThat(response.previewInfo().getContent().imageUrl()).isEqualTo("test-blog-image.jpg");
		assertThat(response.previewInfo().getContent().description()).isEqualTo("테스트 글 설명");
	}

	@Test
	void 미리보기정보_HOBBY_변환검증() {
		// given
		Card card = TestCardFactory.createCardWithPreviewType(PreviewInfoType.HOBBY);

		// when
		MyCardResponse response = cardMapper.toMyCardResponse(card);

		// then
		assertThat(response.previewInfo()).isNotNull();
		assertThat(response.previewInfo().getHobby()).isEqualTo("등산, 독서");
	}

	@Test
	void 미리보기정보_SNS_변환검증() {
		// given
		Card card = TestCardFactory.createCardWithPreviewType(PreviewInfoType.SNS);

		// when
		MyCardResponse response = cardMapper.toMyCardResponse(card);

		// then
		assertThat(response.previewInfo()).isNotNull();
		assertThat(response.previewInfo().getSns()).isNotNull();
		assertThat(response.previewInfo().getSns().type()).isEqualTo(SNSType.GITHUB);
		assertThat(response.previewInfo().getSns().link()).isEqualTo("https://github.com/user");
	}

	@Test
	void 미리보기정보_NEWS_변환검증() {
		// given
		Card card = TestCardFactory.createCardWithPreviewType(PreviewInfoType.NEWS);

		// when
		MyCardResponse response = cardMapper.toMyCardResponse(card);

		// then
		assertThat(response.previewInfo()).isNotNull();
		assertThat(response.previewInfo().getNews()).isEqualTo("최근 블로그 포스팅 시작했습니다");
	}

	@Test
	void 미리보기정보_REGION_변환검증() {
		// given
		Card card = TestCardFactory.createCardWithPreviewType(PreviewInfoType.REGION);

		// when
		MyCardResponse response = cardMapper.toMyCardResponse(card);

		// then
		assertThat(response.previewInfo()).isNotNull();
		assertThat(response.previewInfo().getRegion()).isEqualTo("서울 강남구");
	}

	@Test
	void 빈데이터_NULL데이터_처리_검증() {
		// given
		Card card = TestCardFactory.createCard(cardBuilder ->
			cardBuilder.previewInfo(PreviewInfoType.PROJECT)
				.project(null)
		);

		// when
		MyCardResponse response = cardMapper.toMyCardResponse(card);

		// then
		assertThat(response).isNotNull();
		assertThat(response.previewInfo()).isNotNull();

		PreviewInfoResponse previewInfo = response.previewInfo();
		assertThat(previewInfo.getProject()).isNull();
		assertThat(previewInfo.getContent()).isNull();
		assertThat(previewInfo.getHobby()).isNull();
		assertThat(previewInfo.getSns()).isNull();
		assertThat(previewInfo.getNews()).isNull();
		assertThat(previewInfo.getRegion()).isNull();
	}
}
