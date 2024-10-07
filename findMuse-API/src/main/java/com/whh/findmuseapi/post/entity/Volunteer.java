package com.whh.findmuseapi.post.entity;

import com.whh.findmuseapi.common.constant.Infos.InvieteStatus;
import com.whh.findmuseapi.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
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

    public void updateStatus(InvieteStatus status) {
        this.status = status;
    }
    public void updateActiveStatus() {
        this.activeStatus = !this.activeStatus;
    }

    public static Volunteer toEntity(Post post, User user) {
        return Volunteer.builder()
                .status(InvieteStatus.Wait)
                .activeStatus(true)
                .createDate(LocalDate.now())
                .user(user)
                .post(post)
                .build();
    }

    @Builder
    public Volunteer(InvieteStatus status, boolean activeStatus, LocalDate createDate, User user, Post post) {
        this.status = status;
        this.activeStatus = activeStatus;
        this.createDate = createDate;
        this.user = user;
        this.post = post;
    }
}
