package com.example.newsapp;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class MyAdaptor extends RecyclerView.Adapter<MyAdaptor.MyViewHolder> {
    List<News> newsList;

    public MyAdaptor(List<News> newsList) {
        super();
        this.newsList = newsList;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item_one_image, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        News news = newsList.get(position);
        holder.title.setText(news.title);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(news.publisher).append("    ").append(news.publishTime);
        holder.description.setText(stringBuilder.toString());
        Glide.with(MyApplication.context).load(news.images.get(0)).into(holder.image);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyApplication.context, DetailActivity.class);
                News currentNews = newsList.get(holder.getAdapterPosition());
                intent.putExtra("content=", currentNews.content);
                intent.putExtra("title=", currentNews.title);
                intent.putExtra("publishTime=", currentNews.publishTime);
                intent.putExtra("publisher=", currentNews.publisher);
                intent.putExtra("image=", currentNews.image);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(MyApplication.context, intent, null);
            }
        });
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView description;
        ImageView image;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.news_title);
            description = itemView.findViewById(R.id.news_description);
            image = itemView.findViewById(R.id.news_image);
        }
    }
}

