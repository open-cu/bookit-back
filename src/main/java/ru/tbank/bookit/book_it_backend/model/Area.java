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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String features;

    @Column(nullable = false)
    private int capacity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AreaStatus status;
}