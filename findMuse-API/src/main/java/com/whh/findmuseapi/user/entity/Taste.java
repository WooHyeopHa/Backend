package com.whh.findmuseapi.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Taste {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "taste_id")
    private Long id;
    private String name;

    @OneToMany(mappedBy ="parent")
    private List<Taste> childs = new ArrayList<>();

    @ManyToOne()
    @JoinColumn(referencedColumnName = "taste_id", name = "parent_id")
    private Taste parent;

}
