package com.evenly.took.global.service;

import static com.evenly.took.global.common.constants.EnvironmentConstants.*;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles(TEST_ENV)
@Disabled
public abstract class MockTest {
}
