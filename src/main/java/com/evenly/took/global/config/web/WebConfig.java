package com.evenly.took.global.config.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.evenly.took.global.request.StringToListObjectConverter;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Autowired
	private StringToListObjectConverter stringToListObjectConverter;

	@Override
	public void addFormatters(FormatterRegistry registry) {
		registry.addConverter(stringToListObjectConverter);
	}
}
