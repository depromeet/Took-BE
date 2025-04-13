package com.evenly.took.feature.card.domain;

import java.time.LocalDateTime;

import com.evenly.took.feature.common.model.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "received_card_folders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ReceivedCardFolder extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "folder_id")
	private Folder folder;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "received_card_id")
	private ReceivedCard receivedCard;

	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;

	@Builder
	public ReceivedCardFolder(Folder folder, ReceivedCard receivedCard, LocalDateTime deletedAt) {
		this.folder = folder;
		this.receivedCard = receivedCard;
		this.deletedAt = deletedAt;
	}

	public void softDelete() {
		this.deletedAt = LocalDateTime.now();
	}
}
