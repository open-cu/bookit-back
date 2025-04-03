package ru.tbank.bookit.book_it_backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "AREAS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Area {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AreaType type;

    @Enumerated(EnumType.STRING)
    private AreaFeature features;

    @Column(nullable = false)
    private int capacity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AreaStatus status;
}