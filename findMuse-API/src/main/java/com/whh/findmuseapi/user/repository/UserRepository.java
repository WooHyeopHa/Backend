package com.whh.findmuseapi.user.repository;

import com.whh.findmuseapi.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByAccountId(String accountId);
    Optional<User> findByEmail(String email);
    Optional<User> findByRefreshToken(String refreshToken);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.userTastes WHERE u.id = :userId")
    Optional<User> findUserWithTastesById(@Param("userId") Long userId);

    boolean existsByNickname(String nickname);
}
