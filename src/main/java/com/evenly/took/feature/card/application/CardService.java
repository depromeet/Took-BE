package com.evenly.took.feature.card.application;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.evenly.took.feature.card.dao.CardRepository;
import com.evenly.took.feature.card.dao.CareerRepository;
import com.evenly.took.feature.card.domain.Card;
import com.evenly.took.feature.card.domain.Career;
import com.evenly.took.feature.card.domain.Job;
import com.evenly.took.feature.card.dto.request.CreateCardRequest;
import com.evenly.took.feature.card.dto.response.CareersResponse;
import com.evenly.took.feature.card.exception.CardErrorCode;
import com.evenly.took.feature.card.mapper.CareersMapper;
import com.evenly.took.feature.card.mapper.ContentMapper;
import com.evenly.took.feature.card.mapper.ProjectMapper;
import com.evenly.took.feature.card.mapper.SnsMapper;
import com.evenly.took.feature.user.domain.User;
import com.evenly.took.global.aws.s3.S3Service;
import com.evenly.took.global.exception.TookException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CardService {

	private final CardRepository cardRepository;
	private final CareerRepository careerRepository;
	private final S3Service s3Service;
	private final SnsMapper snsMapper;
	private final ContentMapper contentMapper;
	private final ProjectMapper projectMapper;
	private final CareersMapper careersMapper;

	public String uploadProfileImage(MultipartFile profileImage) {
		return this.s3Service.uploadFile(profileImage, "profile/");
	}

	public void createCard(User user, CreateCardRequest request, String profileImageKey) {
		List<Card> currentCards = this.cardRepository.findByUserIdAndDeletedAtIsNull(user.getId());

		if (currentCards.size() > 3) {
			throw new TookException(CardErrorCode.CARD_LIMIT_EXCEEDED);
		}

		Card newCard = Card.builder()
			.user(user)
			.imagePath(profileImageKey)
			.nickname(request.nickname())
			.career(Career.toEntity(request.detailJobId()))
			.interestDomain(request.interestDomain())
			.summary(request.summary())
			.organization(request.organization())
			.sns(this.snsMapper.toEntity(request.sns()))
			.region(request.region())
			.hobby(request.hobby())
			.news(request.news())
			.content(this.contentMapper.toEntity(request.content()))
			.project(this.projectMapper.toEntity(request.project()))
			.previewInfo(request.previewInfoType())
			.build();

		cardRepository.save(newCard);
	}

	public CareersResponse findCareers(Job job) {
		List<Career> careers = careerRepository.findAllByJob(job);
		return careersMapper.toResponse(careers);
	}
}
