package com.whh.findmuseapi.review.entity;

import com.whh.findmuseapi.common.constant.Infos;
import com.whh.findmuseapi.common.constant.Infos.Rating;
import com.whh.findmuseapi.post.entity.Post;
import com.whh.findmuseapi.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserReview {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_review_id")
    private Long id;
    private Rating rating; // 평가
    private String content;

    // 평가를 보낸 사람
    @ManyToOne
    @JoinColumn(referencedColumnName = "user_id", name = "sender_id")
    private User sender;

    // 평가를 받는 사람
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "user_id", name = "receiver_id")
    private User receiver;
}
