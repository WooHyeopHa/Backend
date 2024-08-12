package com.whh.findmuseapi.post.entity;

import com.whh.findmuseapi.common.constant.Infos.InvieteStatus;
import com.whh.findmuseapi.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Volunteer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "volunteer_id")
    private Long id;
    @Enumerated(EnumType.STRING)
    private InvieteStatus status;
    private boolean activeStatus;
    private LocalDate createDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;
}
