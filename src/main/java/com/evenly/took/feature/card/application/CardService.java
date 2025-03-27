package com.evenly.took.feature.card.application;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.evenly.took.feature.card.client.dto.CrawledDto;
import com.evenly.took.feature.card.dao.CardRepository;
import com.evenly.took.feature.card.dao.CareerRepository;
import com.evenly.took.feature.card.dao.FolderRepository;
import com.evenly.took.feature.card.dao.ReceivedCardFolderRepository;
import com.evenly.took.feature.card.dao.ReceivedCardRepository;
import com.evenly.took.feature.card.domain.Card;
import com.evenly.took.feature.card.domain.Career;
import com.evenly.took.feature.card.domain.Folder;
import com.evenly.took.feature.card.domain.Job;
import com.evenly.took.feature.card.domain.ReceivedCard;
import com.evenly.took.feature.card.domain.ReceivedCardFolder;
import com.evenly.took.feature.card.dto.request.AddCardRequest;
import com.evenly.took.feature.card.dto.request.AddFolderRequest;
import com.evenly.took.feature.card.dto.request.CardDetailRequest;
import com.evenly.took.feature.card.dto.request.CardRequest;
import com.evenly.took.feature.card.dto.request.FixFolderRequest;
import com.evenly.took.feature.card.dto.request.FixReceivedCardRequest;
import com.evenly.took.feature.card.dto.request.LinkRequest;
import com.evenly.took.feature.card.dto.request.ReceiveCardRequest;
import com.evenly.took.feature.card.dto.request.ReceivedCardsRequest;
import com.evenly.took.feature.card.dto.request.RemoveFolderRequest;
import com.evenly.took.feature.card.dto.request.RemoveReceivedCardsRequest;
import com.evenly.took.feature.card.dto.request.SetReceivedCardsFolderRequest;
import com.evenly.took.feature.card.dto.response.CardDetailResponse;
import com.evenly.took.feature.card.dto.response.CardResponse;
import com.evenly.took.feature.card.dto.response.CareersResponse;
import com.evenly.took.feature.card.dto.response.FolderResponse;
import com.evenly.took.feature.card.dto.response.FoldersResponse;
import com.evenly.took.feature.card.dto.response.MyCardListResponse;
import com.evenly.took.feature.card.dto.response.ReceivedCardListResponse;
import com.evenly.took.feature.card.dto.response.ScrapResponse;
import com.evenly.took.feature.card.exception.CardErrorCode;
import com.evenly.took.feature.card.exception.FolderErrorCode;
import com.evenly.took.feature.card.mapper.CardMapper;
import com.evenly.took.feature.card.mapper.CareersMapper;
import com.evenly.took.feature.card.mapper.ContentMapper;
import com.evenly.took.feature.card.mapper.FolderMapper;
import com.evenly.took.feature.card.mapper.ProjectMapper;
import com.evenly.took.feature.card.mapper.ScrapMapper;
import com.evenly.took.feature.card.mapper.SnsMapper;
import com.evenly.took.feature.user.domain.User;
import com.evenly.took.global.aws.s3.S3Service;
import com.evenly.took.global.exception.TookException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class CardService {

	private final CardRepository cardRepository;
	private final CareerRepository careerRepository;
	private final FolderRepository folderRepository;
	private final ReceivedCardRepository receivedCardRepository;
	private final ReceivedCardFolderRepository receivedCardFolderRepository;
	private final LinkExtractor linkExtractor;
	private final S3Service s3Service;
	private final SnsMapper snsMapper;
	private final ContentMapper contentMapper;
	private final ProjectMapper projectMapper;
	private final CareersMapper careersMapper;
	private final CardMapper cardMapper;
	private final ScrapMapper scrapMapper;
	private final FolderMapper folderMapper;

	@Transactional(readOnly = true)
	public MyCardListResponse findUserCardList(Long userId) {
		List<Card> cards = cardRepository.findAllByUserIdAndDeletedAtIsNull(userId);
		return cardMapper.toMyCardListResponse(cards);
	}

	@Transactional(readOnly = true)
	public CardDetailResponse findCardDetail(Long userId, CardDetailRequest request) {
		Optional<Card> ownCard = cardRepository.findByUserIdAndIdAndDeletedAtIsNull(userId, request.cardId());

		if (ownCard.isPresent()) {
			return cardMapper.toCardDetailResponse(ownCard.get());
		} else {
			ReceivedCard receivedCard = receivedCardRepository.findByUserIdAndCardIdAndDeletedAtIsNull(userId,
					request.cardId())
				.orElseThrow(() -> new TookException(CardErrorCode.CARD_NOT_FOUND));

			Card card = receivedCard.getCard();

			List<ReceivedCardFolder> folderRelations =
				receivedCardFolderRepository.findAllByReceivedCardIdAndDeletedAtIsNull(receivedCard.getId());

			List<FolderResponse> folderResponses = folderRelations.stream()
				.map(relation -> new FolderResponse(
					relation.getFolder().getId(),
					relation.getFolder().getName()
				))
				.collect(Collectors.toList());

			String memo = receivedCard.getMemo();

			CardDetailResponse baseResponse = cardMapper.toCardDetailResponse(card);

			return new CardDetailResponse(
				baseResponse.nickname(),
				baseResponse.job(),
				baseResponse.detailJob(),
				baseResponse.organization(),
				baseResponse.summary(),
				baseResponse.region(),
				baseResponse.interestDomain(),
				baseResponse.sns(),
				baseResponse.news(),
				baseResponse.hobby(),
				baseResponse.content(),
				baseResponse.project(),
				folderResponses,
				memo
			);
		}
	}

	@Transactional(readOnly = true)
	public CardResponse findCardOpen(CardRequest request) {
		Card card = cardRepository.findByIdAndDeletedAtIsNull(request.cardId())
			.orElseThrow(() -> new TookException(CardErrorCode.CARD_NOT_FOUND));
		return cardMapper.toCardResponse(card);
	}

	@Transactional(readOnly = true)
	public CardDetailResponse findCardDetailOpen(CardDetailRequest request) {
		Card card = cardRepository.findByIdAndDeletedAtIsNull(request.cardId())
			.orElseThrow(() -> new TookException(CardErrorCode.CARD_NOT_FOUND));
		return cardMapper.toCardDetailResponse(card);
	}

	public CareersResponse findCareers(Job job) {
		List<Career> careers = careerRepository.findAllByJob(job);
		return careersMapper.toCareersResponse(careers);
	}

	public ScrapResponse scrapLink(LinkRequest request) {
		CrawledDto crawledDto = linkExtractor.extractLink(request.link());
		return scrapMapper.toScrapResponse(crawledDto);
	}

	public String uploadProfileImage(MultipartFile profileImage) {
		return s3Service.uploadFile(profileImage, "profile/");
	}

	public void createCard(User user, AddCardRequest request, String profileImageKey) {
		Long currentCardCount = cardRepository.countByUserIdAndDeletedAtIsNull(user.getId());

		// if (currentCardCount >= 3) {
		// 	throw new TookException(CardErrorCode.CARD_LIMIT_EXCEEDED);
		// }

		Card newCard = Card.builder()
			.user(user)
			.imagePath(profileImageKey)
			.nickname(request.nickname())
			.career(Career.toEntity(request.detailJobId()))
			.interestDomain(request.interestDomain())
			.summary(request.summary())
			.organization(request.organization())
			.sns(snsMapper.toEntity(request.sns()))
			.region(request.region())
			.hobby(request.hobby())
			.news(request.news())
			.content(contentMapper.toEntity(request.content()))
			.project(projectMapper.toEntity(request.project()))
			.previewInfo(request.previewInfoType())
			.build();

		cardRepository.save(newCard);
	}

	@Transactional
	public void createFolder(User user, AddFolderRequest request) {
		Folder newFolder = Folder.builder()
			.user(user)
			.name(request.name())
			.build();

		folderRepository.save(newFolder);
	}

	@Transactional(readOnly = true)
	public FoldersResponse findFolders(User user) {
		List<Folder> folders = folderRepository.findAllByUserIdAndDeletedAtIsNull(user.getId());
		return folderMapper.toFoldersResponse(folders);
	}

	@Transactional
	public void updateFolder(User user, FixFolderRequest request) {
		Folder folder = folderRepository.findById(request.folderId())
			.orElseThrow(() -> new TookException(FolderErrorCode.FOLDER_NOT_FOUND));

		if (!folder.getUser().getId().equals(user.getId())) {
			throw new TookException(FolderErrorCode.FOLDER_ACCESS_DENIED);
		}

		if (folder.getDeletedAt() != null) {
			throw new TookException(FolderErrorCode.FOLDER_ALREADY_DELETED);
		}

		folder.updateName(request.name());
	}

	@Transactional
	public void deleteFolder(User user, RemoveFolderRequest request) {
		Folder folder = verifyFolderAccess(user, request.folderId());
		folder.softDelete();

		softDeleteFolderRelations(folder.getId());
	}

	@Transactional
	public void receiveCard(User user, ReceiveCardRequest request) {
		Card card = findCardOrThrow(request.cardId());

		if (card.getUser().getId().equals(user.getId())) {
			throw new TookException(CardErrorCode.CANNOT_RECEIVE_OWN_CARD);
		}

		boolean alreadyReceived = receivedCardRepository.existsByUserIdAndCardIdAndDeletedAtIsNull(
			user.getId(), request.cardId());

		if (alreadyReceived) {
			throw new TookException(CardErrorCode.ALREADY_RECEIVED_CARD);
		}

		ReceivedCard receivedCard = ReceivedCard.builder()
			.user(user)
			.card(card)
			.build();

		receivedCardRepository.save(receivedCard);
	}

	@Transactional
	public void setReceivedCardsFolder(User user, SetReceivedCardsFolderRequest request) {
		Folder folder = verifyFolderAccess(user, request.folderId());

		for (Long cardId : request.cardIds()) {
			ReceivedCard receivedCard = findReceivedCardByUserAndCardId(user.getId(), cardId);

			addToFolderIfNotExists(folder, receivedCard);
		}
	}

	@Transactional(readOnly = true)
	public ReceivedCardListResponse findReceivedCards(User user, ReceivedCardsRequest request) {
		List<Card> cards;

		if (request != null && request.folderId() != null) {
			Folder folder = verifyFolderAccess(user, request.folderId());

			List<ReceivedCardFolder> receivedCardFolders =
				receivedCardFolderRepository.findAllByFolderIdAndDeletedAtIsNull(folder.getId());

			if (receivedCardFolders.isEmpty()) {
				return new ReceivedCardListResponse(new ArrayList<>());
			}

			cards = receivedCardFolders.stream()
				.map(ReceivedCardFolder::getReceivedCard)
				.filter(rc -> rc.getDeletedAt() == null)
				.map(ReceivedCard::getCard)
				.collect(Collectors.toList());
		} else {
			cards = receivedCardRepository.findAllByUserIdAndDeletedAtIsNull(user.getId())
				.stream()
				.map(ReceivedCard::getCard)
				.collect(Collectors.toList());
		}

		return cardMapper.toCardListResponse(cards);
	}

	@Transactional
	public void removeReceivedCards(User user, RemoveReceivedCardsRequest request) {
		List<ReceivedCard> cardsToDelete = new ArrayList<>();

		for (Long cardId : request.cardIds()) {
			ReceivedCard receivedCard = findReceivedCardByUserAndCardId(user.getId(), cardId);
			cardsToDelete.add(receivedCard);
		}

		for (ReceivedCard card : cardsToDelete) {
			card.softDelete();
			softDeleteCardRelations(card.getId());
		}
	}

	@Transactional
	public void updateReceivedCard(User user, FixReceivedCardRequest request) {
		ReceivedCard receivedCard = findReceivedCardByUserAndCardId(user.getId(), request.cardId());
		receivedCard.updateMemo(request.memo());
	}

	private Folder verifyFolderAccess(User user, Long folderId) {
		Folder folder = folderRepository.findById(folderId)
			.orElseThrow(() -> new TookException(FolderErrorCode.FOLDER_NOT_FOUND));

		if (!folder.getUser().getId().equals(user.getId())) {
			throw new TookException(FolderErrorCode.FOLDER_ACCESS_DENIED);
		}

		if (folder.getDeletedAt() != null) {
			throw new TookException(FolderErrorCode.FOLDER_ALREADY_DELETED);
		}

		return folder;
	}

	private ReceivedCard findReceivedCardByUserAndCardId(Long userId, Long cardId) {
		return receivedCardRepository.findByUserIdAndCardIdAndDeletedAtIsNull(userId, cardId)
			.orElseThrow(() -> new TookException(CardErrorCode.RECEIVED_CARD_NOT_FOUND));
	}

	private Card findCardOrThrow(Long cardId) {
		return cardRepository.findById(cardId)
			.orElseThrow(() -> new TookException(CardErrorCode.CARD_NOT_FOUND));
	}

	private void addToFolderIfNotExists(Folder folder, ReceivedCard receivedCard) {
		boolean alreadyExists = receivedCardFolderRepository.existsByFolderIdAndReceivedCardIdAndDeletedAtIsNull(
			folder.getId(), receivedCard.getId());

		if (!alreadyExists) {
			ReceivedCardFolder receivedCardFolder = ReceivedCardFolder.builder()
				.folder(folder)
				.receivedCard(receivedCard)
				.build();

			receivedCardFolderRepository.save(receivedCardFolder);
		}
	}

	private void softDeleteFolderRelations(Long folderId) {
		List<ReceivedCardFolder> relations =
			receivedCardFolderRepository.findAllByFolderIdAndDeletedAtIsNull(folderId);

		for (ReceivedCardFolder relation : relations) {
			relation.softDelete();
		}
	}

	private void softDeleteCardRelations(Long receivedCardId) {
		List<ReceivedCardFolder> relations =
			receivedCardFolderRepository.findAllByReceivedCardIdAndDeletedAtIsNull(receivedCardId);

		for (ReceivedCardFolder relation : relations) {
			relation.softDelete();
		}

	}
}
