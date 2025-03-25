package com.evenly.took.feature.card.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.evenly.took.feature.card.domain.Folder;

public interface FolderRepository extends JpaRepository<Folder, Long> {
	List<Folder> findAllByUserIdAndDeletedAtIsNull(Long userId);
}
