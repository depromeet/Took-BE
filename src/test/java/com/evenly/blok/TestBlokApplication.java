package com.evenly.blok;

import org.springframework.boot.SpringApplication;

import com.evenly.blok.global.config.TestcontainersConfig;

public class TestBlokApplication {

	public static void main(String[] args) {
		SpringApplication.from(BlokApplication::main).with(TestcontainersConfig.class).run(args);
	}

}
