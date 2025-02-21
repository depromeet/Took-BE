package com.evenly.took.domain.user.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.evenly.took.domain.auth.domain.OAuthIdentifier;
import com.evenly.took.domain.user.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByOauthIdentifier(OAuthIdentifier oauthIdentifier);
}
