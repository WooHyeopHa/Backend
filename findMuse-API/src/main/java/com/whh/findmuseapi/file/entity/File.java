package com.whh.findmuseapi.file.entity;

import com.whh.findmuseapi.art.entity.Art;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    private Long id;
    private String url;

    @ManyToOne
    @JoinColumn(name = "art_id")
    private Art art;
}
