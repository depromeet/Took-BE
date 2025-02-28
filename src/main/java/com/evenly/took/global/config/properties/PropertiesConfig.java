package com.evenly.took.global.config.properties;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.evenly.took.global.config.properties.jwt.AuthProperties;

@EnableConfigurationProperties({
	AuthProperties.class,
})
@Configuration
public class PropertiesConfig {
}
