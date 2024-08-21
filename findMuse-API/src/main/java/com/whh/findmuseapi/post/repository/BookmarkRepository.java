package com.whh.findmuseapi.post.repository;

import com.whh.findmuseapi.post.entity.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
}