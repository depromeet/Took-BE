package com.evenly.took.global.helper;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Component
@ActiveProfiles("test")
public class MysqlCleaner {

	@PersistenceContext
	private EntityManager entityManager;

	@Transactional
	public void execute() { // TODO 테이블 읽어서 한 번에 클리어
		entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 0").executeUpdate();
		clearAll();
		entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1").executeUpdate();
	}

	private void clearAll() {
		Query query = entityManager.createNativeQuery(
			"SELECT table_name FROM information_schema.tables WHERE table_schema = DATABASE()",
			String.class
		);
		List<String> tables = query.getResultList();

		for (String table : tables) {
			if (table.contains("careers")) {
				continue;
			}
			entityManager.createNativeQuery("TRUNCATE TABLE " + table).executeUpdate();
		}
	}
}
