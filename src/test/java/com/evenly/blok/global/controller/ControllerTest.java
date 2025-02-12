package com.evenly.blok.global.controller;

import static com.evenly.blok.global.common.constants.EnvironmentConstants.*;

import org.junit.Ignore;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@Transactional
@ActiveProfiles(TEST_ENV)
@Ignore
public abstract class ControllerTest {

	@Autowired
	protected MockMvc mvc;

	@Autowired
	protected ObjectMapper objectMapper;
}
