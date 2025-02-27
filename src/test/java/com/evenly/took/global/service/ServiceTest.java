package com.evenly.took.global.service;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import com.evenly.took.global.config.testcontainers.RedisTestConfig;
import com.evenly.took.global.helper.RedisCleaner;

@SpringBootTest
@ActiveProfiles("test")
@Import(RedisTestConfig.class)
public abstract class ServiceTest {

	@Autowired
	private RedisCleaner redisCleaner;

	@BeforeEach
	public void setUp() {
		redisCleaner.deleteAllKeys();
	}
}
