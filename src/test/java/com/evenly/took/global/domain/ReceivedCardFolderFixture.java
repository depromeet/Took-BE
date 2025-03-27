package com.evenly.took.global.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.util.ReflectionTestUtils;

import com.evenly.took.feature.card.dao.ReceivedCardFolderRepository;
import com.evenly.took.feature.card.domain.Folder;
import com.evenly.took.feature.card.domain.ReceivedCard;
import com.evenly.took.feature.card.domain.ReceivedCardFolder;

@Component
public class ReceivedCardFolderFixture extends ReceivedCardFolderBase {

	@Autowired
	ReceivedCardFolderRepository receivedCardFolderRepository;

	@Autowired
	FolderFixture folderFixture;

	@Autowired
	ReceivedCardFixture receivedCardFixture;

	public ReceivedCardFolderBase creator() {
		init();
		return this;
	}

	@Override
	public ReceivedCardFolder create() {
		if (folder == null) {
			folder = folderFixture.create();
		}
		if (receivedCard == null) {
			// 폴더와 수신된 카드는 동일한 사용자에게 속해야 함
			receivedCard = receivedCardFixture.creator().user(folder.getUser()).create();
		}

		ReceivedCardFolder relation = ReceivedCardFolder.builder()
			.folder(folder)
			.receivedCard(receivedCard)
			.build();

		ReceivedCardFolder savedRelation = receivedCardFolderRepository.save(relation);

		if (deletedAt != null) {
			ReflectionTestUtils.setField(savedRelation, "deletedAt", deletedAt);
			return receivedCardFolderRepository.save(savedRelation);
		}

		return savedRelation;
	}

	// 편의 메서드
	public ReceivedCardFolder createRelation(Folder folder, ReceivedCard receivedCard) {
		return creator().folder(folder).receivedCard(receivedCard).create();
	}
}
