package com.whh.findmuseapi.user.repository;

import com.whh.findmuseapi.user.entity.User;
import com.whh.findmuseapi.user.entity.UserTaste;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserTasteRepository extends JpaRepository<UserTaste, Long> {
    List<UserTaste> findAllByUser(User user);
}
