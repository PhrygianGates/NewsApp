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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    List<News> newsList = new ArrayList<>(Arrays.asList(new News("飓风“艾达”即将登陆美国墨西哥湾沿岸_1", "2021-08-29 07:30:00", "https://static.cnbetacdn.com/thumb/article/2021/0829/0c7c5fafc146c34.jpg"),
            new News("飓风“艾达”即将登陆美国墨西哥湾沿岸_2", "2021-08-29 07:30:00", "https://static.cnbetacdn.com/thumb/article/2021/0829/0c7c5fafc146c34.jpg")));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView newsRecycleView = findViewById(R.id.news_recycle_view);
        newsRecycleView.setLayoutManager(new LinearLayoutManager(this));
        newsRecycleView.setAdapter(new NewsAdaptor(newsList));

    }

    private class NewsAdaptor extends RecyclerView.Adapter<NewsViewHolder> {
        List<News> newsList;

        public NewsAdaptor(List<News> newsList) {
            super();
            this.newsList = newsList;
        }

        @NonNull
        @Override
        public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new NewsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item_one_image, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
            News news = newsList.get(position);
            holder.title.setText(news.title);
            holder.description.setText(news.description);
            Glide.with(MainActivity.this).load(news.imageUrl).into(holder.image);
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