package com.whh.findmuseapi.art.repository;

import com.whh.findmuseapi.art.entity.ArtHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArtHistoryRepository extends JpaRepository<ArtHistory, Long> {
    List<ArtHistory> findByUserId(Long userId);
}
