package com.evenly.took.feature.card.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.evenly.took.feature.card.domain.Card;

public interface CardRepository extends JpaRepository<Card, Long> {

	List<Card> findAllByUserId(Long userId);

	Long countByUserIdAndDeletedAtIsNull(Long userId);
}
