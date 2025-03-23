package com.evenly.took.feature.card.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.evenly.took.feature.card.domain.ReceivedCard;

public interface ReceivedCardRepository extends JpaRepository<ReceivedCard, Long> {

	boolean existsByUserIdAndCardIdAndDeletedAtIsNull(Long userId, Long cardId);

	List<ReceivedCard> findAllByUserIdAndDeletedAtIsNull(Long userId);

	Optional<ReceivedCard> findByUserIdAndCardIdAndDeletedAtIsNull(Long userId, Long cardId);

}
