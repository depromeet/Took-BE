package com.evenly.took.feature.notification.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.evenly.took.feature.notification.exception.NotificationErrorCode;
import com.evenly.took.global.exception.TookException;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class FcmConfig {

	@Value("${fcm.key-path}")
	private String keyPath;

	@PostConstruct
	public void initialize() {
		if (!FirebaseApp.getApps().isEmpty()) {
			log.info("FCM 앱 실행 성공");
			return;
		}
		try (InputStream key = new FileInputStream(keyPath)) {
			FirebaseApp.initializeApp(options(key));
			log.info("FCM 앱 초기화 성공");
		} catch (IOException ex) {
			log.error("FCM 앱 초기화 실패", ex);
			throw new TookException(NotificationErrorCode.FCM_SERVER_ERROR);
		}
	}

	private FirebaseOptions options(InputStream key) throws IOException {
		return FirebaseOptions.builder()
			.setCredentials(GoogleCredentials.fromStream(key))
			.build();
	}
}
