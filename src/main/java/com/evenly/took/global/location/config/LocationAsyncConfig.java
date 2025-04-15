package com.evenly.took.global.location.config;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.evenly.took.global.exception.AsyncErrorHandler;

@Configuration
@EnableAsync
public class LocationAsyncConfig implements AsyncConfigurer {

	private static final int AWAIT_SECONDS = 600;

	@Override
	public Executor getAsyncExecutor() {
		int processors = Runtime.getRuntime().availableProcessors();
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(processors * 3);
		executor.setMaxPoolSize(processors * 6);
		executor.setQueueCapacity(200);
		executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
		executor.setWaitForTasksToCompleteOnShutdown(true);
		executor.setAwaitTerminationSeconds(AWAIT_SECONDS);
		executor.setThreadNamePrefix("LocationAsyncExecutor-");
		executor.setBeanName("LocationAsyncExecutor");
		executor.initialize();
		return executor;
	}

	@Override
	public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
		return new AsyncErrorHandler();
	}

}

