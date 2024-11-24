package com.whh.findmuseapi.review.entity;

import com.whh.findmuseapi.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArtReviewLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "art_review_like_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "art_review_id")
    private ArtReview artReview;

    public ArtReviewLike(User user, ArtReview artReview) {
        updateRelation(user, artReview);
    }

    private void updateRelation(User user, ArtReview artReview) {
        this.user = user;
        this.artReview = artReview;
        user.getReviewLikes().add(this);
        artReview.getReviewLikes().add(this);
    }
}
