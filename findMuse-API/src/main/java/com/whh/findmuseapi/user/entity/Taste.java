package com.whh.findmuseapi.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Taste {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "taste_id")
    private Long id;

}
