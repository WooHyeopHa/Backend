package com.whh.findmuseapi.art.entity;

import com.whh.findmuseapi.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArtLike {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "art_like_id")
    private Long id;
    private boolean activeStatus;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "art_id")
    private Art art;
}
