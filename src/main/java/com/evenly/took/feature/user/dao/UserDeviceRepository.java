package com.evenly.took.feature.user.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.evenly.took.feature.user.domain.User;
import com.evenly.took.feature.user.domain.UserDevice;

public interface UserDeviceRepository extends JpaRepository<UserDevice, Long> {

	List<UserDevice> findByUser(User user);

	boolean existsByUserAndExpoToken(User user, String expoToken);
}
