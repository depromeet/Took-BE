package com.evenly.took.feature.user.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "blacklisted_ips")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class BlacklistedIP {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "ip_address", nullable = false, unique = true)
	private String ipAddress;

	@Column(name = "blocked_at", nullable = false)
	private LocalDateTime blockedAt;

	@Column(name = "reason", length = 500)
	private String reason;

	@Builder
	private BlacklistedIP(String ipAddress, String reason) {
		this.ipAddress = ipAddress;
		this.blockedAt = LocalDateTime.now();
		this.reason = reason;
	}

	public static BlacklistedIP of(String ipAddress, String reason) {
		return BlacklistedIP.builder()
			.ipAddress(ipAddress)
			.reason(reason)
			.build();
	}
}
