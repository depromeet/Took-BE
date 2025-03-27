package com.evenly.took.global.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.util.ReflectionTestUtils;

import com.evenly.took.feature.card.dao.FolderRepository;
import com.evenly.took.feature.card.domain.Folder;
import com.evenly.took.feature.user.domain.User;

@Component
public class FolderFixture extends FolderBase {

	@Autowired
	FolderRepository folderRepository;

	@Autowired
	UserFixture userFixture;

	public FolderBase creator() {
		init();
		return this;
	}

	@Override
	public Folder create() {
		if (user == null) {
			user = userFixture.create();
		}
		Folder folder = Folder.builder()
			.user(user)
			.name(name)
			.build();

		Folder savedFolder = folderRepository.save(folder);

		if (deletedAt != null) {
			ReflectionTestUtils.setField(savedFolder, "deletedAt", deletedAt);
			return folderRepository.save(savedFolder);
		}

		return savedFolder;
	}

	// 편의 메서드
	public Folder createForUser(User user) {
		return creator().user(user).create();
	}

	public Folder createWithName(String name) {
		return creator().name(name).create();
	}

	public Folder createWithNameForUser(String name, User user) {
		return creator().name(name).user(user).create();
	}
}
