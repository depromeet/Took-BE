package com.evenly.took.global.helper;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;
import org.springframework.test.context.jdbc.Sql;

import com.evenly.took.global.config.testcontainers.MySQLTestConfig;
import com.evenly.took.global.config.testcontainers.RedisTestConfig;

@Sql(scripts = "classpath:init.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Import({RedisTestConfig.class, MySQLTestConfig.class})
@Component
public abstract class DatabaseInitializer {

	@Autowired
	private RedisCleaner redisCleaner;

	@BeforeEach
	public void setUp() {
		redisCleaner.deleteAllKeys();
	}
}
