package com.opencu.bookit.domain.model.area;

import com.opencu.bookit.domain.model.user.User;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    private String id;
    private User user;
    private byte rating;
    private String comment;
    private LocalDateTime createdAt;
}
