package com.whh.findmuseapi.art.repository;

import com.whh.findmuseapi.art.entity.ArtLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArtLikeRepository extends JpaRepository<ArtLike, Long> {
    List<ArtLike> findByUserId(Long userId);
}