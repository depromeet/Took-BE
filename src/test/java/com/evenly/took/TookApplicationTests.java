package com.evenly.took;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.evenly.took.global.config.TestConfig;

@ActiveProfiles("test")
@SpringBootTest(classes = TestConfig.class)
class TookApplicationTests {

	@Test
	void contextLoads() {
	}

}
