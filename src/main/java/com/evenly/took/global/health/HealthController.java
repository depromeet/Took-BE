package com.evenly.took.global.health;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.evenly.took.global.auth.meta.PublicApi;
import com.evenly.took.global.monitoring.slack.SlackErrorAlert;

@SlackErrorAlert
@RestController
@RequestMapping("/api")
public class HealthController implements HealthApi {

	@PublicApi
	@GetMapping("/health")
	public String healthCheck() {
		return "ok";
	}
}
