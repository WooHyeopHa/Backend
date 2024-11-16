package com.whh.findmuseapi.user.repository;

import com.whh.findmuseapi.user.entity.BlackUser;
import com.whh.findmuseapi.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BlackUserRepository extends JpaRepository<BlackUser, Long> {
    boolean existsByUserAndTargetUser(User user, User targetUser);
    List<BlackUser> findAllByUser(User user);
    Optional<BlackUser> findByUserAndTargetUser(User user, User targetUser);

}
