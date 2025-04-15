package com.evenly.took.global.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;

import com.evenly.took.global.config.testcontainers.MySQLTestConfig;
import com.evenly.took.global.config.testcontainers.RedisTestConfig;
import com.evenly.took.global.config.testcontainers.S3TestConfig;

@Import({
	MySQLTestConfig.class,
	S3TestConfig.class,
	RedisTestConfig.class
})
@TestConfiguration
public class TestConfig {
}
