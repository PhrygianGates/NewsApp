package com.example.newsapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DisplayActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    List<News> newsList;
    MyAdaptor myAdaptor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_display);
        recyclerView = findViewById(R.id.news_recycle_view);
        newsList = new ArrayList<>();

        String startTime = getIntent().getStringExtra("startTime=");
        String endTime = getIntent().getStringExtra("endTime=");
        String category = getIntent().getStringExtra("category=");
        String word = getIntent().getStringExtra("word=");

        System.out.println("here!");
        Request request = (new Request.Builder()).url("https://api2.newsminer.net/svc/news/queryNewsList?size=&startDate=" + startTime + "&endDate="
                + endTime + "&words=" + word + "&categories=" + category).build();
        try {
            Response response = (new OkHttpClient()).newCall(request).execute();
            String json = Objects.requireNonNull(response.body()).string();
            NewsResponse newsResponse = (new Gson()).fromJson(json, NewsResponse.class);
            if (newsResponse != null) {
                try {
                    newsResponse.process();
                    newsList.clear();
                    newsList.addAll(newsResponse.data);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        myAdaptor = new MyAdaptor(newsList);
        recyclerView.setLayoutManager(new LinearLayoutManager(MyApplication.context));
        recyclerView.setAdapter(myAdaptor);
    }
}
