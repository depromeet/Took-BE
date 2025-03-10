package com.evenly.took.feature.card.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.evenly.took.feature.card.domain.Card;

public interface CardRepository extends JpaRepository<Card, Long> {

	Optional<Card> findByUserIdAndIdAndDeletedAtIsNull(Long userId, Long cardId);
}

