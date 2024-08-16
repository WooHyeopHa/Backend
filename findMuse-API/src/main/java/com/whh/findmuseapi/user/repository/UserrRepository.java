package com.whh.findmuseapi.user.repository;

import com.whh.findmuseapi.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserrRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByAccountId(String accountId);
}