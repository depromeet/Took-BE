package com.evenly.took.feature.card.mapper;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.evenly.took.feature.card.domain.Card;
import com.evenly.took.feature.card.domain.PreviewInfoType;
import com.evenly.took.feature.card.dto.response.CardResponse;
import com.evenly.took.feature.card.dto.response.MyCardListResponse;
import com.evenly.took.feature.card.dto.response.PreviewInfoResponse;
import com.evenly.took.feature.user.domain.User;
import com.evenly.took.global.service.ServiceTest;

class CardMapperTest extends ServiceTest {

	@Autowired
	CardMapper cardMapper;

	@Test
	void 카드를_MyCardResponse로_변환시_기본필드_매핑_검증() {
		// given
		Card card = cardFixture.create();

		// when
		CardResponse response = cardMapper.toCardResponse(card);

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
		User user = userFixture.create();
		Card card1 = cardFixture.creator()
			.user(user)
			.create();
		Card card2 = cardFixture.creator()
			.user(user)
			.create();
		List<Card> cards = List.of(card1, card2);

		// when
		MyCardListResponse responses = cardMapper.toMyCardListResponse(cards);

		// then
		assertThat(responses.cards()).isNotNull();
		assertThat(responses.cards()).hasSize(cards.size());
	}

	@Test
	void 미리보기정보_PROJECT_변환검증() {
		// given
		Card card = cardFixture.creator()
			.previewInfo(PreviewInfoType.PROJECT)
			.create();

		// when
		CardResponse response = cardMapper.toCardResponse(card);

		// then
		assertThat(response.previewInfo()).isNotNull();
		assertThat(response.previewInfo().getProject()).isNotNull();
		assertThat(response.previewInfo().getProject().title()).isEqualTo(card.getProject().get(0).title());
		assertThat(response.previewInfo().getProject().link()).isEqualTo(card.getProject().get(0).link());
		assertThat(response.previewInfo().getProject().imageUrl()).isEqualTo(card.getProject().get(0).imageUrl());
		assertThat(response.previewInfo().getProject().description()).isEqualTo(card.getProject().get(0).description());
	}

	@Test
	void 미리보기정보_CONTENT_변환검증() {
		// given
		Card card = cardFixture.creator()
			.previewInfo(PreviewInfoType.CONTENT)
			.create();

		// when
		CardResponse response = cardMapper.toCardResponse(card);

		// then
		assertThat(response.previewInfo()).isNotNull();
		assertThat(response.previewInfo().getContent()).isNotNull();
		assertThat(response.previewInfo().getContent().title()).isEqualTo(card.getContent().get(0).title());
		assertThat(response.previewInfo().getContent().link()).isEqualTo(card.getContent().get(0).link());
		assertThat(response.previewInfo().getContent().imageUrl()).isEqualTo(card.getContent().get(0).imageUrl());
		assertThat(response.previewInfo().getContent().description()).isEqualTo(card.getContent().get(0).description());
	}

	@Test
	void 미리보기정보_HOBBY_변환검증() {
		// given
		Card card = cardFixture.creator()
			.previewInfo(PreviewInfoType.HOBBY)
			.create();

		// when
		CardResponse response = cardMapper.toCardResponse(card);

		// then
		assertThat(response.previewInfo()).isNotNull();
		assertThat(response.previewInfo().getHobby()).isEqualTo(card.getHobby());
	}

	@Test
	void 미리보기정보_SNS_변환검증() {
		// given
		Card card = cardFixture.creator()
			.previewInfo(PreviewInfoType.SNS)
			.create();

		// when
		CardResponse response = cardMapper.toCardResponse(card);

		// then
		assertThat(response.previewInfo()).isNotNull();
		assertThat(response.previewInfo().getSns()).isNotNull();
		assertThat(response.previewInfo().getSns().type()).isEqualTo(card.getSns().get(0).type());
		assertThat(response.previewInfo().getSns().link()).isEqualTo(card.getSns().get(0).link());
	}

	@Test
	void 미리보기정보_NEWS_변환검증() {
		// given
		Card card = cardFixture.creator()
			.previewInfo(PreviewInfoType.NEWS)
			.create();

		// when
		CardResponse response = cardMapper.toCardResponse(card);

		// then
		assertThat(response.previewInfo()).isNotNull();
		assertThat(response.previewInfo().getNews()).isEqualTo(card.getNews());
	}

	@Test
	void 미리보기정보_REGION_변환검증() {
		// given
		Card card = cardFixture.creator()
			.previewInfo(PreviewInfoType.REGION)
			.create();

		// when
		CardResponse response = cardMapper.toCardResponse(card);

		// then
		assertThat(response.previewInfo()).isNotNull();
		assertThat(response.previewInfo().getRegion()).isEqualTo(card.getRegion());
	}

	@Test
	void 빈데이터_NULL데이터_처리_검증() {
		// given
		Card card = cardFixture.creator()
			.previewInfo(PreviewInfoType.PROJECT)
			.projects(null)
			.create();

		// when
		CardResponse response = cardMapper.toCardResponse(card);

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
