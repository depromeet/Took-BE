package com.evenly.blok.global.common.constants;

import java.util.List;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EnvironmentConstants {

	public static final String PROD_ENV = "prod";
	public static final String DEV_ENV = "dev";
	public static final String LOCAL_ENV = "local";
	public static final List<String> PROD_AND_DEV_ENV = List.of(PROD_ENV, DEV_ENV);
}
