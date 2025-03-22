package com.evenly.took.global.config;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import com.evenly.took.feature.notification.config.FcmConfig;

@TestConfiguration
public class FcmTestConfig {

	@Bean
	@Primary
	public FcmConfig fcmConfig() {
		return Mockito.mock(FcmConfig.class);
	}
}
