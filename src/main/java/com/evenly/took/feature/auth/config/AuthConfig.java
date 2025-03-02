package com.evenly.took.feature.auth.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.evenly.took.global.config.properties.auth.KakaoProperties;

@Configuration
@EnableConfigurationProperties(KakaoProperties.class)
public class AuthConfig {
}
