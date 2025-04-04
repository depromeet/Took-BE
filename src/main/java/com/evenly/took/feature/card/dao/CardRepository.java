package com.evenly.took.feature.card.dao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.evenly.took.feature.card.domain.Card;

public interface CardRepository extends JpaRepository<Card, Long> {

	Optional<Card> findByUserIdAndIdAndDeletedAtIsNull(Long userId, Long cardId);

	List<Card> findAllByUserId(Long userId);

	Long countByUserIdAndDeletedAtIsNull(Long userId);

	List<Card> findAllByUserIdAndDeletedAtIsNull(Long userId);

	Optional<Card> findByIdAndDeletedAtIsNull(Long cardId);

	@Modifying(clearAutomatically = true)
	@Query("UPDATE Card c SET c.deletedAt = :now WHERE c.user.id = :userId AND c.deletedAt IS NULL")
	int softDeleteAllByUserId(@Param("userId") Long userId, @Param("now") LocalDateTime now);
}
