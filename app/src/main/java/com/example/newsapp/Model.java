package com.example.newsapp;

import org.litepal.crud.LitePalSupport;

import java.util.Arrays;
import java.util.List;

class News extends LitePalSupport {
    long id;
    String newsID;
    String title;
    String publisher;
    String publishTime;
    String image;
    String content;
    String category;

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
