package com.opencu.bookit.adapter.out.persistence.entity;

import com.opencu.bookit.domain.model.area.AreaFeature;
import com.opencu.bookit.domain.model.area.AreaStatus;
import com.opencu.bookit.domain.model.area.AreaType;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "AREAS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AreaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

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

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "area_keys", joinColumns = @JoinColumn(name = "area_id"))
    @Column(nullable = false)
    private List<String> keys;

    @OneToMany(mappedBy = "areaEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookingEntity> bookingEntities = new ArrayList<>();

    @OneToMany(mappedBy = "areaEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TicketEntity> ticketEntities = new ArrayList<>();
}