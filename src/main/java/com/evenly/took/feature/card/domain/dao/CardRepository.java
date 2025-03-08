package com.evenly.took.feature.card.domain.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.evenly.took.feature.card.domain.Card;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

	List<Card> findByUserIdAndDeletedAtIsNull(Long userId);
}
