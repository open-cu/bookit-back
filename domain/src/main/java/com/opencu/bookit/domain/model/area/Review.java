package com.opencu.bookit.domain.model.area;

import com.opencu.bookit.domain.model.user.UserModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    private String id;
    private UserModel userModel;
    private byte rating;
    private String comment;
    private LocalDateTime createdAt;
}
