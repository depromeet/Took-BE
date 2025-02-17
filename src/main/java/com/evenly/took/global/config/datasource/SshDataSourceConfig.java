package com.evenly.took.global.config.datasource;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.evenly.took.global.config.ssh.SshTunnelingInitializer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Profile("local")
@Configuration
@RequiredArgsConstructor
public class SshDataSourceConfig {

	private final SshTunnelingInitializer initializer;
	private final DataSourceProperties properties;

	@Bean
	public DataSource dataSource() {
		Integer forwardedPort = initializer.buildSshConnection();
		String url = properties.getUrl().replace("[forwardedPort]", forwardedPort.toString());

		log.info("Configuring DataSource with URL: {}", url);

		return DataSourceBuilder.create()
			.url(url)
			.username(properties.getUsername())
			.password(properties.getPassword())
			.driverClassName(properties.getDriverClassName())
			.build();
	}
}
