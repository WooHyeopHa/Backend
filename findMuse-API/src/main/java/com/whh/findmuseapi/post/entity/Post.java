package com.whh.findmuseapi.post.entity;

import com.whh.findmuseapi.art.entity.Art;
import com.whh.findmuseapi.common.constant.Infos;
import com.whh.findmuseapi.common.constant.Infos.Ages;
import com.whh.findmuseapi.post.dto.request.PostUpdateRequest;
import com.whh.findmuseapi.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.util.Collections;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;
    @NotNull
    private String title;
    @NotNull
    private String content;
    @NotNull
    private String place;
    @NotNull
    private LocalDate createDate;
    @NotNull
    private LocalDate endDate;
    @NotNull
    private int inviteCount;
    @NotNull
    private int viewCount;
    @Enumerated(EnumType.STRING)
    private Ages ages; //선호 연령

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "art_id")
    private Art art;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
    private List<Volunteer> volunteeredList = new ArrayList<>();

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
    private List<PostTag> tagList = new ArrayList<>();

    public void updateTagList(List<PostTag> tagList) {
        this.tagList = tagList;
    }
    public void updateCount(){
        this.viewCount = Math.addExact(1, this.viewCount);
    }

    public void updatePost(PostUpdateRequest updateRequest,Art newArt,List<PostTag> postTagList) {
        this.title = updateRequest.getTitle();
        this.content = updateRequest.getContent();
        this.place = updateRequest.getPlace();
        this.endDate = updateRequest.getEndDate();
        this.inviteCount = updateRequest.getInviteCount();
        this.ages = Infos.Ages.valueOf(updateRequest.getAges());
        this.art = newArt;
        this.tagList = postTagList;
    }

    @Builder
    public Post(String title, String content, String place, LocalDate endDate, int inviteCount, Ages ages, Art art,
                User user) {
        this.title = title;
        this.content = content;
        this.place = place;
        this.createDate = LocalDate.now();
        this.endDate = endDate;
        this.inviteCount = inviteCount;
        this.viewCount = 0;
        this.ages = ages;
        this.art = art;
        this.user = user;
        this.volunteeredList = Collections.emptyList();
        this.tagList = Collections.emptyList();
    }
}
