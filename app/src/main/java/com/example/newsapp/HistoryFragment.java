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
        myAdaptor = new MyAdaptor(newsList);
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
                List<HistoryLog> historyList = LitePal.order("id desc").find(HistoryLog.class);
                newsList.clear();
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



}
