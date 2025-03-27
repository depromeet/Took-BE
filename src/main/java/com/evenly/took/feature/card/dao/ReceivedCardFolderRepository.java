package com.evenly.took.feature.card.dao;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.evenly.took.feature.card.domain.ReceivedCardFolder;

public interface ReceivedCardFolderRepository extends JpaRepository<ReceivedCardFolder, Long> {

	boolean existsByFolderIdAndReceivedCardIdAndDeletedAtIsNull(Long folderId, Long receivedCardId);

	List<ReceivedCardFolder> findAllByReceivedCardIdAndDeletedAtIsNull(Long receivedCardId);

	List<ReceivedCardFolder> findAllByFolderIdAndDeletedAtIsNull(Long folderId);

	@Modifying(clearAutomatically = true)
	@Query("UPDATE ReceivedCardFolder rcf SET rcf.deletedAt = :now " +
		"WHERE (rcf.folder.id IN (SELECT f.id FROM Folder f WHERE f.user.id = :userId) " +
		"OR rcf.receivedCard.id IN (SELECT rc.id FROM ReceivedCard rc WHERE rc.user.id = :userId)) " +
		"AND rcf.deletedAt IS NULL")
	int softDeleteAllByUserId(@Param("userId") Long userId, @Param("now") LocalDateTime now);
}
