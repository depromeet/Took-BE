package com.evenly.took.global.config.properties;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.evenly.took.feature.auth.config.properties.TokenProperties;

@EnableConfigurationProperties({
	TokenProperties.class,
})
@Configuration
public class PropertiesConfig {
}
