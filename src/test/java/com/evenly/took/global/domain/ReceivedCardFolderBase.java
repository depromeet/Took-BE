package com.evenly.took.global.domain;

import java.time.LocalDateTime;

import com.evenly.took.feature.card.domain.Folder;
import com.evenly.took.feature.card.domain.ReceivedCard;
import com.evenly.took.feature.card.domain.ReceivedCardFolder;

public abstract class ReceivedCardFolderBase {

	static Long DEFAULT_ID = 1L;
	static Folder DEFAULT_FOLDER = null;
	static ReceivedCard DEFAULT_RECEIVED_CARD = null;
	static LocalDateTime DEFAULT_DELETED_AT = null;

	Long id;
	Folder folder;
	ReceivedCard receivedCard;
	LocalDateTime deletedAt;

	protected ReceivedCardFolderBase() {
		init();
	}

	protected void init() {
		this.id = DEFAULT_ID;
		this.folder = DEFAULT_FOLDER;
		this.receivedCard = DEFAULT_RECEIVED_CARD;
		this.deletedAt = DEFAULT_DELETED_AT;
	}

	public ReceivedCardFolderBase id(Long id) {
		this.id = id;
		return this;
	}

	public ReceivedCardFolderBase folder(Folder folder) {
		this.folder = folder;
		return this;
	}

	public ReceivedCardFolderBase receivedCard(ReceivedCard receivedCard) {
		this.receivedCard = receivedCard;
		return this;
	}

	public ReceivedCardFolderBase deletedAt(LocalDateTime deletedAt) {
		this.deletedAt = deletedAt;
		return this;
	}

	public abstract ReceivedCardFolder create();
}
