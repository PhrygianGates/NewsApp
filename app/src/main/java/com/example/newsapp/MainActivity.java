package com.example.newsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    List<News> newsList = new ArrayList<>();
    RecyclerView newsRecycleView;

    private void refresh() {
        Thread t = new Thread(() -> {
            Request.Builder rb = new Request.Builder();
            Request request = rb.url("https://api2.newsminer.net/svc/news/queryNewsList?size=15&startDate=2021-08-20&endDate=2021-08-30&words=拜登&categories=科技").build();
            OkHttpClient client = new OkHttpClient();
            try (Response response = client.newCall(request).execute()) {
                String json = Objects.requireNonNull(response.body()).string();
                Gson gson = new Gson();
                NewsResponse newsResponse = gson.fromJson(json, NewsResponse.class);
                if (newsResponse != null) {
                    List<News> data = newsResponse.data;
                    newsList.clear();
                    newsList.addAll(data);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Objects.requireNonNull(newsRecycleView.getAdapter()).notifyDataSetChanged();
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        t.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        newsRecycleView = findViewById(R.id.news_recycle_view);
        newsRecycleView.setLayoutManager(new LinearLayoutManager(this));
        newsRecycleView.setAdapter(new NewsAdaptor());
        refresh();
    }

    private class NewsAdaptor extends RecyclerView.Adapter<NewsViewHolder> {

        @NonNull
        @Override
        public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new NewsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item_one_image, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
            News news = newsList.get(position);
            holder.title.setText(news.title);
            holder.description.setText(news.publishTime);
            Glide.with(MainActivity.this).load(news.image).into(holder.image);
            holder.itemView.setOnClickListener(
                    view -> Toast.makeText(MainActivity.this, "hello!", Toast.LENGTH_SHORT).show()
            );
        }

        @Override
        public int getItemCount() {
            return newsList.size();
        }
    }

    private class NewsViewHolder extends RecyclerView.ViewHolder {
        TextView title = itemView.findViewById(R.id.news_title);
        TextView description = itemView.findViewById(R.id.news_description);
        ImageView image = itemView.findViewById(R.id.news_image);

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}