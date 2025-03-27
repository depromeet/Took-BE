package com.evenly.took.feature.card.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.evenly.took.feature.card.domain.ReceivedCardFolder;

public interface ReceivedCardFolderRepository extends JpaRepository<ReceivedCardFolder, Long> {

	boolean existsByFolderIdAndReceivedCardIdAndDeletedAtIsNull(Long folderId, Long receivedCardId);

	List<ReceivedCardFolder> findAllByReceivedCardIdAndDeletedAtIsNull(Long receivedCardId);

	List<ReceivedCardFolder> findAllByFolderIdAndDeletedAtIsNull(Long folderId);
}
