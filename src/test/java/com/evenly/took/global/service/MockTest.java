package com.evenly.took.global.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import com.evenly.took.global.domain.UserFactory;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public abstract class MockTest {

	protected UserFactory userFactory = new UserFactory();
}
