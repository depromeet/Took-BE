package com.evenly.took.global.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.evenly.took.feature.card.dao.FolderRepository;
import com.evenly.took.feature.card.domain.Folder;
import com.evenly.took.feature.user.domain.User;

@Component
public class FolderFixture extends FolderBase {

	@Autowired
	FolderRepository folderRepository;

	public FolderBase creator() {
		init();
		return this;
	}

	@Override
	public Folder create() {
		if (user == null) {
			throw new IllegalStateException("user를 함께 입력해주세요.");
		}
		Folder folder = Folder.builder()
			.user(user)
			.name(name)
			.deletedAt(deletedAt)
			.build();
		return folderRepository.save(folder);
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
