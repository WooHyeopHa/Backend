package com.whh.findmuseapi.user.repository;

import com.whh.findmuseapi.user.entity.Taste;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TasteRepository extends JpaRepository<Taste, Long> {
    Optional<Taste> findByName(String name);
    Optional<Taste> findByNameAndParent(String name, Taste parent);
}
