package com.evenly.took.feature.user.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.evenly.took.feature.user.domain.BlacklistedIP;

@Repository
public interface BlacklistedIPRepository extends JpaRepository<BlacklistedIP, Long> {

	Optional<BlacklistedIP> findByIpAddress(String ipAddress);
}
