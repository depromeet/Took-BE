package com.evenly.took.global.helper;

import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Component
@ActiveProfiles("test")
public class MysqlCleaner {

	@PersistenceContext
	private EntityManager entityManager;

	@Transactional
	public void execute() {
		entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 0").executeUpdate();
		clearCard();
		clearUser();
		entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1").executeUpdate();
	}

	private void clearCard() {
		entityManager.createNativeQuery("TRUNCATE TABLE cards").executeUpdate();
	}

	private void clearUser() {
		entityManager.createNativeQuery("TRUNCATE TABLE users").executeUpdate();
	}
}
