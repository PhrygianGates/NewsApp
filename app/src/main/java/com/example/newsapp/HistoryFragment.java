package com.example.newsapp;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    MyAdaptor myAdaptor;
    List<News> newsList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LitePal.deleteAll(HistoryLog.class);
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        recyclerView = view.findViewById(R.id.history_recycler_view);
        swipeRefreshLayout = view.findViewById(R.id.history_refresh);
        newsList = new ArrayList<>();
        List<HistoryLog> historyList = LitePal.order("id desc").find(HistoryLog.class);
        for (HistoryLog historyLog : historyList) {
            List<News> thisNews = LitePal.where("id=?", String.valueOf(historyLog.historyID)).find(News.class);
            if (thisNews != null && !thisNews.isEmpty() && !newsList.contains(thisNews.get(0))) {
                newsList.add(thisNews.get(0));
            }
        }
        myAdaptor = new MyAdaptor();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recyclerView.setLayoutManager(new LinearLayoutManager(MyApplication.context));
        recyclerView.setAdapter(myAdaptor);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                newsList = new ArrayList<>();
                List<HistoryLog> historyList = LitePal.order("id desc").find(HistoryLog.class);
                for (HistoryLog historyLog : historyList) {
                    List<News> thisNews = LitePal.where("id=?", String.valueOf(historyLog.historyID)).find(News.class);
                    if (thisNews != null && !thisNews.isEmpty() && !newsList.contains(thisNews.get(0))) {
                        newsList.add(thisNews.get(0));
                    }
                }
                myAdaptor.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    class MyAdaptor extends RecyclerView.Adapter<MyViewHolder> {

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
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return newsList.size();
        }
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
