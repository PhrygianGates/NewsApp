package com.example.newsapp;

import java.util.Arrays;
import java.util.List;

class News {
    String title;
    String publisher;
    String publishTime;
    String image;
    String content;

    List<String> images;
    public void process() {
        image = image.substring(1, image.length() - 1);
        if (image == "") {
            images.add("");
            return;
        }
        images = Arrays.asList(image.split(", "));
    }
}

class NewsResponse {
    int pageSize;
    int total;
    List<News> data;
    int currentPage;
    public void process() {
        for (News news : data) {
            news.process();
        }
    }
}
