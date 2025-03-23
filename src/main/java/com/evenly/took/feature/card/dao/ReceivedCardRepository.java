package com.evenly.took.feature.card.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.evenly.took.feature.card.domain.ReceivedCard;

public interface ReceivedCardRepository extends JpaRepository<ReceivedCard, Long> {

}
