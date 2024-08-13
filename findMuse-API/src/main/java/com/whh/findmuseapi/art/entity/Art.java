package com.whh.findmuseapi.art.entity;

import com.whh.findmuseapi.file.entity.File;
import com.whh.findmuseapi.review.entity.ArtReview;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static com.whh.findmuseapi.common.constant.Infos.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Art {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "art_id")
    private Long id;
    private String title;
    private String content;
    @Enumerated(EnumType.STRING)
    private ArtType artType;
    private String location;
    private String place;
    private String startDate;
    private String endDate;
    private String callNum;
    private String price;
    private int likeCount;

    @OneToOne
    @JoinColumn(name = "setlist_id")
    private SetList setList;

    @OneToMany(mappedBy = "art", fetch = FetchType.LAZY)
    private List<ArtReview> artReviews = new ArrayList<>();

    @OneToMany(mappedBy = "art", fetch = FetchType.LAZY)
    private List<File> files = new ArrayList<>();
}
