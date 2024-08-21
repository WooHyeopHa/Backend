package com.whh.findmuseapi.post.repository;

import com.whh.findmuseapi.post.entity.PostTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostTagRepository extends JpaRepository<PostTag, Long> {
}