package com.whh.findmuseapi.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BlackUser {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "black_user")
    private Long id;
    private boolean activeStatus;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
