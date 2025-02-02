package com.evenly.blok.global.health;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HealthController implements HealthApi {

	@GetMapping("/health")
	public String healthCheck() {
		return "ok";
	}
}
