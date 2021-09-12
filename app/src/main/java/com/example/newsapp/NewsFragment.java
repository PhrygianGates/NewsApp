package com.example.newsapp;

import static com.example.newsapp.NewsAdaptor.FAILED;
import static com.example.newsapp.NewsAdaptor.FINISHED;
import static com.example.newsapp.NewsAdaptor.HAS_MORE;

import android.annotation.SuppressLint;
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

import org.litepal.LitePal;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
                                loadNewData();
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
            //writeLog();
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
                        List<News> dataFromDatabase = getDataFromDatabase(6, -1);
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
            List<News> dataFromDatabase = getDataFromDatabase(6, -1);
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
                    List<News> newData = getDataFromDatabase(6, minIDInNewsList() - 1);
                    if (newData.isEmpty()) {
                        newsAdaptor.footerViewStatus = FINISHED;
                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                newsAdaptor.notifyItemChanged(newsAdaptor.getItemCount() - 1);
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
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        String dateString = simpleDateFormat.format(date);
        System.out.println(dateString);


        Request request = (new Request.Builder()).url("https://api2.newsminer.net/svc/news/queryNewsList?size=&startDate=&endDate=" + dateString + "&words=&categories=" + category).build();
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

    private List<News> getDataFromDatabase(int limitCount, long maxID) {
        if (maxID < 0) {
            return LitePal.where("category=?", category)
                    .order("id desc")
                    .limit(limitCount)
                    .find(News.class);
        } else {
            return LitePal.where("category=? and id<=?", category, Long.toString(maxID))
                    .order("id desc")
                    .limit(limitCount)
                    .find(News.class);
        }
    }

    private long minIDInNewsList() {
        if (newsList == null || newsList.isEmpty()) {
            return -1;
        } else {
            long minID = newsList.get(0).id;
            for (int i = 1; i < newsList.size(); i++) {
                long ID = newsList.get(i).id;
                if (ID < minID) {
                    minID = ID;
                }
            }
            return minID;
        }
    }

    private void insertNewsToDataBase() {
        try {
            for (int i = newsList.size() - 1; i >= 0; i--) {
                News news = newsList.get(i);
                List<News> resultList = LitePal.where("title=?", news.title).find(News.class);
                if (resultList == null || resultList.isEmpty()) {
                    news.save();
                } else {
                    news.id = resultList.get(0).id;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            requireActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {}
            });
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    private void replaceDataInRecyclerView(List<News> newData) {
        try {
            newsList.clear();;
            newsList.addAll(newData);
            newsAdaptor.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
