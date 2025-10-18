package com.opencu.bookit.adapter.out.persistence.entity;

import com.opencu.bookit.domain.model.area.AreaModel;
import com.opencu.bookit.domain.model.contentcategory.*;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "EVENTS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String full_description;

    @Column
    private String short_description;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "event_tags", joinColumns = @JoinColumn(name = "event_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "tag")
    private Set<ThemeTags> tags = new HashSet<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "event_formats", joinColumns = @JoinColumn(name = "event_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "format")
    private Set<ContentFormat> formats = new HashSet<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "event_times", joinColumns = @JoinColumn(name = "event_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "time")
    private Set<ContentTime> times = new HashSet<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "event_participation_formats", joinColumns = @JoinColumn(name = "event_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "participation_format")
    private Set<ParticipationFormat> participationFormats = new HashSet<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "event_target_audiences", joinColumns = @JoinColumn(name = "event_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "target_audience")
    private Set<TargetAudience> targetAudiences = new HashSet<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "event_keys", joinColumns = @JoinColumn(name = "event_id"))
    @Column(nullable = false)
    private List<String> keys;
    /**
     * stands for start of the event
     */
    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column
    private LocalDateTime endTime;

    @Column(nullable = false)
    private int available_places;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "Event_Users",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<UserEntity> users = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "area_id")
    private AreaEntity area;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "system_booking_id")
    private BookingEntity systemBooking;

    @Column(name = "requires_application", nullable = false)
    private boolean requiresApplication;
}