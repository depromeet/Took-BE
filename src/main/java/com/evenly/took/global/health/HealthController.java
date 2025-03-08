package com.evenly.took.global.health;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirements;

@RestController
@RequestMapping("/api")
@SecurityRequirements
public class HealthController implements HealthApi {

	@GetMapping("/health")
	public String healthCheck() {
		return "ok";
	}
}
