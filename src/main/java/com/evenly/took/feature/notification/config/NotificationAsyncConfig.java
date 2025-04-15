package com.evenly.took.feature.notification.config;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.evenly.took.global.exception.AsyncErrorHandler;

@EnableAsync
@Configuration
public class NotificationAsyncConfig implements AsyncConfigurer {

	@Override
	public Executor getAsyncExecutor() {
		int processors = Runtime.getRuntime().availableProcessors();
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(processors * 5);
		executor.setMaxPoolSize(processors * 10);
		executor.setQueueCapacity(500);
		executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
		executor.setThreadNamePrefix("NotificationAsyncExecutor-");
		executor.setBeanName("NotificationAsyncExecutor");
		executor.initialize();
		return executor;
	}

	@Override
	public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
		return new AsyncErrorHandler();
	}
}
