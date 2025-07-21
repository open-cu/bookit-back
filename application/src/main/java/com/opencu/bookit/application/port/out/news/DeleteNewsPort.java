package com.opencu.bookit.application.port.out.news;

import java.util.UUID;

public interface DeleteNewsPort {
    void delete(UUID newsId);
}
