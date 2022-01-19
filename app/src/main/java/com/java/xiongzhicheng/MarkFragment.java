package com.java.xiongzhicheng;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class MarkFragment extends Fragment {
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    MyAdaptor myAdaptor;
    List<News> newsList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mark, container, false);
        recyclerView = view.findViewById(R.id.mark_recycler_view);
        swipeRefreshLayout = view.findViewById(R.id.mark_refresh);
        newsList = new ArrayList<>();
        List<MarkLog> markLogList = LitePal.order("id desc").find(MarkLog.class);
        for (MarkLog markLog : markLogList) {
            List<News> thisNews = LitePal.where("id=?", String.valueOf(markLog.markID)).find(News.class);
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
                List<MarkLog> markList = LitePal.order("id desc").find(MarkLog.class);
                newsList.clear();
                for (MarkLog markLog : markList) {
                    List<News> thisNews = LitePal.where("id=?", String.valueOf(markLog.markID)).find(News.class);
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

