package com.evenly.took.feature.card.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.evenly.took.feature.card.domain.Career;
import com.evenly.took.feature.card.domain.Job;

public interface CareerRepository extends JpaRepository<Career, Long> {

	List<Career> findAllByJob(Job job);
}
