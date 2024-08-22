package com.whh.findmuseapi.user.entity;

import com.whh.findmuseapi.alarm.entity.Alarm;
import com.whh.findmuseapi.art.entity.ArtHistory;
import com.whh.findmuseapi.art.entity.ArtLike;
import com.whh.findmuseapi.chat.entity.Chat;
import com.whh.findmuseapi.common.constant.Infos.Role;
import com.whh.findmuseapi.file.entity.File;
import com.whh.findmuseapi.post.entity.Bookmark;
import com.whh.findmuseapi.post.entity.Post;
import com.whh.findmuseapi.post.entity.Volunteer;
import com.whh.findmuseapi.review.entity.ArtReview;
import com.whh.findmuseapi.review.entity.ArtReviewLike;
import com.whh.findmuseapi.review.entity.UserReview;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static com.whh.findmuseapi.common.constant.Infos.LoginType;
import static com.whh.findmuseapi.common.constant.Infos.Gender;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;
    private String accountId;
    private String email;
    
    private String nickname;
    private String birthYear;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    private String location;
    private String comment;
    private int artCount;  //참여한 전시 횟수
    private boolean showStatus;
    private boolean alarmStatus;
    private boolean activateStatus;
    private LoginType loginType;
    
    private String accessToken;
    private String refreshToken;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id")
    private File photo;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<ArtLike> artLikes = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<ArtHistory> histories = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<ArtReview> artReviews = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<ArtReviewLike> reviewLikes = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Bookmark> bookmarks = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Chat> chatList = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Volunteer> volunteeredList = new ArrayList<>();

    //내가 받은 리뷰 목록
    @OneToMany(mappedBy = "receiver", fetch = FetchType.LAZY)
    private List<UserReview> userReviews = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Alarm> alarmList = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<BlackUser> blackList = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<ComplaintUser> complaintList = new ArrayList<>();
    
    @Builder
    public User(String accountId, String email, Role role, String refreshToken) {
        this.accountId = accountId;
        this.email = email;
        this.role = role;
        this.refreshToken = refreshToken;
    }
    
    public void authorizeUser() {
        this.role = Role.USER;
    }
    
    public void updateRefreshToken(String updatedRefreshToken) {
        this.refreshToken = updatedRefreshToken;
    }
}
