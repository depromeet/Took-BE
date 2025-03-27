package com.evenly.took.global.domain;

import org.springframework.test.util.ReflectionTestUtils;

import com.evenly.took.feature.card.domain.ReceivedCardFolder;

public class ReceivedCardFolderFactory extends ReceivedCardFolderBase {

	FolderFactory folderFactory;
	ReceivedCardFactory receivedCardFactory;

	public ReceivedCardFolderBase creator() {
		return new ReceivedCardFolderFactory();
	}

	@Override
	public ReceivedCardFolder create() {
		if (folder == null) {
			folder = folderFactory.create();
		}
		if (receivedCard == null) {
			receivedCard = receivedCardFactory.create();
		}

		ReceivedCardFolder relation = ReceivedCardFolder.builder()
			.folder(folder)
			.receivedCard(receivedCard)
			.build();

		ReflectionTestUtils.setField(relation, "id", id);

		if (deletedAt != null) {
			ReflectionTestUtils.setField(relation, "deletedAt", deletedAt);
		}

		return relation;
	}
}
