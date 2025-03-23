package com.evenly.took.feature.card.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.evenly.took.feature.card.domain.ReceivedCardFolder;

public interface ReceivedCardFolderRepository extends JpaRepository<ReceivedCardFolder, Long> {

}
