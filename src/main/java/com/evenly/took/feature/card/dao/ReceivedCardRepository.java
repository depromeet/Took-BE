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

	boolean existsByUserIdAndCardIdAndDeletedAtIsNullOrderByIdDesc(Long userId, Long cardId);

	List<ReceivedCard> findAllByUserIdAndDeletedAtIsNullOrderByIdDesc(Long userId);

	Optional<ReceivedCard> findByUserIdAndCardIdAndDeletedAtIsNullOrderByIdDesc(Long userId, Long cardId);

	@Modifying(clearAutomatically = true)
	@Query("UPDATE ReceivedCard rc SET rc.deletedAt = :now WHERE rc.user.id = :userId AND rc.deletedAt IS NULL")
	int softDeleteAllByUserId(@Param("userId") Long userId, @Param("now") LocalDateTime now);

	@Query("""
		SELECT rc
		FROM ReceivedCard rc
		WHERE rc.createdAt >= :from AND rc.createdAt <= :to AND rc.deletedAt IS NULL
		""")
	List<ReceivedCard> findAllByCreatedAtAndDeletedAtIsNull(LocalDateTime from, LocalDateTime to);
}
