package ru.tbank.bookit.book_it_backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "EVENTS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "event_tags",
            joinColumns = @JoinColumn(name = "event_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "tag")
    private Set<ThemeTags> tags = new HashSet<>();

    @Column(nullable = false)
    private LocalDateTime date;

    @Column(nullable = false)
    private int available_places;

    @Column
    private String user_list;

}