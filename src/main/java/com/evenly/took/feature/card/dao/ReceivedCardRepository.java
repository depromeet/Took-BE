package com.evenly.took.feature.card.dao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.evenly.took.feature.card.domain.ReceivedCard;

public interface ReceivedCardRepository extends JpaRepository<ReceivedCard, Long> {

	boolean existsByUserIdAndCardIdAndDeletedAtIsNull(Long userId, Long cardId);

	List<ReceivedCard> findAllByUserIdAndDeletedAtIsNull(Long userId);

	Optional<ReceivedCard> findByUserIdAndCardIdAndDeletedAtIsNull(Long userId, Long cardId);

	@Modifying(clearAutomatically = true)
	@Query("UPDATE ReceivedCard rc SET rc.deletedAt = :now WHERE rc.user.id = :userId AND rc.deletedAt IS NULL")
	int softDeleteAllByUserId(@Param("userId") Long userId, @Param("now") LocalDateTime now);
}
