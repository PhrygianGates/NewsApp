package com.example.newsapp;

import org.litepal.crud.LitePalSupport;

import java.util.ArrayList;
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
    String video;

    List<String> images;
    public void process() {
        images = new ArrayList<>();
        if (image.length() <= 2) {
            image = "";
            images.add("https://th.bing.com/th/id/OIP.F0l-uBZ7P7BSiifS_ZIRRQAAAA?pid=ImgDet&rs=1");
            return;
        }
        image = image.substring(1, image.length() - 1);
        if (image == "") {
            images.add("https://th.bing.com/th/id/OIP.F0l-uBZ7P7BSiifS_ZIRRQAAAA?pid=ImgDet&rs=1");
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

class HistoryLog extends LitePalSupport {
    long id;
    long historyID;
    public HistoryLog(long historyID) {
        this.historyID = historyID;
    }
}

class MarkLog extends LitePalSupport {
    long id;
    long markID;
    public MarkLog(long markID) {
        this.markID = markID;
    }
}
