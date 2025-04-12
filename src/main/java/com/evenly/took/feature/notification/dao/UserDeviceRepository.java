package com.evenly.took.feature.notification.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.evenly.took.feature.notification.domain.UserDevice;
import com.evenly.took.feature.user.domain.User;

public interface UserDeviceRepository extends JpaRepository<UserDevice, Long> {

	List<UserDevice> findByUser(User user);
}
