package com.whh.findmuseapi.art.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_id")
    private Long id;
    private String name;
    private String url;

    @ManyToOne
    @JoinColumn(name = "art_id")
    private Art art;

    @Builder
    public Ticket(String name, String url, Art art) {
        this.name = name;
        this.url = url;
        this.art = art;
    }
}
