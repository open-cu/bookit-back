package ru.tbank.bookit.book_it_backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "TICKETS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {

   @EmbeddedId
   private TicketId id;

    @Column(nullable = false)
    private long type;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}