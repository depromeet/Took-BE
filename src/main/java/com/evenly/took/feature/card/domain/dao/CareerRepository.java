package com.evenly.took.feature.card.domain.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.evenly.took.feature.card.domain.Career;

@Repository
public interface CareerRepository extends JpaRepository<Career, Long> {

}
