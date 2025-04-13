package com.evenly.took.global.service;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.evenly.took.global.config.TestConfig;
import com.evenly.took.global.helper.DatabaseManager;

@ActiveProfiles("test")
@SpringBootTest(classes = TestConfig.class)
public abstract class ServiceTest extends DatabaseManager {

}
