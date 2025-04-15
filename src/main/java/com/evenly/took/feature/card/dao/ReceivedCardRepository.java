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
	
	/**
	 * 특정 사용자의 baseTime 이전 하루 동안 새로 추가된 받은 명함 목록을 조회합니다.
	 */
	@Query("SELECT rc FROM ReceivedCard rc WHERE rc.user.id = :userId AND rc.deletedAt IS NULL " +
	       "AND rc.createdAt < :baseTime AND rc.createdAt >= :oneDayBefore " +
	       "ORDER BY rc.id DESC")
	List<ReceivedCard> findNewReceivedCards(
		@Param("userId") Long userId, 
		@Param("baseTime") LocalDateTime baseTime, 
		@Param("oneDayBefore") LocalDateTime oneDayBefore
	);
}
