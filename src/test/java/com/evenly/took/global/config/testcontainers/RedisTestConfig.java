package com.evenly.took.global.config.testcontainers;

import org.springframework.boot.test.context.TestConfiguration;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration()
public class RedisTestConfig {

	static {
		GenericContainer<?> redisContainer = new GenericContainer<>(DockerImageName.parse("redis:7.0.8-alpine"))
			.withExposedPorts(6379);

		redisContainer.start();

		System.setProperty("spring.data.redis.host", redisContainer.getHost());
		System.setProperty("spring.data.redis.port", String.valueOf(redisContainer.getMappedPort(6379)));
	}
}
