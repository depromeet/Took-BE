package com.evenly.took.feature.user.domain.vo;

import java.util.concurrent.atomic.AtomicInteger;

import lombok.Getter;

@Getter
public class RequestCounter {

	private final AtomicInteger count = new AtomicInteger(0);
	private long windowStart;
	private long lastActivityTime;

	public RequestCounter(long startTime) {
		this.windowStart = startTime;
		this.lastActivityTime = startTime;
	}

	public int incrementAndGet() {
		lastActivityTime = System.currentTimeMillis();
		return count.incrementAndGet();
	}

	public void resetWindow(long newStartTime) {
		count.set(0);
		windowStart = newStartTime;
		lastActivityTime = newStartTime;
	}
}
