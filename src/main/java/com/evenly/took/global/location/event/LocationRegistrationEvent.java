package com.evenly.took.global.location.event;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;

@Getter
public class LocationRegistrationEvent extends ApplicationEvent {

	private final Long userId;
	private final double longitude;
	private final double latitude;

	public LocationRegistrationEvent(Object source, Long userId, double longitude, double latitude) {
		super(source);
		this.userId = userId;
		this.longitude = longitude;
		this.latitude = latitude;
	}
}
