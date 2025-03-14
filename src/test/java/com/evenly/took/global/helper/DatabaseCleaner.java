package com.evenly.took.global.helper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

import com.evenly.took.global.config.testcontainers.MySQLTestConfig;
import com.evenly.took.global.config.testcontainers.RedisTestConfig;

@Component
@Import({RedisTestConfig.class, MySQLTestConfig.class, MysqlCleaner.class, RedisCleaner.class})
public class DatabaseCleaner {

	@Autowired
	MysqlCleaner mysqlCleaner;

	@Autowired
	RedisCleaner redisCleaner;

	public void execute() {
		mysqlCleaner.execute();
		redisCleaner.execute();
	}
}
