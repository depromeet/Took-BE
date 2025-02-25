package com.evenly.took.feature.user.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.evenly.took.feature.auth.domain.OAuthIdentifier;
import com.evenly.took.feature.user.domain.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByOauthIdentifier(OAuthIdentifier oauthIdentifier);
}
