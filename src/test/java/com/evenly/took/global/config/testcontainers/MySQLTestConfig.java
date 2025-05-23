package com.evenly.took.global.config.testcontainers;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class MySQLTestConfig {

	@Bean
	@ServiceConnection
	MySQLContainer<?> mysqlContainer() {
		return new MySQLContainer<>(
			DockerImageName.parse("mysql/mysql-server:8.0.26")
				.asCompatibleSubstituteFor("mysql"))
			.withDatabaseName("testdb")
			.withUsername("test")
			.withPassword("test");
	}
}
