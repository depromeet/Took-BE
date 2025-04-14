package com.evenly.took.feature.notification.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.evenly.took.feature.notification.domain.Notification;
import com.evenly.took.feature.user.domain.User;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

	List<Notification> findAllByUser(User user);
}
