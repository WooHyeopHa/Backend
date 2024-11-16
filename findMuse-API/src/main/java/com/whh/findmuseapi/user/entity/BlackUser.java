package com.whh.findmuseapi.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BlackUser {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "black_user")
    private Long id;

    // 차단한 사용자
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // 차단된 대상 사용자
    @ManyToOne
    @JoinColumn(name = "target_user_id")
    private User targetUser;

    @Builder
    public BlackUser(User user, User targetUser, boolean activeStatus) {
        this.user = user;
        this.targetUser = targetUser;
    }
}
