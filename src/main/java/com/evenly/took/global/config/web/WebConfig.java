package com.evenly.took.global.config.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.evenly.took.global.auth.resolver.LoginUserArgumentResolver;
import com.evenly.took.global.request.StringToListObjectConverter;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Autowired
	private StringToListObjectConverter stringToListObjectConverter;

	@Autowired
	private LoginUserArgumentResolver loginUserArgumentResolver;

	@Override
	public void addFormatters(FormatterRegistry registry) {
		registry.addConverter(stringToListObjectConverter);
	}

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		resolvers.add(loginUserArgumentResolver);
	}
}
