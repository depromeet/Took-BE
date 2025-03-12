package com.evenly.took.global.service;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.evenly.took.global.helper.DatabaseInitializer;

@ActiveProfiles("test")
@SpringBootTest
public abstract class ServiceTest extends DatabaseInitializer {

}
