package com.evenly.took.global.config.ssh;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Profile("local")
@Component
@Validated
public class SshTunnelingInitializer {

	@Value("${ssh.host}")
	private String sshHost;

	@Value("${ssh.user}")
	private String sshUser;

	@Value("${ssh.port}")
	private int sshPort;

	@Value("${ssh.private-key-path}")
	private String privateKey;

	@Value("${ssh.db-host}")
	private String databaseHost;

	@Value("${ssh.db-port}")
	private int databasePort;

	private Session session;

	@PreDestroy
	public void closeSsh() {
		if (session.isConnected())
			session.disconnect();
	}

	public Integer buildSshConnection() {

		Integer forwardedPort = null;

		try {
			log.info("start ssh tunneling..");
			JSch jSch = new JSch();

			log.info("creating ssh session");
			jSch.addIdentity(privateKey);  // 개인키 설정

			session = jSch.getSession(sshUser, sshHost, sshPort);  // 터널링 세션 설정

			Properties config = new Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			// 터미널에서 SSH 명령어로 처음 서버 접속 시 보이는 "Are you sure you want to continue connecting (yes/no)?" 메시지를 자동으로 "yes"로 처리하는 것과 같은 세팅

			log.info("complete creating ssh session");

			log.info("start connecting ssh connection");
			session.connect();  // ssh 연결 시도

			log.info("success connecting ssh connection ");

			// 로컬의 남는 포트 하나와 원격 접속한 인스턴스의 DB 포트 연결
			log.info("start forwarding");
			forwardedPort = session.setPortForwardingL(0, databaseHost, databasePort);

			// 로컬에서 원격 인스턴스를 터널링 하여 Private RDS 접근 성공
			log.info("successfully connected to database from local");

		} catch (Exception e) {
			log.error("SSH tunneling failed with exception", e);
			this.closeSsh();
			System.exit(1);
		}

		// 연결된 RDS 와 연결된 로컬 port 반환
		return forwardedPort;
	}
}
