package com.evenly.took;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class TookApplication {

	public static void main(String[] args) {
		SpringApplication.run(TookApplication.class, args);
	}
}
