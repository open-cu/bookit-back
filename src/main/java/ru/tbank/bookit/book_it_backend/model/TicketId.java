package ru.tbank.bookit.book_it_backend.model;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Setter
@Getter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class TicketId implements java.io.Serializable {
    private int userId;
    private int areaId;
}
