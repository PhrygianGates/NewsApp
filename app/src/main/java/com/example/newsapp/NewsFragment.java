package com.example.newsapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NewsFragment extends Fragment {

    List<News> newsList = new ArrayList<>();
    RecyclerView newsRecycleView;
    String category;
    SwipeRefreshLayout refresh;

    public NewsFragment(String category) {
        super();
        this.category = category;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        newsRecycleView = view.findViewById(R.id.news_recycle_view);
        refresh = view.findViewById(R.id.news_refresh);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        newsRecycleView.setLayoutManager(new LinearLayoutManager(MyApplication.context));
        newsRecycleView.setAdapter(new NewsAdaptor());
        refresh();
        refresh.setColorSchemeColors(Color.parseColor("#03A9F4"));
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(700);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                refresh();
                                refresh.setRefreshing(false);
                            }
                        });
                    }
                };
                Thread t = new Thread(r);
                t.start();
            }
        });
    }

    private void refresh() {
        Thread t = new Thread(() -> {
            Request.Builder rb = new Request.Builder();
            Request request = rb.url("https://api2.newsminer.net/svc/news/queryNewsList?size=15&startDate=2021-01-01&endDate=2021-08-31&words=&categories=" + category).build();
            OkHttpClient client = new OkHttpClient();
            try (Response response = client.newCall(request).execute()) {
                String json = Objects.requireNonNull(response.body()).string();
                Gson gson = new Gson();
                NewsResponse newsResponse = gson.fromJson(json, NewsResponse.class);
                if (newsResponse != null) {
                    newsResponse.process();
                    List<News> data = newsResponse.data;
                    newsList.clear();
                    newsList.addAll(data);
                    requireActivity().runOnUiThread(new Runnable() {
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
            holder.description.setText(news.publisher);
            if (news.images.get(0).isEmpty()) {
                Glide.with(MyApplication.context).load("https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fotowabi.com%2Fwp-content%2Fthemes%2Flionblog%2Fimg%2Fimg_no.gif&refer=http%3A%2F%2Fotowabi.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1633175286&t=2b3b38287bcd040f527a50ed1a589215").into(holder.image);
            } else {
                Glide.with(MyApplication.context).load(news.images.get(0)).into(holder.image);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MyApplication.context, DetailActivity.class);
                    intent.putExtra("url=", news.url);
                    startActivity(intent);
                }
            });
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
