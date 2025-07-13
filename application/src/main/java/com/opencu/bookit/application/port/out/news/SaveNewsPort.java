package com.opencu.bookit.application.port.out.news;

import com.opencu.bookit.domain.model.news.NewsModel;

public interface SaveNewsPort {
    NewsModel save(NewsModel newsModel);
}
