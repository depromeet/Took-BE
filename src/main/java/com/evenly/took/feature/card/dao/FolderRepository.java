package com.evenly.took.feature.card.dao;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.evenly.took.feature.card.domain.Folder;

public interface FolderRepository extends JpaRepository<Folder, Long> {
	List<Folder> findAllByUserIdAndDeletedAtIsNull(Long userId);

	@Modifying(clearAutomatically = true)
	@Query("UPDATE Folder f SET f.deletedAt = :now WHERE f.user.id = :userId AND f.deletedAt IS NULL")
	int softDeleteAllByUserId(@Param("userId") Long userId, @Param("now") LocalDateTime now);
}
