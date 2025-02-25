package com.evenly.took.global.service;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import com.evenly.took.global.config.testcontainers.RedisTestConfig;

@SpringBootTest
@ActiveProfiles("test")
@Import(RedisTestConfig.class)
public abstract class ServiceTest {
}
