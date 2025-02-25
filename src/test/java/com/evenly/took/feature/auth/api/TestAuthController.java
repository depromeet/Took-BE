package com.evenly.took.feature.auth.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestAuthController {

	@GetMapping("/api/test")
	public void test() {
	}
}
