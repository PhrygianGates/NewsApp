package com.example.newsapp;

import static com.example.newsapp.NewsAdaptor.FAILED;
import static com.example.newsapp.NewsAdaptor.FINISHED;
import static com.example.newsapp.NewsAdaptor.HAS_MORE;

import android.graphics.Color;
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

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NewsFragment extends Fragment {

    List<News> newsList = new ArrayList<>();
    RecyclerView newsRecycleView;
    String category;
    SwipeRefreshLayout swipeRefreshLayout;
    NewsAdaptor newsAdaptor = new NewsAdaptor(newsList, this);
    boolean isLoading = false;

    public NewsFragment(String category) {
        super();
        this.category = category;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        newsRecycleView = view.findViewById(R.id.news_recycle_view);
        swipeRefreshLayout = view.findViewById(R.id.news_refresh);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        newsRecycleView.setLayoutManager(new LinearLayoutManager(MyApplication.context));
        newsRecycleView.setAdapter(newsAdaptor);
        loadNewData();
        swipeRefreshLayout.setColorSchemeColors(Color.parseColor("#03A9F4"));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
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
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        });
                    }
                };
                Thread t = new Thread(r);
                t.start();
            }
        });
        newsRecycleView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy < 0) return;
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int position = layoutManager.findLastVisibleItemPosition();
                if (position == newsAdaptor.getItemCount() - 1) {
                    loadCacheData();
                }
            }
        });
    }

    private void loadNewData() {
        if (isLoading) return;
        isLoading = true;
        if (NetworkUtil.isNetworkAvailable(MyApplication.context)) {
            writeLog();
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    List<News> dataFromNetwork = getDataFromNetwork();
                    if (dataFromNetwork != null && !dataFromNetwork.isEmpty()) {
                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                replaceDataInRecyclerView(dataFromNetwork);
                                Runnable rr = new Runnable() {
                                    @Override
                                    public void run() {
                                        insertNewsToDataBase();
                                    }
                                };
                                Thread tt = new Thread(rr);
                                tt.run();
                                newsAdaptor.footerViewStatus = HAS_MORE;
                                isLoading = false;
                            }
                        });
                    } else {
                        List<News> dataFromDatabase = getDataFromDatabase(6);
                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                replaceDataInRecyclerView(dataFromDatabase);
                                newsAdaptor.footerViewStatus = HAS_MORE;
                                isLoading = false;
                            }
                        });
                    }
                }
            };
            Thread t = new Thread(r);
            t.start();
        } else {
            List<News> dataFromDatabase = getDataFromDatabase(6);
            requireActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    replaceDataInRecyclerView(dataFromDatabase);
                    newsAdaptor.footerViewStatus = HAS_MORE;
                    isLoading = false;
                }
            });
        }
    }

    public void loadCacheData() {
        if (isLoading) return;
        if (newsAdaptor.footerViewStatus != HAS_MORE) return;
        isLoading = true;
        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    List<News> newData = getDataFromDatabase(6, minIdInNewsList() - 1);
                    if (newData.isEmpty()) {
                        newsAdaptor.footerViewStatus = FINISHED;
                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                newsAdaptor.notifyItemChanged(newsAdaptor.itemCount - 1);
                                isLoading = false;
                            }
                        });
                    } else {
                        List<News> list = new ArrayList<>(newsList);
                        list.addAll(newData);
                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                replaceDataInRecyclerView(list);
                                isLoading = false;
                            }
                        });

                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    newsAdaptor.footerViewStatus = FAILED;
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            newsAdaptor.notifyItemChanged(newsAdaptor.getItemCount() - 1);
                            isLoading = false;
                        }
                    });
                }
            }
        };
        Thread t = new Thread(r);
        t.start();
    }

    private List<News> getDataFromNetwork() {
        List<News> list = null;
        Request request = (new Request.Builder()).url("https://api2.newsminer.net/svc/news/queryNewsList?size=15&startDate=2021-01-01&endDate=2021-08-31&words=&categories=" + category).build();
        try {
            Response response = (new OkHttpClient()).newCall(request).execute();
            String json = Objects.requireNonNull(response.body()).string();
            NewsResponse newsResponse = (new Gson()).fromJson(json, NewsResponse.class);
            if (newsResponse != null) {
                try {
                    newsResponse.process();
                    list = newsResponse.data;
                } catch (Exception e) {
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {}
                    });
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            requireActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {}
            });
        }
        return list;
    }


}
