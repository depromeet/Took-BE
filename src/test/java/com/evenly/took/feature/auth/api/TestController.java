package com.evenly.took.feature.auth.api;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@ActiveProfiles("test")
@RestController
public class TestController {

	@GetMapping("/api/test")
	public void test() {
	}
}
