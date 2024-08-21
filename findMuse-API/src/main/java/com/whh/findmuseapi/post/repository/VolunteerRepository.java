package com.whh.findmuseapi.post.repository;

import com.whh.findmuseapi.post.entity.Volunteer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VolunteerRepository extends JpaRepository<Volunteer, Long> {
}