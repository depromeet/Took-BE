package com.evenly.took.feature.card.application;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
import com.evenly.took.feature.card.dto.request.FixCardRequest;
import com.evenly.took.feature.card.dto.request.FixFolderRequest;
import com.evenly.took.feature.card.dto.request.FixReceivedCardRequest;
import com.evenly.took.feature.card.dto.request.LinkRequest;
import com.evenly.took.feature.card.dto.request.NewReceivedCardsRequest;
import com.evenly.took.feature.card.dto.request.ReceiveCardRequest;
import com.evenly.took.feature.card.dto.request.ReceivedCardsRequest;
import com.evenly.took.feature.card.dto.request.RemoveFolderRequest;
import com.evenly.took.feature.card.dto.request.RemoveReceivedCardsRequest;
import com.evenly.took.feature.card.dto.request.SetReceivedCardsFolderRequest;
import com.evenly.took.feature.card.dto.request.SetReceivedCardsMemoRequest;
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
		List<Card> cards = cardRepository.findAllByUserIdAndDeletedAtIsNullOrderByIsPrimaryDesc(userId);

		cards.forEach(this::updatePresignedImagePath);

		return cardMapper.toMyCardListResponse(cards);
	}

	@Transactional(readOnly = true)
	public CardDetailResponse findCardDetail(Long userId, CardDetailRequest request) {
		Optional<Card> ownCard = cardRepository.findByUserIdAndIdAndDeletedAtIsNull(userId, request.cardId());

		if (ownCard.isPresent()) {
			return cardMapper.toCardDetailResponse(updatePresignedImagePath(ownCard.get()));
		} else {
			ReceivedCard receivedCard = receivedCardRepository.findByUserIdAndCardIdAndDeletedAtIsNullOrderByIdDesc(
					userId,
					request.cardId())
				.orElseThrow(() -> new TookException(CardErrorCode.CARD_NOT_FOUND));

			List<ReceivedCardFolder> folderRelations =
				receivedCardFolderRepository.findAllByReceivedCardIdAndDeletedAtIsNull(receivedCard.getId());

			List<FolderResponse> folderResponses = folderRelations.stream()
				.map(relation -> new FolderResponse(
					relation.getFolder().getId(),
					relation.getFolder().getName()
				))
				.collect(Collectors.toList());

			String memo = receivedCard.getMemo();

			CardDetailResponse baseResponse = cardMapper.toCardDetailResponse(
				updatePresignedImagePath(receivedCard.getCard()));

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
				memo,
				baseResponse.imagePath(),
				baseResponse.isPrimary(),
				baseResponse.previewInfoType(),
				baseResponse.previewInfo()
			);
		}
	}

	@Transactional(readOnly = true)
	public CardResponse findCardOpen(CardRequest request) {
		Card card = cardRepository.findByIdAndDeletedAtIsNull(request.cardId())
			.orElseThrow(() -> new TookException(CardErrorCode.CARD_NOT_FOUND));

		return cardMapper.toCardResponse(updatePresignedImagePath(card));
	}

	@Transactional(readOnly = true)
	public CardDetailResponse findCardDetailOpen(CardDetailRequest request) {
		Card card = cardRepository.findByIdAndDeletedAtIsNull(request.cardId())
			.orElseThrow(() -> new TookException(CardErrorCode.CARD_NOT_FOUND));
		return cardMapper.toCardDetailResponse(updatePresignedImagePath(card));
	}

	@Transactional(readOnly = true)
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

	@Transactional
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
			.isPrimary(isCreatingFirstCard(currentCardCount))
			.build();

		System.out.println(newCard.getIsPrimary());
		cardRepository.save(newCard);
	}

	private boolean isCreatingFirstCard(Long currentCardCount) {
		return currentCardCount == 0;
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

		boolean alreadyReceived = receivedCardRepository.existsByUserIdAndCardIdAndDeletedAtIsNullOrderByIdDesc(
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
		List<ReceivedCard> receivedCards;

		if (request != null && request.folderId() != null) {
			Folder folder = verifyFolderAccess(user, request.folderId());

			List<ReceivedCardFolder> receivedCardFolders =
				receivedCardFolderRepository.findAllByFolderIdAndDeletedAtIsNull(folder.getId());

			if (receivedCardFolders.isEmpty()) {
				return new ReceivedCardListResponse(new ArrayList<>());
			}

			receivedCards = receivedCardFolders.stream()
				.map(ReceivedCardFolder::getReceivedCard)
				.filter(rc -> rc.getDeletedAt() == null)
				.collect(Collectors.toList());
		} else {
			receivedCards = receivedCardRepository.findAllByUserIdAndDeletedAtIsNullOrderByIdDesc(user.getId());
		}

		receivedCards.forEach(rc -> updatePresignedImagePath(rc.getCard()));

		return cardMapper.toReceivedCardListResponse(receivedCards);
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

	@Transactional
	public void updateReceivedCardsMemo(User user, List<SetReceivedCardsMemoRequest.CardMemo> cardMemos) {
		for (SetReceivedCardsMemoRequest.CardMemo cardMemo : cardMemos) {
			ReceivedCard receivedCard = findReceivedCardByUserAndCardId(user.getId(), cardMemo.cardId());
			receivedCard.updateMemo(cardMemo.memo());
		}
	}

	@Transactional
	public void updateCard(User user, FixCardRequest request, MultipartFile profileImage) {
		Card card = findCardOrThrow(request.cardId());

		if (!Objects.equals(card.getUser().getId(), user.getId())) {
			throw new TookException(CardErrorCode.INVALID_CARD_OWNER);
		}

		handleImageUpdate(card, request.profileImage(), request.isImageRemoved());

		card.setNickname(request.nickname());
		card.setSummary(request.summary());
		card.setCareer(Career.toEntity(request.detailJobId()));
		card.setOrganization(request.organization());
		card.setInterestDomain(request.interestDomain());
		card.setRegion(request.region());
		card.setHobby(request.hobby());
		card.setNews(request.news());
		card.setPreviewInfo(request.previewInfoType());
		card.setSns(snsMapper.toEntity(request.sns()));
		card.setContent(contentMapper.toEntity(request.content()));
		card.setProject(projectMapper.toEntity(request.project()));
	}

	private void handleImageUpdate(Card card, MultipartFile newProfileImage, Boolean isImageRemoved) {
		String oldImageKey = card.getImagePath();

		String newImageKey = uploadProfileImage(newProfileImage);
		// Case 1: 새 이미지 업로드된 경우
		if (newProfileImage != null && !newProfileImage.isEmpty()) {
			if (oldImageKey != null && !oldImageKey.isEmpty()) {
				s3Service.deleteFile(oldImageKey);
			}
			card.setImageLink(newImageKey);
		}
		// Case 2: 새 이미지 없고, 기존 이미지 유지 신호도 없는 경우 (기존 이미지 삭제)
		else if (isImageRemoved) {
			// 기존 이미지 S3에서 삭제 (있었다면)
			if (oldImageKey != null && !oldImageKey.isEmpty()) {
				s3Service.deleteFile(oldImageKey);
			}
			card.setImageLink(newImageKey); // 기본 이미지 Key
		}
		// Case 3: 새 이미지 없고, 기존 이미지 유지 신호(originImageKey)가 있는 경우
		// -> 아무 작업도 하지 않음 (card.imagePath는 변경되지 않음)
	}

	@Transactional
	public void softDeleteAllCards(Long userId, LocalDateTime now) {
		cardRepository.softDeleteAllByUserId(userId, now);
	}

	@Transactional
	public void softDeleteAllFolders(Long userId, LocalDateTime now) {
		folderRepository.softDeleteAllByUserId(userId, now);
	}

	@Transactional
	public void softDeleteAllReceivedCards(Long userId, LocalDateTime now) {
		receivedCardRepository.softDeleteAllByUserId(userId, now);
	}

	@Transactional
	public void softDeleteAllReceivedCardFolders(Long userId, LocalDateTime now) {
		receivedCardFolderRepository.softDeleteAllByUserId(userId, now);
	}

	@Transactional
	public void softDeleteMyCard(Long userId, Long cardId) {
		Card card = findCardOrThrow(cardId);
		card.softDelete(userId);

		if (card.getIsPrimary()) {
			card.changePrimaryCard(false);
			assignNewPrimaryIfNecessary(userId);
		}
	}

	private void assignNewPrimaryIfNecessary(Long userId) {
		List<Card> remainingCards = cardRepository.findAllByUserIdAndDeletedAtIsNull(userId);

		if (!remainingCards.isEmpty()) {
			remainingCards.get(0).changePrimaryCard(true);
		}
	}

	@Transactional
	public void setPrimaryCard(Long userId, Long cardId) {
		Card targetCard = findOwnedCardOrThrow(userId, cardId);
		clearExistingPrimaryCard(userId);
		targetCard.changePrimaryCard(true);
	}

	private Card findOwnedCardOrThrow(Long userId, Long cardId) {
		Card card = cardRepository.findByIdAndDeletedAtIsNull(cardId)
			.orElseThrow(() -> new TookException(CardErrorCode.CARD_NOT_FOUND));

		if (!card.getUser().getId().equals(userId)) {
			throw new TookException(CardErrorCode.INVALID_CARD_OWNER);
		}
		return card;
	}

	private void clearExistingPrimaryCard(Long userId) {
		List<Card> cards = cardRepository.findAllByUserIdAndDeletedAtIsNull(userId);
		cards.stream()
			.filter(Card::getIsPrimary)
			.forEach(card -> card.changePrimaryCard(false));
	}

	@Transactional
	public void sendCardToUser(Long senderUserId, Long targetUserId, Long cardId) {
		if (senderUserId.equals(targetUserId)) {
			throw new TookException(CardErrorCode.CANNOT_RECEIVE_OWN_CARD);
		}

		Card card = findOwnedCardOrThrow(senderUserId, cardId);

		boolean alreadySent = receivedCardRepository.existsByUserIdAndCardIdAndDeletedAtIsNullOrderByIdDesc(
			targetUserId, cardId);
		if (alreadySent) {
			throw new TookException(CardErrorCode.ALREADY_RECEIVED_CARD);
		}

		User receiver = User.toEntity(targetUserId);
		ReceivedCard receivedCard = ReceivedCard.builder()
			.user(receiver)
			.card(card)
			.build();

		receivedCardRepository.save(receivedCard);
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
		return receivedCardRepository.findByUserIdAndCardIdAndDeletedAtIsNullOrderByIdDesc(userId, cardId)
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

	private Card updatePresignedImagePath(Card card) {
		if (card.getImagePath() != null && !card.getImagePath().isEmpty()) {
			card.setImageLink(s3Service.generatePresignedViewUrl(card.getImagePath()));
		}
		return card;
	}

	public Card findPrimaryCard(User user) {
		return cardRepository.findFirstByUserAndIsPrimaryTrueAndDeletedAtIsNull(user).orElse(null);
	}

	public List<ReceivedCard> findReceivedCardsCreatedBetween(LocalDateTime from, LocalDateTime to) {
		return receivedCardRepository.findAllByCreatedAtAndDeletedAtIsNull(from, to);
	}

	/**
	 * 새로 추가된 받은 명함 중, 내 대표명함과 관심사가 하나라도 겹치는 "흥미로운 명함" 목록을 조회합니다.
	 * 관심사 기준: 관심도메인 겹침 or 소속정보 일치 or 세부직군 일치
	 */
	@Transactional(readOnly = true)
	public ReceivedCardListResponse findInterestingNewReceivedCards(User user, NewReceivedCardsRequest request) {
		LocalDateTime baseTime = request.baseTime() != null ? request.baseTime() : LocalDateTime.now();
		LocalDateTime oneDayBefore = baseTime.minusDays(1);

		Card primaryCard = getPrimaryCard(user.getId());
		if (primaryCard == null) {
			return new ReceivedCardListResponse(new ArrayList<>());
		}

		List<ReceivedCard> newReceivedCards = receivedCardRepository.findNewReceivedCards(
			user.getId(), baseTime, oneDayBefore);

		List<ReceivedCard> interestingCards = newReceivedCards.stream()
			.filter(rc -> hasCommonInterest(primaryCard, rc.getCard()))
			.collect(Collectors.toList());

		interestingCards.forEach(rc -> updatePresignedImagePath(rc.getCard()));

		return cardMapper.toReceivedCardListResponse(interestingCards);
	}

	/**
	 * 새로 추가된 받은 명함 중, 내 대표명함과 관심사가 겹치지 않고 메모가 없는 "한줄 메모가 필요한 명함" 목록을 조회합니다.
	 * 관심사 기준: 관심도메인 겹침 or 소속정보 일치 or 세부직군 일치
	 */
	@Transactional(readOnly = true)
	public ReceivedCardListResponse findMemoNeededNewReceivedCards(User user, NewReceivedCardsRequest request) {
		LocalDateTime baseTime = request.baseTime() != null ? request.baseTime() : LocalDateTime.now();
		LocalDateTime oneDayBefore = baseTime.minusDays(1);

		Card primaryCard = getPrimaryCard(user.getId());

		List<ReceivedCard> newReceivedCards = receivedCardRepository.findNewReceivedCards(
			user.getId(), baseTime, oneDayBefore);

		List<ReceivedCard> memoNeededCards = newReceivedCards.stream()
			.filter(rc -> {
				// 메모가 있는 명함은 제외
				if (rc.getMemo() != null && !rc.getMemo().trim().isEmpty()) {
					return false;
				}

				// 대표 명함이 없는 경우, 메모가 없는 모든 명함 포함
				if (primaryCard == null) {
					return true;
				}

				// 공통 관심사가 없는 경우에만 포함
				return !hasCommonInterest(primaryCard, rc.getCard());
			})
			.collect(Collectors.toList());

		memoNeededCards.forEach(rc -> updatePresignedImagePath(rc.getCard()));

		return cardMapper.toReceivedCardListResponse(memoNeededCards);
	}

	/**
	 * 사용자의 대표 명함을 조회합니다.
	 */
	private Card getPrimaryCard(Long userId) {
		try {
			return cardRepository.findByUserIdAndIsPrimaryTrueAndDeletedAtIsNull(userId)
				.orElse(null);
		} catch (Exception e) {
			log.warn("Failed to get primary card: {}", e.getMessage());
			return null;
		}
	}

	/**
	 * 두 명함 간에 공통 관심사가 있는지 확인합니다.
	 * 관심사 기준: 관심도메인 겹침 or 소속정보 일치 or 세부직군 일치
	 */
	private boolean hasCommonInterest(Card primaryCard, Card otherCard) {
		if (hasCommonInterestDomain(primaryCard, otherCard)) {
			return true;
		}

		if (hasSameOrganization(primaryCard, otherCard)) {
			return true;
		}

		if (hasSameCareer(primaryCard, otherCard)) {
			return true;
		}

		return false;
	}

	/**
	 * 두 명함 간에 공통 관심 도메인이 있는지 확인합니다.
	 */
	private boolean hasCommonInterestDomain(Card primaryCard, Card otherCard) {
		List<String> primaryInterests = primaryCard.getInterestDomain();
		List<String> otherInterests = otherCard.getInterestDomain();

		if (primaryInterests == null || primaryInterests.isEmpty() ||
			otherInterests == null || otherInterests.isEmpty()) {
			return false;
		}

		return otherInterests.stream().anyMatch(primaryInterests::contains);
	}

	/**
	 * 두 명함의 소속정보가 일치하는지 확인합니다.
	 */
	private boolean hasSameOrganization(Card primaryCard, Card otherCard) {
		String primaryOrg = primaryCard.getOrganization();
		String otherOrg = otherCard.getOrganization();

		if (primaryOrg == null || primaryOrg.trim().isEmpty() ||
			otherOrg == null || otherOrg.trim().isEmpty()) {
			return false;
		}

		return primaryOrg.trim().equalsIgnoreCase(otherOrg.trim());
	}

	/**
	 * 두 명함의 세부직군이 일치하는지 확인합니다.
	 */
	private boolean hasSameCareer(Card primaryCard, Card otherCard) {
		Career primaryCareer = primaryCard.getCareer();
		Career otherCareer = otherCard.getCareer();

		if (primaryCareer == null || otherCareer == null) {
			return false;
		}

		// 직업 카테고리(Job) 일치 여부 확인
		if (Objects.equals(primaryCareer.getId(), otherCareer.getId())) {
			return true;
		}

		return false;
	}
}
