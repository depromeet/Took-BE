package com.evenly.took.feature.card.mapper;

import java.util.List;
import java.util.Optional;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.evenly.took.feature.card.domain.Card;
import com.evenly.took.feature.card.domain.PreviewInfoType;
import com.evenly.took.feature.card.domain.vo.Content;
import com.evenly.took.feature.card.domain.vo.Project;
import com.evenly.took.feature.card.domain.vo.SNS;
import com.evenly.took.feature.card.dto.response.CardDetailResponse;
import com.evenly.took.feature.card.dto.response.ContentResponse;
import com.evenly.took.feature.card.dto.response.MyCardListResponse;
import com.evenly.took.feature.card.dto.response.MyCardResponse;
import com.evenly.took.feature.card.dto.response.PreviewInfoResponse;
import com.evenly.took.feature.card.dto.response.ProjectResponse;
import com.evenly.took.feature.card.dto.response.SNSResponse;

@Mapper(componentModel = "spring", imports = {Optional.class})
public interface CardMapper {

	@Mapping(source = "card", target = "previewInfo", qualifiedByName = "toPreviewInfoResponse")
	@Mapping(source = "career.job", target = "job")
	@Mapping(source = "career.detailJobEn", target = "detailJob")
	@Mapping(source = "previewInfo", target = "previewInfoType")
	MyCardResponse toMyCardResponse(Card card);

	List<MyCardResponse> toMyCardResponseList(List<Card> cards);

	default MyCardListResponse toMyCardListResponse(List<Card> cards) {
		return new MyCardListResponse(toMyCardResponseList(cards));
	}

	@Named("toPreviewInfoResponse")
	default PreviewInfoResponse toPreviewInfoResponse(Card card) {
		if (card == null || card.getPreviewInfo() == null) {
			return null;
		}

		PreviewInfoResponse.PreviewInfoResponseBuilder builder = PreviewInfoResponse.builder();
		PreviewInfoType type = card.getPreviewInfo();

		switch (type) {
			case PROJECT -> handleProjectPreview(card, builder);
			case CONTENT -> handleContentPreview(card, builder);
			case HOBBY -> Optional.ofNullable(card.getHobby()).ifPresent(builder::hobby);
			case SNS -> handleSnsPreview(card, builder);
			case NEWS -> Optional.ofNullable(card.getNews()).ifPresent(builder::news);
			case REGION -> Optional.ofNullable(card.getRegion()).ifPresent(builder::region);
		}

		return builder.build();
	}

	@Named("handleProjectPreview")
	default void handleProjectPreview(Card card, PreviewInfoResponse.PreviewInfoResponseBuilder builder) {
		Optional.ofNullable(card.getProject())
			.filter(projects -> !projects.isEmpty())
			.map(projects -> projects.get(0))
			.map(this::toProjectResponse)
			.ifPresent(builder::project);
	}

	@Named("handleContentPreview")
	default void handleContentPreview(Card card, PreviewInfoResponse.PreviewInfoResponseBuilder builder) {
		Optional.ofNullable(card.getContent())
			.filter(contents -> !contents.isEmpty())
			.map(contents -> contents.get(0))
			.map(this::toContentResponse)
			.ifPresent(builder::content);
	}

	@Named("handleSnsPreview")
	default void handleSnsPreview(Card card, PreviewInfoResponse.PreviewInfoResponseBuilder builder) {
		Optional.ofNullable(card.getSns())
			.filter(snsList -> !snsList.isEmpty())
			.map(snsList -> snsList.get(0))
			.map(this::toSNSResponse)
			.ifPresent(builder::sns);
	}

	@Mapping(source = "career.job", target = "job")
	@Mapping(source = "career.detailJobEn", target = "detailJob")
	CardDetailResponse toCardDetailResponse(Card card);

	ProjectResponse toProjectResponse(Project project);

	ContentResponse toContentResponse(Content content);

	SNSResponse toSNSResponse(SNS sns);
}
