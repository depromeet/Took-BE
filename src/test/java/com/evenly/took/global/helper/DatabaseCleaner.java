package com.evenly.took.global.helper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
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
