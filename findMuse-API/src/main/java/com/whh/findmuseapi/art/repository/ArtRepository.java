package com.whh.findmuseapi.art.repository;

import com.whh.findmuseapi.art.entity.Art;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtRepository extends JpaRepository<Art, Long> {
}