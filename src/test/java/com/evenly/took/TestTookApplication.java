package com.evenly.took;

import org.springframework.boot.SpringApplication;

import com.evenly.took.global.config.TestcontainersConfig;

public class TestTookApplication {

	public static void main(String[] args) {
		SpringApplication.from(TookApplication::main).with(TestcontainersConfig.class).run(args);
	}

}
