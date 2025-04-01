package com.evenly.took.global.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.evenly.took.feature.card.dao.ReceivedCardFolderRepository;
import com.evenly.took.feature.card.domain.Folder;
import com.evenly.took.feature.card.domain.ReceivedCard;
import com.evenly.took.feature.card.domain.ReceivedCardFolder;

@Component
public class ReceivedCardFolderFixture extends ReceivedCardFolderBase {

	@Autowired
	ReceivedCardFolderRepository receivedCardFolderRepository;

	public ReceivedCardFolderBase creator() {
		init();
		return this;
	}

	@Override
	public ReceivedCardFolder create() {
		if (folder == null) {
			throw new IllegalStateException("folder를 함께 입력해주세요.");
		}
		if (receivedCard == null) {
			throw new IllegalStateException("receivedCard를 함께 입력해주세요.");
		}
		ReceivedCardFolder relation = ReceivedCardFolder.builder()
			.folder(folder)
			.receivedCard(receivedCard)
			.deletedAt(deletedAt)
			.build();
		return receivedCardFolderRepository.save(relation);
	}

	// 편의 메서드
	public ReceivedCardFolder createRelation(Folder folder, ReceivedCard receivedCard) {
		return creator().folder(folder).receivedCard(receivedCard).create();
	}
}
