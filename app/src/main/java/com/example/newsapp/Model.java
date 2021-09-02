package com.example.newsapp;

import java.util.List;

class News {
    String title;
    String publishTime;
    String image;
    String url;

    public News(String title, String publishTime, String image, String url) {
        this.title = title;
        this.publishTime = publishTime;
        this.image = image;
        this.url = url;
    }
}

class NewsResponse {
    int pageSize;
    int total;
    List<News> data;
    int currentPage;
}
