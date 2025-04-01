package com.evenly.took.global.logging.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.evenly.took.feature.user.dao.BlacklistedIPRepository;
import com.evenly.took.global.logging.filter.ContentCachingFilter;
import com.evenly.took.global.logging.filter.HttpTraceLoggingFilter;
import com.evenly.took.global.logging.filter.RateLimitingFilter;
import com.evenly.took.global.logging.logger.RequestResponseLogger;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class LoggingFilterConfig {

	private final BlacklistedIPRepository blacklistedIPRepository;
	private final RequestResponseLogger requestResponseLogger;
	private final RequestMappingHandlerMapping handlerMapping;

	@Value("${security.rate-limit.max-requests:100}")
	private int maxRequestsPerWindow;

	@Value("${security.rate-limit.window-seconds:1}")
	private int timeWindowSeconds;

	@Bean
	public FilterRegistrationBean<ContentCachingFilter> contentCachingFilter() {
		FilterRegistrationBean<ContentCachingFilter> registrationBean = new FilterRegistrationBean<>();
		registrationBean.setFilter(new ContentCachingFilter());
		registrationBean.addUrlPatterns("/*");
		registrationBean.setOrder(1);
		return registrationBean;
	}

	@Bean
	public FilterRegistrationBean<HttpTraceLoggingFilter> httpTraceLoggingFilter() {
		FilterRegistrationBean<HttpTraceLoggingFilter> registrationBean = new FilterRegistrationBean<>();
		registrationBean.setFilter(new HttpTraceLoggingFilter(requestResponseLogger, handlerMapping));
		registrationBean.addUrlPatterns("/*");
		registrationBean.setOrder(FilterRegistrationBean.LOWEST_PRECEDENCE - 10); // Spring Security 필터 이후 실행
		return registrationBean;
	}

	@Bean
	public FilterRegistrationBean<RateLimitingFilter> rateLimitingFilter() {
		FilterRegistrationBean<RateLimitingFilter> registrationBean = new FilterRegistrationBean<>();
		registrationBean.setFilter(
			new RateLimitingFilter(blacklistedIPRepository, maxRequestsPerWindow, timeWindowSeconds));
		registrationBean.addUrlPatterns("/*");
		registrationBean.setOrder(3);
		return registrationBean;
	}
}
