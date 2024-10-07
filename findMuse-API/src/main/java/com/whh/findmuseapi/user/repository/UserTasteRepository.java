package com.whh.findmuseapi.user.repository;

import com.whh.findmuseapi.user.entity.User;
import com.whh.findmuseapi.user.entity.UserTaste;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserTasteRepository extends JpaRepository<UserTaste, Long> {
}
