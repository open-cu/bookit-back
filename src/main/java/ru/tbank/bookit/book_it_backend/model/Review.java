package ru.tbank.bookit.book_it_backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "REVIEWS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private long id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private byte rating;

    @Column()
    private String comment;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}