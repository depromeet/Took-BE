package com.evenly.took.feature.card.dao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.evenly.took.feature.card.domain.Card;
import com.evenly.took.feature.user.domain.User;

public interface CardRepository extends JpaRepository<Card, Long> {

	Optional<Card> findByUserIdAndIdAndDeletedAtIsNull(Long userId, Long cardId);

	List<Card> findAllByUserId(Long userId);

	Long countByUserIdAndDeletedAtIsNull(Long userId);

	List<Card> findAllByUserIdAndDeletedAtIsNull(Long userId);

	@Query(value = """
		SELECT * FROM cards
		WHERE user_id = :userId
		  AND deleted_at IS NULL
		ORDER BY is_primary DESC, id ASC
		""", nativeQuery = true)
	List<Card> findAllByUserIdAndDeletedAtIsNullOrderByIsPrimaryDesc(@Param("userId") Long userId);

	Optional<Card> findByIdAndDeletedAtIsNull(Long cardId);

	@Modifying(clearAutomatically = true)
	@Query("UPDATE Card c SET c.deletedAt = :now WHERE c.user.id = :userId AND c.deletedAt IS NULL")
	int softDeleteAllByUserId(@Param("userId") Long userId, @Param("now") LocalDateTime now);

	Optional<Card> findFirstByUserAndIsPrimaryTrueAndDeletedAtIsNull(User user);

	/**
	 * 사용자의 대표 명함을 조회합니다.
	 */
	Optional<Card> findByUserIdAndIsPrimaryTrueAndDeletedAtIsNull(Long userId);
}
