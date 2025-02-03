package com.evenly.blok.global.util;

import static com.evenly.blok.global.common.constants.EnvironmentConstants.Constants.*;

import java.util.Arrays;
import java.util.stream.Stream;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProfileResolver {

	private final Environment environment;

	public String getCurrentProfile() {
		return getActiveProfiles()
			.filter(profile -> profile.equals(PROD_ENV) || profile.equals(DEV_ENV))
			.findFirst()
			.orElse(LOCAL_ENV);
	}

	public boolean isProdProfile() {
		return getActiveProfiles().anyMatch(PROD_ENV::equals);
	}

	public boolean isDevProfile() {
		return getActiveProfiles().anyMatch(DEV_ENV::equals);
	}

	public boolean isProdAndDevProfile() {
		return getActiveProfiles().anyMatch(PROD_AND_DEV_ENV::contains);
	}

	private Stream<String> getActiveProfiles() {
		return Arrays.stream(environment.getActiveProfiles());
	}
}
