package com.evenly.took.global.domain;

import org.springframework.test.util.ReflectionTestUtils;

import com.evenly.took.feature.card.domain.Folder;

public class FolderFactory extends FolderBase {

	UserFactory userFactory;

	public FolderBase creator() {
		return new FolderFactory();
	}

	@Override
	public Folder create() {
		if (user == null) {
			user = userFactory.create();
		}
		Folder folder = Folder.builder()
			.user(user)
			.name(name)
			.build();
		ReflectionTestUtils.setField(folder, "id", id);
		if (deletedAt != null) {
			ReflectionTestUtils.setField(folder, "deletedAt", deletedAt);
		}
		return folder;
	}
}
