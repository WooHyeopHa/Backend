package com.whh.findmuseapi.art.repository;

import com.whh.findmuseapi.art.entity.ArtLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.Optional;
import java.util.List;

@Repository
public interface ArtLikeRepository extends JpaRepository<ArtLike, Long> {
    Optional<ArtLike> findArtLikeByArtIdAndUserId(Long art_id, Long user_id);
    List<ArtLike> findByUserId(Long userId);
}

