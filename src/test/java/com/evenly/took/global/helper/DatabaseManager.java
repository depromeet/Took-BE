package com.evenly.took.global.helper;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.evenly.took.global.domain.CardFixture;
import com.evenly.took.global.domain.CareerFixture;
import com.evenly.took.global.domain.FolderFixture;
import com.evenly.took.global.domain.ReceivedCardFixture;
import com.evenly.took.global.domain.ReceivedCardFolderFixture;
import com.evenly.took.global.domain.UserFixture;

@Component
public abstract class DatabaseManager {

	@Autowired
	protected DatabaseCleaner databaseCleaner;

	@Autowired
	protected UserFixture userFixture;

	@Autowired
	protected CardFixture cardFixture;

	@Autowired
	protected CareerFixture careerFixture;

	@Autowired
	protected FolderFixture folderFixture;

	@Autowired
	protected ReceivedCardFixture receivedCardFixture;

	@Autowired
	protected ReceivedCardFolderFixture receivedCardFolderFixture;

	@BeforeEach
	void setUp() {
		databaseCleaner.execute();
	}
}
