package com.evenly.took;

import org.springframework.boot.SpringApplication;

import com.evenly.took.global.config.testcontainers.MySQLTestConfig;

public class TestTookApplication {

	public static void main(String[] args) {
		SpringApplication.from(TookApplication::main).with(MySQLTestConfig.class).run(args);
	}

}
