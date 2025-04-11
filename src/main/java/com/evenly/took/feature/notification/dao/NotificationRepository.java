package com.evenly.took.feature.notification.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.evenly.took.feature.notification.domain.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

}
